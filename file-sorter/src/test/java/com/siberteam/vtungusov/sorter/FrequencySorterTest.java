package com.siberteam.vtungusov.sorter;

import com.siberteam.vtungusov.ui.BadArgumentsException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FrequencySorterTest {
    private final Sorter sorter = new FrequencySorter();
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
    public void testShouldSortByAsc() throws BadArgumentsException {
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

        List<String> act1 = sorter.sort(in1, SortDirection.ASC)
                .collect(Collectors.toList());
        List<String> act2 = sorter.sort(in2, SortDirection.ASC)
                .collect(Collectors.toList());

        Assert.assertEquals(exp1, act1);
        Assert.assertEquals(exp2, act2);
    }

    @Test
    public void testShouldSortByDesc() throws BadArgumentsException {
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

        List<String> act1 = sorter.sort(in1, SortDirection.DESC)
                .collect(Collectors.toList());
        List<String> act2 = sorter.sort(in2, SortDirection.DESC)
                .collect(Collectors.toList());

        Assert.assertEquals(exp1, act1);
        Assert.assertEquals(exp2, act2);
    }
}