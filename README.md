# cli

An annotation based CLI command framework for Java.

## Hello World

Below is a `Hello World` example, showcasing all annotations:

<pre lang="Java">
@Cli.Command(name = "HelloWorld", description = "some command")
public class HelloWorld {
    @Cli.Option(description = "some option", shortName = 'U')
    private boolean uppercase;

    @Cli.Argument(description = "some argument")
    private String who;

    @Cli.Run
    void perform() {
        String value = "Hello " + who;
        if (uppercase) {
            value = value.toUpperCase();
        }
        System.out.println(value);
    }
    
    public static void main(String[] args) {
        ProgramRunner.run(HelloWorld.class, args);
    }
}
</pre>

This defines a program with one command, a mandatory argument and one option. The `ProgramRunner.run(...)` method will parse the arguments and set values to the corresponding fields.

The compiled binary, for example `HelloWorld`, can now be invoked in the following way:

<pre>
$ HelloWorld World
Hello World
$ HelloWorld -U Earth
HELLO EARTH
$ HelloWorld World --uppercase=false
Hello World
$ HelloWorld Earth -U=false
Hello Earth
$ HelloWorld --uppercase People
HELLO PEOPLE
</pre>

When run without arguments, this will produce the following output:

<pre>
$ HelloWorld
Error: Expected more arguments.

HelloWorld
    some command

USAGE: HelloWorld [OPTION...]  who
WHERE:
    who:        some argument
OPTION:
    -U,--uppercase=boolean ('true', 'false') 
                some option
</pre>

## Arguments

An argument can have a `name`, a `description`, default `values` and a value `parser`.

The argument `name` is only used for display purposes in the generated help output. If the `name` is not supplied in the argument, then the field name will be used.
`description` is mandatory, it is used in the generated help output.
`values` are the accepted values for the argument. When any other value is supplied, an error is displayed with usage.
`parser` can be used to specify a parser for the type, see [ValueParser](#valueparser).

<pre lang="Java">
@Cli.Argument(
    name = "argument_name",
    description = "explains the purpose of this argument",
    values = {"all", "allowed", "values"},
    parser = MyTypeParser.class
)
private MyType argument;
</pre>

## Options
## Composite Commands
## ValueParser

Next to supporting some standard types as arguments and options, ProgramRunner is extensible with additional parsers. Additional value parsers are discovered using `ServiceLoader`, or via the `parser` option at the `@Cli.Argument` and `@Cli.Option` annotations.

Value parsers must implement the `ValueParser` interface:

<pre lang="Java">
package com.github.peterpaul.cli.parser;

public interface ValueParser<T> {
    Class[] getSupportedClasses();

    T parse(String argument) throws ValueParseException;
}
</pre>

As an example, consider the following custom type.

<pre lang="Java">
public class MyType {
    private final String value;

    public MyType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
</pre>

The value parser for this type would look like this.

<pre lang="Java">
public class MyTypeParser implements ValueParser<MyType> {
    @Override
    public Class[] getSupportedClasses() {
        return new Class[]{ MyType.class };
    }

    @Override
    public MyType parse(String argument) throws ValueParseException {
        return new MyType(argument);
    }
}
</pre>

Value parsers can be registered in the `@Cli.Argument` annotation (see the bold sections.)

<pre lang="java">
public class HelloWorldArg {
    @Cli.Option(description = "some option", shortName = 'U')
    private boolean uppercase;

    @Cli.Argument(description = "some argument", <b>parser = MyTypeParser.class</b>)
    private <b>MyType</b> who;

    public static void main(String[] args) {
        ProgramRunner.run(HelloWorldArg.class, args);
    }

    @Cli.Run
    public void perform() {
        String value = "Hello " + <b>who.getValue()</b>;
        if (uppercase) {
            value = value.toUpperCase();
        }
        System.out.println(value);
    }
}
</pre>

Value parsers can also be registered via `ServiceLoader`. To do that add the a file named `META-INF/services/com.github.peterpaul.cli.parser.ValueParser` to the classpath with the class name of the parser:

<pre lang="Java">
com.github.peterpaul.cli.examples.MyTypeParser
</pre>

Then the specific value parser will be used automatically when arguments or options with any type in the `supportedClasses` are used.

<pre lang="Java">
@Cli.Command(name = "HelloWorldArg", description = "some command")
public class HelloWorldArg {
    @Cli.Option(description = "some option", shortName = 'U')
    private boolean uppercase;

    @Cli.Argument(description = "some argument")
    private <b>MyType</b> who;

    public static void main(String[] args) {
        ProgramRunner.run(HelloWorldArg.class, args);
    }

    @Cli.Run
    public void perform() {
        String value = "Hello " + <b>who.getValue()</b>;
        if (uppercase) {
            value = value.toUpperCase();
        }
        System.out.println(value);
    }
}
</pre>

## Instantiator

There are several cases where `ProgramRunner` must create instances of classes. These are custom value parsers, commands and subcommands.

It does this via an `Instantiator`.

<pre lang="Java">
package com.github.peterpaul.cli.instantiator;

public interface Instantiator {
    <T> T instantiate(Class<T> aClass);
}
</pre>

A custom `Instantiator` can be registered via `ServiceLoader` in the file `META-INF/services/com.github.peterpaul.cli.instantiator.Instantiator`.

This mechanism can be used to hook up specific injection framework.

## Run
## I18n