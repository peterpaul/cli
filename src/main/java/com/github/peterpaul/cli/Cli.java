package com.github.peterpaul.cli;

import com.github.peterpaul.cli.parser.ValueParser;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE, FIELD, METHOD})
@Retention(RUNTIME)
public @interface Cli {
    @Target({TYPE})
    @Retention(RUNTIME)
    @interface Command {
        String name() default "";

        String description();

        Class[] subCommands() default {};

        String resourceBundle() default "";
    }

    @Target({FIELD})
    @Retention(RUNTIME)
    @interface Argument {
        String name() default "";

        String description();

        String[] values() default {};

        Class<? extends ValueParser> parser() default ValueParser.class;
    }

    @Target({FIELD})
    @Retention(RUNTIME)
    @interface Option {
        String name() default "";

        String description();

        String[] values() default {};

        Class<? extends ValueParser> parser() default ValueParser.class;

        char shortName() default '\0';

        String defaultValue() default "";
    }

    @Target({ElementType.METHOD})
    @Retention(RUNTIME)
    @interface Run {
    }
}
