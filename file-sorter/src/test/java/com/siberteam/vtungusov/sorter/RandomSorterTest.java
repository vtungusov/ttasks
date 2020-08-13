package com.siberteam.vtungusov.sorter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RandomSorterTest {
    private final Sorter mockedSorter = new RandomSorter(true);
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
    public void shouldSort() {
        List<String> exp1 = Arrays.asList(
                "ab (0.0)",
                "lambada (0.0)",
                "cool (0.0)"
        );
        List<String> exp2 = Arrays.asList(
                "cft (0.0)",
                "18th (0.0)",
                "pop (0.0)"
        );

        List<String> act1 = mockedSorter.sort(in1, SortDirection.ASC)
                .collect(Collectors.toList());
        List<String> act2 = mockedSorter.sort(in2, SortDirection.ASC)
                .collect(Collectors.toList());

        Assert.assertEquals(exp1, act1);
        Assert.assertEquals(exp2, act2);
    }
}