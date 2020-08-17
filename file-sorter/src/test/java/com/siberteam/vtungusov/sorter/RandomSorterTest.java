package com.siberteam.vtungusov.sorter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RandomSorterTest {
    private Random mockRandom;
    private Sorter sorter;
    private Stream<String> in1;
    private Stream<String> in2;

    @Before
    public void setUp() {
        mockRandom = Mockito.mock(Random.class);
        sorter = new RandomSorter(mockRandom);
        in1 = Stream.of(
                "ab", "lambada", "cool", "lambada"
        );
        in2 = Stream.of(
                "cft", "18th", "cft", "18th", "18th", "pop"
        );
    }

    @Test
    public void shouldSortIn1() {
        List<String> expected = Arrays.asList(
                "lambada (1.0)",
                "ab (2.0)",
                "cool (8.0)"
        );

        Mockito.when(mockRandom.nextDouble()).thenReturn(0.2d, 0.1d, 0.8d);
        List<String> act1 = sorter.sort(in1, SortDirection.ASC)
                .collect(Collectors.toList());

        Assert.assertEquals(expected, act1);
    }

    @Test
    public void shouldSortIn2() {
        List<String> expected = Arrays.asList(
                "18th (1.2)",
                "pop (5.0)",
                "cft (33.0)"
        );

        Mockito.when(mockRandom.nextDouble()).thenReturn(3.3d, 0.12d, 0.5d);
        List<String> act2 = sorter.sort(in2, SortDirection.ASC)
                .collect(Collectors.toList());

        Assert.assertEquals(expected, act2);
    }
}