package com.siberteam.vtungusov.sorter;

import com.siberteam.vtungusov.annotation.Description;
import com.siberteam.vtungusov.model.SorterData;
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
    public static final String DEFAULT_CONSTRUCTOR_EXPECTED = "Sorter class must contain default public constructor. Constructor expected for ";
    private static final String CREATION_ERROR = "Error due sorter creation";
    private static final Set<Class<? extends Sorter>> SORTERS;

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
            throw new InstantiationException(CREATION_ERROR + constructor.getClass().getName());
        }
    }

    public SorterData getSorterData(String clazz) throws BadArgumentsException {
        Class<? extends Sorter> optClass = validateAndGetClass(clazz);
        try {
            return new SorterData(optClass.getName(), getDescription(optClass), optClass.getConstructor());
        } catch (NoSuchMethodException e) {
            throw new BadArgumentsException(DEFAULT_CONSTRUCTOR_EXPECTED + clazz);
        }
    }

    private String getDescription(Class<? extends Sorter> clazz) {
        String result = "";
        Description annotation = clazz.getAnnotation(Description.class);
        if (annotation != null) {
            result = annotation.value();
        }
        return result;
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

    public Set<SorterData> getAllSorterData() {
        return SORTERS.stream()
                .map(this::getSorterData)
                .collect(Collectors.toSet());
    }

    private SorterData getSorterData(Class<? extends Sorter> clazz) {
        try {
            return getSorterData(clazz.getName());
        } catch (BadArgumentsException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
