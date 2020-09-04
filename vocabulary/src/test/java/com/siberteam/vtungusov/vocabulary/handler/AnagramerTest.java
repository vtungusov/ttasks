package com.siberteam.vtungusov.vocabulary.handler;

import com.siberteam.vtungusov.vocabulary.model.Word;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class AnagramerTest {
    private final Anagramer anagram = new Anagramer();

    @Test
    public void ShouldShowIfWordsIsAnagram() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method isAnagram = anagram.getClass().getDeclaredMethod("isAnagram", Word.class, Word.class);
        isAnagram.setAccessible(true);

        Object aTrue = isAnagram
                .invoke(anagram, new Word("sos"), new Word("oss"));
        Assert.assertTrue((Boolean) aTrue);

        Object aTrue2 = isAnagram
                .invoke(anagram, new Word("saas"), new Word("assa"));
        Assert.assertTrue((Boolean) aTrue2);

        Object aFalse = isAnagram
                .invoke(anagram, new Word("zalur"), new Word("lazrt"));
        Assert.assertFalse((Boolean) aFalse);

        Object aFalse2 = isAnagram
                .invoke(anagram, new Word("azalar"), new Word("alaz"));
        Assert.assertFalse((Boolean) aFalse2);
    }
}