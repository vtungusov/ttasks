package com.siberteam.vtungusov.sorter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RandomSorterTest {
    private final Sorter fSorter = new RandomSorter(new FrequencySorter());
    private final Sorter abrSorter = new RandomSorter(new AlphabetReverseWordSorter());
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
                "ab (1)",
                "cool (1)",
                "lambada (2)"
        );
        List<String> exp2 = Arrays.asList(
                "pop (1)",
                "cft (2)",
                "18th (3)"
        );

        List<String> act1 = fSorter.sort(in1, SortDirection.ASC)
                .collect(Collectors.toList());
        List<String> act2 = fSorter.sort(in2, SortDirection.ASC)
                .collect(Collectors.toList());

        Assert.assertEquals(exp1, act1);
        Assert.assertEquals(exp2, act2);
    }

    @Test
    public void testShouldSortByDesc() {
        List<String> exp1 = Arrays.asList(
                "lambada (2)",
                "ab (1)",
                "cool (1)"
        );
        List<String> exp2 = Arrays.asList(
                "18th (3)",
                "cft (2)",
                "pop (1)"
        );

        List<String> act1 = fSorter.sort(in1, SortDirection.DESC)
                .collect(Collectors.toList());
        List<String> act2 = fSorter.sort(in2, SortDirection.DESC)
                .collect(Collectors.toList());

        Assert.assertEquals(exp1, act1);
        Assert.assertEquals(exp2, act2);
    }

    @Test
    public void shouldAlphabetReverseWordSortByAsc() {
        List<String> exp1 = Arrays.asList(
                "lambada (adabmal)",
                "ab (ba)",
                "cool (looc)"
        );
        List<String> exp2 = Arrays.asList(
                "18th (ht81)",
                "pop (pop)",
                "cft (tfc)"
        );

        List<String> act1 = abrSorter.sort(in1, SortDirection.ASC)
                .collect(Collectors.toList());
        List<String> act2 = abrSorter.sort(in2, SortDirection.ASC)
                .collect(Collectors.toList());

        Assert.assertEquals(exp1, act1);
        Assert.assertEquals(exp2, act2);
    }

    @Test
    public void shouldAlphabetReverseWordSortByDesc() {
        List<String> exp1 = Arrays.asList(
                "cool (looc)",
                "ab (ba)",
                "lambada (adabmal)"
        );
        List<String> exp2 = Arrays.asList(
                "cft (tfc)",
                "pop (pop)",
                "18th (ht81)"
        );

        List<String> act1 = abrSorter.sort(in1, SortDirection.DESC)
                .collect(Collectors.toList());
        List<String> act2 = abrSorter.sort(in2, SortDirection.DESC)
                .collect(Collectors.toList());

        Assert.assertEquals(exp1, act1);
        Assert.assertEquals(exp2, act2);
    }
}