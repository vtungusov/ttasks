package com.siberteam.vtungusov.sorter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RandomSorterTest {
    private final RandomSorter mockSorter = Mockito.spy(RandomSorter.class);
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
    public void shouldSortIn1() {
        List<String> exp1 = Arrays.asList(
                "ab (0.0)",
                "lambada (0.0)",
                "cool (0.0)"
        );

        Mockito.when(mockSorter.getSortFeature(Mockito.anyString())).thenReturn(0d);
        List<String> act1 = mockSorter.sort(in1, SortDirection.ASC)
                .collect(Collectors.toList());

        Assert.assertEquals(exp1, act1);
    }

    @Test
    public void shouldSortIn2() {
        List<String> exp2 = Arrays.asList(
                "cft (1.0)",
                "18th (1.0)",
                "pop (1.0)"
        );

        Mockito.when(mockSorter.getSortFeature(Mockito.anyString())).thenReturn(1d);
        List<String> act2 = mockSorter.sort(in2, SortDirection.ASC)
                .collect(Collectors.toList());

        Assert.assertEquals(exp2, act2);
    }
}