package com.github.peterpaul.cli;

import org.junit.Test;

public class TestProgram {
    @Test
    public void runTestCommand() {
        ProgramRunner.run(new TestCommand(), new String[]{"1", "2", "test.txt", "--verbose", "--url=http://nu.nl", "fiets", "race"});
    }

    @Test
    public void runTestCompositeCommand() {
        ProgramRunner.run(new TestCompositeCommand(), new String[]{"test", "1", "2", "test.txt", "--verbose", "--url=http://nu.nl", "fiets", "race"});
    }
}
