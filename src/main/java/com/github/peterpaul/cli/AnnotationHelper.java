package com.github.peterpaul.cli;

import com.github.peterpaul.cli.locale.Bundle;
import com.github.peterpaul.fn.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

import static com.github.peterpaul.fn.Predicates.equalTo;
import static com.github.peterpaul.fn.Stream.stream;

public abstract class AnnotationHelper {
    public static final Function<Class, String> GET_COMMAND_NAME = new Function<Class, String>() {
        @Override
        public String apply(Class aClass) {
            return getCommandName(aClass);
        }
    };
    public static final Function<Class, Pair<String, Class>> GET_NAME_TO_CLASS_MAP = new Function<Class, Pair<String, Class>>() {
        @Override
        public Pair<String, Class> apply(Class aClass) {
            return Pair.pair(getCommandName(aClass), aClass);
        }
    };

    public static <T extends AccessibleObject> Predicate<T> isAnnotationPresent(final Class<? extends Annotation> aClass) {
        return new Predicate<T>() {
            @Override
            public Boolean apply(T t) {
                return t.isAnnotationPresent(aClass);
            }
        };
    }

    public static Option<String> fromEmpty(String s) {
        return Objects.equals("", s)
                ? Option.<String>none()
                : Option.of(s);
    }

    public static Option<Stream<String>> valueStream(String[] values) {
        return values.length != 0
                ? Option.of(stream(values))
                : Option.<Stream<String>>none();
    }

    public static Option<String> checkedValue(final String value, String[] values) {
        Option<Stream<String>> valueStream = valueStream(values);
        return valueStream.isPresent()
                ? valueStream.get()
                .filter(equalTo(value))
                .first()
                : Option.of(value);
    }

    public static Cli.Command getCommandAnnotation(Object command) {
        return getCommandAnnotation(command.getClass());
    }

    public static Cli.Command getCommandAnnotation(Class<?> aClass) {
        return aClass.getAnnotation(Cli.Command.class);
    }

    public static Cli.Option getOptionAnnotation(Field field) {
        return field.getAnnotation(Cli.Option.class);
    }

    public static Cli.Argument getArgumentAnnotation(Field field) {
        return field.getAnnotation(Cli.Argument.class);
    }

    public static Bundle getResourceBundle(Cli.Command commandAnnotation) {
        return new Bundle(fromEmpty(commandAnnotation.resourceBundle())
                .map(new Function<String, ResourceBundle>() {
                    @Override
                    public ResourceBundle apply(String r) {
                        return ResourceBundle.getBundle(r, Locale.getDefault());
                    }
                }));
    }

    public static String getCommandName(Class aClass) {
        return getCommandAnnotation(aClass).name();
    }
}
