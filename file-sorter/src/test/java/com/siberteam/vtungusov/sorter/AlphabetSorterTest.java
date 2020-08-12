package com.siberteam.vtungusov.sorter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AlphabetSorterTest {
    private final Sorter sorter = new AlphabetSorter();
    private Stream<String> in1;
    private Stream<String> in2;

    @Before
    public void setUp() {
        in1 = Stream.of(
                "ab", "lambada", "cool", "lambada"
        );
        in2 = Stream.of(
                "cft", "18th", "cft", "18th", "18th", "pop"
        );
    }

    @Test
    public void testShouldSortByAsc() {
        List<String> exp1 = Arrays.asList(
                "ab (ab)",
                "cool (cool)",
                "lambada (lambada)"
        );
        List<String> exp2 = Arrays.asList(
                "18th (18th)",
                "cft (cft)",
                "pop (pop)"
        );

        List<String> act1 = sorter.sort(in1, false)
                .collect(Collectors.toList());
        List<String> act2 = sorter.sort(in2, false)
                .collect(Collectors.toList());

        Assert.assertEquals(exp1, act1);
        Assert.assertEquals(exp2, act2);
    }

    @Test
    public void testShouldSortByDesc() {
        List<String> exp1 = Arrays.asList(
                "lambada (lambada)",
                "cool (cool)",
                "ab (ab)"
        );
        List<String> exp2 = Arrays.asList(
                "pop (pop)",
                "cft (cft)",
                "18th (18th)"
        );

        List<String> act1 = sorter.sort(in1, true)
                .collect(Collectors.toList());
        List<String> act2 = sorter.sort(in2, true)
                .collect(Collectors.toList());

        Assert.assertEquals(exp1, act1);
        Assert.assertEquals(exp2, act2);
    }
}