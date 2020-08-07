package com.siberteam.vtungusov.filesorter;

import javafx.util.Pair;

public class SortedPair extends Pair<String, String> implements Comparable<Pair<String, String>> {
    /**
     * Creates a new pair
     *
     * @param key   The key for this pair
     * @param value The value to use for this pair
     */
    public SortedPair(String key, String value) {
        super(key, value);
    }

    @Override
    public String toString() {
        return this.getKey() + " (" + this.getValue() + ")";
    }

    @Override
    public int compareTo(Pair<String, String> o) {
        return this.getValue().compareTo(o.getValue());
    }
}
