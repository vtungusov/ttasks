package com.siberteam.vtungusov.sorter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WordLengthSorterTest {
    private final Sorter sorter = new WordLengthSorter();
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
    public void shouldSortByAscIn1() {
        List<String> exp1 = Arrays.asList(
                "ab (2)",
                "cool (4)",
                "lambada (7)"
        );

        List<String> act1 = sorter.sort(in1, SortDirection.ASC)
                .collect(Collectors.toList());

        Assert.assertEquals(exp1, act1);
    }

    @Test
    public void shouldSortByAscIn2() {
        List<String> exp2 = Arrays.asList(
                "cft (3)",
                "pop (3)",
                "18th (4)"
        );

        List<String> act2 = sorter.sort(in2, SortDirection.ASC)
                .collect(Collectors.toList());

        Assert.assertEquals(exp2, act2);
    }

    @Test
    public void shouldSortByDescIn1() {
        List<String> exp1 = Arrays.asList(
                "lambada (7)",
                "cool (4)",
                "ab (2)"
        );

        List<String> act1 = sorter.sort(in1, SortDirection.DESC)
                .collect(Collectors.toList());

        Assert.assertEquals(exp1, act1);
    }

    @Test
    public void shouldSortByDescIn2() {
        List<String> exp2 = Arrays.asList(
                "18th (4)",
                "cft (3)",
                "pop (3)"
        );

        List<String> act2 = sorter.sort(in2, SortDirection.DESC)
                .collect(Collectors.toList());

        Assert.assertEquals(exp2, act2);
    }
}