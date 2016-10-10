package com.github.peterpaul.cli;

import com.github.peterpaul.cli.collection.ListUtil;
import com.github.peterpaul.cli.collection.TooManyElementException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CommandRunner {
    public static void runCommand(Object command) {
        Optional<Method> runMethod = Stream.of(getRunMethodByAnnotation(command), getRunMethodByName(command))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
        runMethod.ifPresent(method -> {
            if (method.getParameterCount() != 0) {
                throw new IllegalRunMethodException("Run method should not take any arguments, however '" + method.getName() + "' has '" + method.getParameterCount() + "'");
            }
            if (!method.getReturnType().equals(Void.TYPE)) {
                throw new IllegalRunMethodException("Run method should not return anything, however '" + method.getName() + "' returns '" + method.getReturnType().getCanonicalName() + "'");
            }
        });
        if (runMethod.isPresent()) {
            try {
                runMethod.get().invoke(command);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new IllegalRunMethodException("No run method found.");
        }
    }

    private static Optional<Method> getRunMethodByName(Object command) {
        Class<?> commandClass = command.getClass();
        try {
            Method method = commandClass.getMethod("run");
            return Optional.of(method);
        } catch (NoSuchMethodException e) {
            return Optional.empty();
        }
    }

    private static Optional<Method> getRunMethodByAnnotation(Object command) {
        Class<?> commandClass = command.getClass();
        List<Method> annotatedRunMethods = Arrays.stream(commandClass.getDeclaredMethods())
                .filter(m -> m.isAnnotationPresent(Cli.Run.class))
                .collect(Collectors.toList());
        try {
            return ListUtil.tryGetUnique(annotatedRunMethods);
        } catch (TooManyElementException e) {
            throw new IllegalRunMethodException("Only one method may be annotated with " + Cli.Run.class.getCanonicalName(),
                    e);
        }
    }
}
