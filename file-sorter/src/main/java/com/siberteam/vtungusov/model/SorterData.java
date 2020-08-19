package com.siberteam.vtungusov.model;

import com.siberteam.vtungusov.sorter.Sorter;

import java.lang.reflect.Constructor;
import java.util.Objects;

public class SorterData {
    private final String name;
    private final String description;
    private final Constructor<? extends Sorter> constructor;

    public SorterData(String name, String description, Constructor<? extends Sorter> constructor) {
        this.name = name;
        this.description = description;
        this.constructor = constructor;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Constructor<? extends Sorter> getConstructor() {
        return constructor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SorterData that = (SorterData) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(description, that.description) &&
                Objects.equals(constructor, that.constructor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, constructor);
    }
}
