package com.siberteam.vtungusov.sorter;

import com.siberteam.vtungusov.ui.BadArgumentsException;
import org.reflections.Reflections;
import org.reflections.scanners.MethodParameterScanner;
import org.reflections.scanners.SubTypesScanner;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Set;
import java.util.stream.Collectors;

public class SorterFactory {
    private static final String BASE_PACKAGE = "com.siberteam.vtungusov";
    private static final String INVALID_CLASS_ARGUMENT = "Invalid arguments value for 'c' option. Class not supported.";
    private static final Set<Class<? extends Sorter>> SORTERS;
    private static final String DEFAULT_CONSTRUCTOR_EXPECTED = "Sorter class must contain default public constructor";
    private static final String CREATION_ERROR = "Error due sorter creation";

    static {
        Reflections reflections = new Reflections(BASE_PACKAGE, new SubTypesScanner(), new MethodParameterScanner());
        SORTERS = reflections.getSubTypesOf(Sorter.class).stream()
                .filter(SorterFactory::isInstantiable)
                .collect(Collectors.toSet());
    }

    public Sorter getSorter(Constructor<? extends Sorter> constructor) throws InstantiationException {
        try {
            return constructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException ignore) {
            throw new InstantiationException(CREATION_ERROR);
        }
    }

    public Constructor<? extends Sorter> getConstructor(String clazz) throws BadArgumentsException {
        Class<? extends Sorter> optClass = validateAndGetClass(clazz);
        try {
            return optClass.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new BadArgumentsException(DEFAULT_CONSTRUCTOR_EXPECTED);
        }
    }

    private Class<? extends Sorter> validateAndGetClass(String clazz) throws BadArgumentsException {
        return SORTERS.stream()
                .filter(aClass -> aClass.getName().equals(clazz))
                .findFirst().orElseThrow(() -> new BadArgumentsException(INVALID_CLASS_ARGUMENT));
    }

    private static boolean isInstantiable(Class<?> clazz) {
        return !Modifier.isAbstract(clazz.getModifiers())
                && !clazz.isInterface();
    }

    public static Set<Class<? extends Sorter>> getSorters() {
        return SORTERS;
    }

    public Set<Class<? extends Sorter>> getAllSorter() {
        return SORTERS;
    }
}
