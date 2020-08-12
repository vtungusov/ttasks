package com.siberteam.vtungusov.util;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import java.util.Set;

public class SortReflection {
    private static final String BASE_PACKAGE = "com.siberteam.vtungusov";

    public static synchronized <T> Set<Class<? extends T>> getSubTypes(Class<T> clazz) {
        Reflections reflections = new Reflections(BASE_PACKAGE, new SubTypesScanner());
        return reflections.getSubTypesOf(clazz);
    }

    public static synchronized <T> T getInstance(Class<T> clazz) throws InstantiationException, IllegalAccessException {
        return clazz.newInstance();
    }
}
