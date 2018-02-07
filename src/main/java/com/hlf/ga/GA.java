package com.hlf.ga;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class GA {

    final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";
    Random random = new Random();

    String generate() {
        return generate(random.nextInt(15) + 10);
    }

    String generate(int length) {
        return IntStream.rangeClosed(1, length)
                .boxed()
                .map( i -> ALPHABET.charAt(random.nextInt(ALPHABET.length())) + "")
                .collect(Collectors.joining());
    }

    List<String> generateMultiple(int n) {
        return IntStream.rangeClosed(1, n)
                .boxed()
                .map(i -> generate())
                .collect(Collectors.toList());
    }

    String crossover(String parent1, String parent2) {
        int point1 = random.nextInt(parent1.length());
        int point2 = random.nextInt(parent2.length());
        return parent1.substring(0, point1) + parent2.substring(point2, parent2.length());
    }

    List<String> crossoverPools(int n, List<String> pool1, List<String> pool2) {
        return IntStream.rangeClosed(1, n)
                .boxed()
                .map(i -> {
                    String parent1 = pool1.get(random.nextInt(pool1.size()));
                    String parent2 = pool2.get(random.nextInt(pool2.size()));
                    return random.nextFloat() < .5 ?
                    crossover(parent1, parent2) : crossover(parent2, parent2);
                })
                .collect(Collectors.toList());
    }


    String mutate(String input, float mutationRate) {
        return input.chars()
                .mapToObj(i -> (char) i)
                .map(c -> random.nextFloat() < mutationRate ? "" + ALPHABET.charAt(random.nextInt(ALPHABET.length())) : "" +c)
                .collect(Collectors.joining());
    }

    List<String> makeNextGen(List<String> population, FitnessFunction func, float mutationRate) {
        Comparator<String> comparator= new FitnessComparator(func);
        population.sort(comparator);
        int twentyPercent = Math.round(population.size() * .2f);

        List<String> best = population.subList(0, twentyPercent);
        List<String> rest = population.subList(twentyPercent, population.size());

        List<String> offspring = crossoverPools(population.size() -twentyPercent, best, rest)
                .stream()
                .map(str -> mutate(str, mutationRate))
                .collect(Collectors.toList());

        List<String> nextGen = new ArrayList<>();
        nextGen.add(best.get(0)); // keep the best one
        nextGen.addAll(offspring);
        List<String> newBlood = generateMultiple(population.size() - nextGen.size());
        nextGen.addAll(newBlood);

        return nextGen;
    }

    public void runGA(FitnessFunction fitnessFunction,
                      int popultionSize,
                      int maxGenerations,
                      int resultFrequency,
                      float mutationRate) {
        List<String> population = generateMultiple(popultionSize);
        for (int i = 0; i < maxGenerations; i++) {

            population = makeNextGen(population, fitnessFunction, mutationRate);

            if (i % resultFrequency == 0) {
                String best = population.get(0);
                int score = fitnessFunction.apply(best);
                System.out.println(score + " " + best + " " + i + " of " + maxGenerations );
            }
        }
        String finalBest = population.get(0);
        int finalScore = fitnessFunction.apply(finalBest);
        System.out.println(finalScore + " " + finalBest );
    }

    public void runGA(FitnessFunction fitnessFunction) {
        runGA(fitnessFunction, 500, 10000, 50, .01f);
    }

    class FitnessComparator implements Comparator<String> {
        FitnessComparator(FitnessFunction fitnessFunction) {
            this.fitnessFunction = fitnessFunction;
        }

        final FitnessFunction fitnessFunction;

        @Override
        public int compare(String s, String t1) {
            return this.fitnessFunction.apply(t1) - this.fitnessFunction.apply(s);
        }
    }
}
