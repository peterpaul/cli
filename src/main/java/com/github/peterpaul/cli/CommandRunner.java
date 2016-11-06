package com.github.peterpaul.cli;

import com.github.peterpaul.cli.exceptions.IllegalRunMethodException;
import com.github.peterpaul.fn.*;
import com.github.peterpaul.fn.status.NoElements;
import com.github.peterpaul.fn.status.TooManyElements;
import com.github.peterpaul.fn.status.UniquenessErrorStatus;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static com.github.peterpaul.cli.AnnotationHelper.isAnnotationPresent;

public abstract class CommandRunner {
    public static void runCommand(Object command) {
        Option<Method> runMethod = Stream.stream(getRunMethodByAnnotation(command), getRunMethodByName(command))
                .filterMap(Function.<Option<Method>>identity())
                .first();
        runMethod.peek(new Consumer<Method>() {
            @Override
            public void consume(Method method) {
                int parameterCount = method.getParameterTypes().length;
                if (parameterCount != 0) {
                    throw new IllegalRunMethodException("Run method should not take any arguments, however '" + method.getName() + "' has '" + parameterCount + "'");
                }
                if (!method.getReturnType().equals(Void.TYPE)) {
                    throw new IllegalRunMethodException("Run method should not return anything, however '" + method.getName() + "' returns '" + method.getReturnType().getCanonicalName() + "'");
                }
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

    private static Option<Method> getRunMethodByName(Object command) {
        Class<?> commandClass = command.getClass();
        try {
            Method method = commandClass.getMethod("run");
            return Option.of(method);
        } catch (NoSuchMethodException e) {
            return Option.none();
        }
    }

    private static Option<Method> getRunMethodByAnnotation(Object command) {
        Class<?> commandClass = command.getClass();
        Either<Method, UniquenessErrorStatus<Method>> annotatedRunMethods = Stream.stream(commandClass.getDeclaredMethods())
                .filter(isAnnotationPresent(Cli.Run.class))
                .unique();
        return annotatedRunMethods.map(
                new Function<Method, Option<Method>>() {
                    @Override
                    public Option<Method> apply(Method method) {
                        return Option.some(method);
                    }
                },
                new Function<UniquenessErrorStatus<Method>, Option<Method>>() {
                    @Override
                    public Option<Method> apply(UniquenessErrorStatus<Method> methodUniquenessErrorStatus) {
                        return methodUniquenessErrorStatus.getStatus().map(
                                new Function<NoElements, Option<Method>>() {
                                    @Override
                                    public Option<Method> apply(NoElements noElements) {
                                        return Option.none();
                                    }
                                },
                                new Function<TooManyElements<Method>, Option<Method>>() {
                                    @Override
                                    public Option<Method> apply(TooManyElements<Method> methodTooManyElements) {
                                        throw new IllegalRunMethodException("Only one method may be annotated with " + Cli.Run.class.getCanonicalName() +
                                                " but multiple found: " + methodTooManyElements.getItems());
                                    }
                                }
                        );
                    }
                });
    }
}
