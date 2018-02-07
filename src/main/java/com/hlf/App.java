package com.hlf;

import com.hlf.ga.FitnessFunction;
import com.hlf.ga.GA;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        FitnessFunction[] funcs = {
                str -> -(Math.abs(str.length() - 30) * 2),
                str -> StringUtils.countMatches(str, "a") * 2,
                str -> StringUtils.countMatches(str, "b") * 2,
                str -> StringUtils.countMatches(str, "ab") * 4,
        };

        FitnessFunction fitnessFunction =
                (str) ->
                        Arrays.stream(funcs)
                                .mapToInt(func -> func.apply(str))
                                .sum();


        GA ga = new GA();

        ga.runGA(fitnessFunction);
    }
}
