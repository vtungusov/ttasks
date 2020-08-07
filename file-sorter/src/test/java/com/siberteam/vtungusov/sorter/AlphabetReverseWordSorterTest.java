package com.siberteam.vtungusov.sorter;

import com.siberteam.vtungusov.filesorter.SortedPair;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AlphabetReverseWordSorterTest extends TestCase {
    private final AlphabetReverseWordSorter sorter = new AlphabetReverseWordSorter();
    private Stream<SortedPair> input;

    @BeforeClass
    public void setUp() {
        input = Stream.of(
                new SortedPair("ab", "ab"),
                new SortedPair("lambada", "lambada"),
                new SortedPair("cool", "cool")
        );
    }

    @Test
    public void testShouldSortByAsc() {
        //given
        List<String> expect = Arrays.asList(
                "lambada (adabmal)",
                "ab (ba)",
                "cool (looc)"
        );
        //when
        List<String> actual = sorter.sort(input, false)
                .collect(Collectors.toList());
        //then
        Assert.assertEquals(expect, actual);
    }

    @Test
    public void testShouldSortByDesc() {
        //given
        List<String> expect = Arrays.asList(
                "cool (looc)",
                "ab (ba)",
                "lambada (adabmal)"
        );
        //when
        List<String> actual = sorter.sort(input, true)
                .collect(Collectors.toList());
        //then
        Assert.assertEquals(expect, actual);
    }
}