package net.kleinhaneveld.cli;

import net.kleinhaneveld.fn.Stream;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static net.kleinhaneveld.cli.AnnotationHelper.isAnnotationPresent;
import static net.kleinhaneveld.fn.Predicates.equalTo;
import static net.kleinhaneveld.fn.Predicates.not;

public abstract class FieldsProvider {
    public static List<Field> getArgumentList(Class<?> commandClass) {
        return getArgumentStream(commandClass)
                .to(new ArrayList<Field>());
    }

    public static Stream<Field> getArgumentStream(Class<?> commandClass) {
        return getFieldStream(commandClass, Cli.Argument.class);
    }

    public static Stream<Field> getOptionStream(Class<?> commandClass) {
        return getFieldStream(commandClass, Cli.Option.class);
    }

    public static String getName(Field field, String name) {
        return Stream.stream(name, field.getName())
                .filter(not(equalTo("")))
                .first()
                .get();
    }

    private static Stream<Field> getFieldStream(Class<?> commandClass, final Class<? extends Annotation> annotationClass) {
        return Stream.stream(commandClass.getDeclaredFields())
                .filter(isAnnotationPresent(annotationClass));
    }
}
