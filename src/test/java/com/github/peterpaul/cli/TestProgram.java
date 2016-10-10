package com.github.peterpaul.cli;

import org.junit.Test;

public class TestProgram {
    @Test
    public void runTestCommand() {
        ProgramRunner programRunner = new ProgramRunner();
        programRunner.run(new String[]{"1", "2", "test.txt", "--verbose", "--url=http://nu.nl", "fiets", "race"}, new TestCommand());
    }
}
