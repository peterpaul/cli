# cli

An annotation based CLI framework for Java.

## Hello World

Below is a `Hello World` example, showcasing all annotations:

```Java
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
```

This defines a program with one command, a mandatory argument and one option. The `ProgramRunner.run(...)` method will
parse the arguments and set values to the corresponding fields.

The compiled binary, for example `HelloWorld`, can now be invoked in the following way:

```
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
```

When run without arguments, this will produce the following output:

```
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
```

## Arguments

An argument can have a `name`, a `description`, default `values` and a value `parser`.
The argument `name` is only used for display purposes in the generated help output.
If the `name` is not supplied in the argument, then the field name will be used.
The `description` is mandatory. It is used in the generated help output.

## Options
## Composite Commands
## ValueParser
## Instantiator
## Run
## I18n