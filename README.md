# cli

An annotation based CLI command framework for Java.

<pre lang="Java">
@Cli.Command(description = "Minimal example")
public class HelloWorld {
    public static void main(String[] args) {
        ProgramRunner.run(HelloWorld.class, args);
    }

    public void run() {
        System.out.println("Hello World");
    }
}
</pre>

Available from maven central with the following coordinates:

```xml
<dependency>
    <groupId>net.kleinhaneveld.cli</groupId>
    <artifactId>cli</artifactId>
    <version>0.1.0</version>
</dependency>
```

##Contents

- [Greeter example](#greeter-example)
- [Run Command](#run-command)
- [Arguments](#arguments)
- [Options](#options)
    - [Boolean Options](#boolean-options)
- [Composite Commands](#composite-commands)
- [ValueParser](#valueparser)
- [Instantiator](#instantiator)
- [I18n](#i18n)
- [TODO](#todo)

## Greeter example

Below is a more extensive example, showcasing all annotations.

<pre lang="Java">
@Cli.Command(name = "hello", description = "Example command using all cli annotations.")
public class Greeter {
    @Cli.Option(description = "some option", shortName = 'U')
    private boolean uppercase;

    @Cli.Argument(description = "some argument")
    private String who;

    public static void main(String[] args) {
        ProgramRunner.run(Greeter.class, args);
    }

    @Cli.Run
    public void perform() {
        String value = "Hello " + who;
        if (uppercase) {
            value = value.toUpperCase();
        }
        System.out.println(value);
    }
}
</pre>

This defines a program with one command, a mandatory argument and one option. The `ProgramRunner.run(...)` method will parse the arguments and set values to the corresponding fields.

The compiled binary, for example `hello`, can now be invoked in the following way:

<pre>
$ hello World
Hello World
$ hello -U Earth
HELLO EARTH
$ hello World --uppercase=false
Hello World
$ hello Earth -U=false
Hello Earth
$ hello --uppercase People
HELLO PEOPLE
</pre>

When run without arguments, this will produce the following output:

<pre>
$ hello
Error: Expected argument who

hello
    Example command using all cli annotations.

USAGE: hello [OPTION...]  who
WHERE:
    who:        some argument
OPTION:
    -U,--uppercase=boolean ('true', 'false') 
                some option
</pre>

## Run Command

`ProgramRunner` searches for a void method without arguments annotated with `@Cli.Run` or, if not found, with the name `run`.

## Arguments

An argument can have a `name`, a `description`, default `values` and a value `parser`.

The argument's `name` is only used for display purposes in the generated help output. If the `name` is not supplied in the argument, then the field name will be used.
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

Next to the `name`, `description`, `values` and `parser` attributes, a `@Cli.Option` can have a single character `shortName` and a `defaultValue`.

The option's `name` and `shortName` are used to parse options from the command line. On the command line the `name` is prefixed with `--`, the `shortName` with `-`.

All options, except boolean options, take an argument that must be provided after an `=` sign.

When an option is not supplied on the command line, the `defaultValue` is applied if present, or the default from source code is used. The `defaultValue` is matched against the accepted `values`, if present.

Options can be placed anywhere on the commandline (after the binary.) 

<pre lang="Java">
@Cli.Option(
    name = "option-name",
    shortName = 'o',
    description = "explains the purpose of this option",
    values = {"all", "allowed", "values"},
    parser = MyTypeParser.class,
    defaultValue = "allowed"    
)
private MyType option;
</pre>

### Boolean Options

Boolean options don't need to be given a value. If the option is present on the command line, but the value is not specified, the value will be set to `true`. 

## Composite Commands

Composite commands can be created by defining `subCommands` on a command without any arguments.`

<pre lang="Java">
@Cli.Command(
        description = "Composite command example",
        subCommands = {HelloWorld.class, Greeter.class, GreeterMyType.class}
)
public class ExampleProgram {
    public static void main(String[] args) {
        ProgramRunner.run(ExampleProgram.class, args);
    }
}
</pre>

Composite commands support the `help` command, which generates usage information about the composite command, or any of it's subcommands. For example when invoked with `ExampleProgram help`, it generates the following output.

<pre>
ExampleProgram
    Composite command example

USAGE: ExampleProgram COMMAND
COMMAND:
    HelloWorld  Minimal example
    hello       Example command using all cli annotations.
    GreeterMyType some command
</pre>

When invoked with `ExampleProgram help hello` it generates the following output.

<pre>
hello
    Example command using all cli annotations.

USAGE: hello [OPTION...] who
WHERE:
    who:        some argument
OPTION:
    -U,--uppercase=boolean ('true', 'false') 
                some option
</pre>

Composite commands only take one argument, but can have options and a run method. The run method for composite commands is a void method that can take a `Runner` argument that corresponds to invoking the subcommand. Consider the following example.

<pre lang="Java">
@Cli.Command(
        description = "Transaction subcommands example",
        subCommands = {HelloWorld.class, Greeter.class, GreeterMyType.class}
)
public class TransactionalCommand {
    public static void main(String[] args) {
        ProgramRunner.run(TransactionalCommand.class, args);
    }

    void run(Runner subCommand) {
        Transaction transaction = Transaction.begin();
        try {
            subCommand.run();
            transaction.commit();
        } catch (RuntimeException e) {
            transaction.rollback();
        }
    }
}
</pre>

This command will wrap the subcommand in a transaction that will be committed upon success, and rolledback upon failure.

## ValueParser

Next to supporting some standard types as arguments and options, ProgramRunner is extensible with additional parsers. Additional value parsers are discovered using `ServiceLoader`, or via the `parser` option at the `@Cli.Argument` and `@Cli.Option` annotations.

Value parsers must implement the `ValueParser` interface:

<pre lang="Java">
package net.kleinhaneveld.cli.parser;

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
@Cli.Command(name = "GreeterMyType", description = "some command")
public class GreeterMyType {
    @Cli.Option(description = "some option", shortName = 'U')
    private boolean uppercase;

    @Cli.Argument(description = "some argument")
    private MyType who;

    public static void main(String[] args) {
        ProgramRunner.run(GreeterMyType.class, args);
    }

    @Cli.Run
    public void perform() {
        String value = "Hello " + who.getValue();
        if (uppercase) {
            value = value.toUpperCase();
        }
        System.out.println(value);
    }
}
</pre>

Value parsers can also be registered via `ServiceLoader`. To do that add the a file named `META-INF/services/net.kleinhaneveld.cli.parser.ValueParser` to the classpath with the class name of the parser:

<pre lang="Java">
net.kleinhaneveld.cli.examples.MyTypeParser
</pre>

Then the specific value parser will be used automatically when arguments or options with any type in the `supportedClasses` are used.

<pre lang="Java">
@Cli.Command(name = "HelloWorldArg", description = "some command")
public class HelloWorldArg {
    @Cli.Option(description = "some option", shortName = 'U')
    private boolean uppercase;

    @Cli.Argument(description = "some argument")
    private <strong>MyType</strong> who;

    public static void main(String[] args) {
        ProgramRunner.run(HelloWorldArg.class, args);
    }

    @Cli.Run
    public void perform() {
        String value = "Hello " + <strong>who.getValue()</strong>;
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
package net.kleinhaneveld.cli.instantiator;

public interface Instantiator {
    <T> T instantiate(Class<T> aClass);
}
</pre>

A custom `Instantiator` can be registered via `ServiceLoader` in the file `META-INF/services/net.kleinhaneveld.cli.instantiator.Instantiator`.

This mechanism can be used to hook up specific injection framework.

## I18n

Internationalization is supported for the descriptions of commands, options and arguments by specifying `resourceBundle` in the `@Cli.Command` annotation. The values of the `description` attributes are used as keys for the bundle.

The following example shows an internationalized variant of the Greeter we saw before. Note that the same resource bundle is also used in the run method.

<pre lang="Java">
@Cli.Command(name = "hello", description = "command.hello", resourceBundle = "greeter")
public class InternationalizedGreeter {
    @Cli.Option(description = "option.uppercase", shortName = 'U')
    private boolean uppercase;

    @Cli.Argument(description = "argument.who")
    private String who;

    public static void main(String[] args) {
        ProgramRunner.run(InternationalizedGreeter.class, args);
    }

    public void run() {
        ResourceBundle bundle = ResourceBundle.getBundle("greeter", Locale.getDefault());
        String value = bundle.getString("hello") + " " + who;
        if (uppercase) {
            value = value.toUpperCase();
        }
        System.out.println(value);
    }
}
</pre>

An example resource bundle would be the following `greeter.properties`.

<pre lang="Java">
option.uppercase =Generate output in uppercase.
argument.who     =Who to greet.
command.hello    =Friendly greeter application.
hello            =Hi
</pre>

Example output would be

<pre>
$ hello there
Hi there
</pre>

Generated help output would be

<pre>
hello
    Friendly greeter application.

USAGE: hello [OPTION...] who
WHERE:
    who:        Who to greet.
OPTION:
    -U,--uppercase=boolean ('true', 'false') 
                Generate output in uppercase.
</pre>

## TODO

- [ ] Internationalized error messages.
- [ ] Parsing of combined shortNames of boolean options, as in `tar -xzvf file.tar.gz`.
- [ ] Detecting undefined options.
- [ ] Analyzing whether subcommands don't override options.
- [ ] Bash autocompletion.
