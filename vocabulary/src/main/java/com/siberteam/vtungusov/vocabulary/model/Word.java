package com.siberteam.vtungusov.vocabulary.model;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Word {
    private final String wordName;
    private boolean handled;
    private final List<Word> anagrams = new CopyOnWriteArrayList<>();

    public Word(String wordName) {
        this.wordName = wordName;
    }

    public String getWord() {
        return wordName;
    }

    public List<Word> getAnagrams() {
        return anagrams;
    }

    public void addAnagram(Word word) {
        anagrams.add(word);
    }

    public void addAnagram(List<Word> words) {
        anagrams.addAll(words);
    }

    public boolean isHandled() {
        return handled;
    }

    public void setHandled() {
        this.handled = true;
    }
}
