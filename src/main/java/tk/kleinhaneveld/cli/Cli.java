package tk.kleinhaneveld.cli;

import tk.kleinhaneveld.cli.parser.ValueParser;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE, FIELD, METHOD})
@Retention(RUNTIME)
public @interface Cli {
    @Target({TYPE})
    @Retention(RUNTIME)
    @interface Command {
        String name();

        String description();

        Class[] subCommands() default {};
    }

    @Target({FIELD})
    @Retention(RUNTIME)
    @interface Argument {
        String name() default "";

        String description();

        Class<? extends ValueParser> parser() default ValueParser.class;
    }

    @Target({FIELD})
    @Retention(RUNTIME)
    @interface Option {
        String name() default "";

        String shortName() default "";

        String description();

        Class<? extends ValueParser> parser() default ValueParser.class;
    }

    @Target({ElementType.METHOD})
    @Retention(RUNTIME)
    @interface Run {
    }
}
