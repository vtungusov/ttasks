package com.siberteam.vtungusov.sorter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AlphabetReverseWordSorterTest {
    private final Sorter sorter = new AlphabetReverseWordSorter();
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
                "lambada (adabmal)",
                "ab (ba)",
                "cool (looc)"
        );

        List<String> act1 = sorter.sort(in1, SortDirection.ASC)
                .collect(Collectors.toList());

        Assert.assertEquals(exp1, act1);
    }

    @Test
    public void shouldSortByAscIn2() {
        List<String> exp2 = Arrays.asList(
                "18th (ht81)",
                "pop (pop)",
                "cft (tfc)"
        );

        List<String> act2 = sorter.sort(in2, SortDirection.ASC)
                .collect(Collectors.toList());

        Assert.assertEquals(exp2, act2);
    }

    @Test
    public void shouldSortByDescIn1() {
        List<String> exp1 = Arrays.asList(
                "cool (looc)",
                "ab (ba)",
                "lambada (adabmal)"
        );

        List<String> act1 = sorter.sort(in1, SortDirection.DESC)
                .collect(Collectors.toList());

        Assert.assertEquals(exp1, act1);
    }

    @Test
    public void shouldSortByDescIn2() {
        List<String> exp2 = Arrays.asList(
                "cft (tfc)",
                "pop (pop)",
                "18th (ht81)"
        );

        List<String> act2 = sorter.sort(in2, SortDirection.DESC)
                .collect(Collectors.toList());

        Assert.assertEquals(exp2, act2);
    }
}