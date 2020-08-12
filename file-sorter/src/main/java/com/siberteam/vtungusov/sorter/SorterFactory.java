package com.siberteam.vtungusov.sorter;

import com.siberteam.vtungusov.ui.BadArgumentsException;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import java.lang.reflect.Modifier;
import java.util.Set;
import java.util.stream.Collectors;

public class SorterFactory {
    private static final String BASE_PACKAGE = "com.siberteam.vtungusov";
    public static final String INVALID_CLASS_ARGUMENT = "Invalid arguments value for 'c' option. Class not supported.";
    private static final Set<Class<? extends AbstractSorter>> SORTERS;

    static {
        Reflections reflections = new Reflections(BASE_PACKAGE, new SubTypesScanner());
        SORTERS = reflections.getSubTypesOf(AbstractSorter.class).stream()
                .filter(SorterFactory::isInstantiable)
                .collect(Collectors.toSet());
    }

    public Set<Class<? extends AbstractSorter>> getSorters() {
        return SORTERS;
    }

    public Sorter createSorter(Class<?> clazz) throws BadArgumentsException {
        validateSorter(clazz);
        Sorter sorter = null;
        try {
            sorter = (Sorter) clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException ignore) {
        }
        return sorter;
    }

    private void validateSorter(Class<?> clazz) throws BadArgumentsException {
        if (!SORTERS.contains(clazz)) {
            throw new BadArgumentsException(INVALID_CLASS_ARGUMENT);
        }
    }

    private static boolean isInstantiable(Class<?> clazz) {
        return !clazz.isPrimitive()
                && !Modifier.isAbstract(clazz.getModifiers())
                && !clazz.isInterface()
                && !clazz.isArray();
    }
}
