package net.kleinhaneveld.cli.examples;

import net.kleinhaneveld.cli.ProgramRunner;
import org.junit.Test;

public class ExampleProgramTest {
    @Test
    public void testArgumentListCommand() {
        ProgramRunner.run(ExampleProgram.class, new String[]{"help", ArgumentListCommand.class.getSimpleName()});
    }

    @Test
    public void testHelloWorld() {
        ProgramRunner.run(ExampleProgram.class, new String[]{"help", HelloWorld.class.getSimpleName()});
    }

    @Test
    public void testHello() {
        ProgramRunner.run(ExampleProgram.class, new String[]{"help", "hello"});
    }

    @Test
    public void testGreeterMyType() {
        ProgramRunner.run(ExampleProgram.class, new String[]{"help", GreeterMyType.class.getSimpleName()});
    }

    @Test
    public void testHelp() {
        ProgramRunner.run(ExampleProgram.class, new String[]{"help"});
    }
}
