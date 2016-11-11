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

When run without arguments, this will produce the following output:

```
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
