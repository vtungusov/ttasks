package com.siberteam.vtungusov.vocabulary.handler;

import org.junit.Test;

import java.io.IOException;

public class AnagramerTest {
    public static final String INPUT = "anagrammer_test.txt";
    private final Anagramer anagram = new Anagramer();

    @Test
    public void ShouldRetrieveAnagramList() throws IOException {
        anagram.findAnagrams(INPUT);
    }
}