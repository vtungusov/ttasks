package com.siberteam.vtungusov.model;

import java.util.Map;
import java.util.Objects;

public class PairEntry<V extends Comparable<V>> implements Map.Entry<String, V>, Comparable<PairEntry<V>> {
    private final String key;
    private V value;

    public PairEntry(String key, V value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public V getValue() {
        return value;
    }

    @Override
    public V setValue(V value) {
        this.value = value;
        return value;
    }

    @Override
    public int compareTo(PairEntry<V> o) {
        return this.value.compareTo(o.value);
    }

    public boolean equals(Object o) {
        if (!(o instanceof Map.Entry))
            return false;
        Map.Entry<?, ?> e = (Map.Entry<?, ?>) o;

        return Objects.equals(key, e.getKey()) &&
                Objects.equals(value, e.getValue());
    }

    public int hashCode() {
        return Objects.hash(key, value);
    }
}
