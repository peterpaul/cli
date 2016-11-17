package net.kleinhaneveld.cli;

import net.kleinhaneveld.cli.exceptions.IllegalRunMethodException;
import net.kleinhaneveld.fn.*;
import net.kleinhaneveld.fn.status.NoElements;
import net.kleinhaneveld.fn.status.TooManyElements;
import net.kleinhaneveld.fn.status.UniquenessErrorStatus;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static net.kleinhaneveld.cli.AnnotationHelper.isAnnotationPresent;
import static net.kleinhaneveld.cli.exceptions.ExceptionWrapper.wrap;
import static net.kleinhaneveld.fn.Stream.stream;

public abstract class CommandRunner {
    public static void runCommand(Object command) {
        Option<Method> runMethod = getRunMethod(command);
        if (runMethod.isPresent()) {
            invoke(runMethod.get(), command);
        } else {
            throw new IllegalRunMethodException("No run method found.");
        }
    }

    public static void runCompositeCommand(Object command, Runner subCommandInvocation) {
        Option<Method> runMethod = getRunMethod(command, Runner.class);
        if (runMethod.isPresent()) {
            invoke(runMethod.get(), command, subCommandInvocation);
        } else {
            subCommandInvocation.run();
        }
    }

    private static void invoke(Method runMethod, Object command, Object... subCommandInvocation) {
        try {
            runMethod.setAccessible(true);
            runMethod.invoke(command, subCommandInvocation);
        } catch (InvocationTargetException e) {
            throw wrap(e.getTargetException());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static Option<Method> getRunMethod(Object command, Class<?>... runMethodArguments) {
        return stream(getRunMethodByAnnotation(command), getRunMethodByName(command, runMethodArguments))
                .filterMap(Functions.<Option<Method>>identity())
                .first()
                .peek(validateCompositeRunMethod(runMethodArguments));
    }

    private static void validateMethodIsProcedure(Method method) {
        if (!method.getReturnType().equals(Void.TYPE)) {
            throw new IllegalRunMethodException("Run method should not return anything, however '" + method.getName() + "' returns '" + method.getReturnType().getCanonicalName() + "'");
        }
    }

    private static Consumer<Method> validateCompositeRunMethod(final Class<?>[] requiredMethodArguments) {
        return new Consumer<Method>() {
            @Override
            public void consume(@Nonnull Method method) {
                int parameterCount = method.getParameterTypes().length;
                if (!(parameterCount == requiredMethodArguments.length &&
                        allAssignableFrom(requiredMethodArguments, method.getParameterTypes()))) {
                    String requiredParameterTypeNames = getSimpleClassNames(requiredMethodArguments);
                    String parameterTypeNames = getSimpleClassNames(method.getParameterTypes());

                    throw new IllegalRunMethodException(String
                            .format("Run method should have %d parameter(s): %s, however %s has %d parameter(s): %s",
                                    requiredMethodArguments.length,
                                    requiredParameterTypeNames,
                                    method.getName(),
                                    parameterCount,
                                    parameterTypeNames));
                }
                validateMethodIsProcedure(method);
            }
        };
    }

    private static String getSimpleClassNames(Class<?>[] parameterTypes) {
        return stream(parameterTypes)
                .map(new Function<Class<?>, String>() {
                    @Nonnull
                    @Override
                    public String apply(@Nonnull Class<?> aClass) {
                        return aClass.getCanonicalName();
                    }
                })
                .reduce(Reductions.join(", "))
                .map(new Function<String, String>() {
                    @Nonnull
                    @Override
                    public String apply(@Nonnull String s) {
                        return "[" + s + ']';
                    }
                })
                .or("[]");
    }

    private static boolean allAssignableFrom(Class<?>[] runMethodArguments, Class<?>[] parameterTypes) {
        return stream(runMethodArguments)
                .zip(stream(parameterTypes))
                .map(new Function<Pair<Class<?>, Class<?>>, Boolean>() {
                    @Nonnull
                    @Override
                    public Boolean apply(@Nonnull Pair<Class<?>, Class<?>> p) {
                        return p.getLeft().isAssignableFrom(p.getRight());
                    }
                })
                .reduce(true, Reductions.ALL_TRUE);
    }

    private static Option<Method> getRunMethodByName(Object command, Class<?>[] arguments) {
        Class<?> commandClass = command.getClass();
        try {
            Method method = commandClass.getDeclaredMethod("run", arguments);
            return Option.of(method);
        } catch (NoSuchMethodException e) {
            return Option.none();
        }
    }

    private static Option<Method> getRunMethodByAnnotation(Object command) {
        Class<?> commandClass = command.getClass();
        Either<Method, UniquenessErrorStatus<Method>> annotatedRunMethods = stream(commandClass.getDeclaredMethods())
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
