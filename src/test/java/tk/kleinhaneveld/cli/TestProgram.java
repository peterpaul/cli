package tk.kleinhaneveld.cli;

import org.junit.Test;
import tk.kleinhaneveld.cli.ProgramRunner;

public class TestProgram {
    @Test
    public void runTestCommand() {
        ProgramRunner programRunner = new ProgramRunner();
        programRunner.run(new String[]{"1", "2", "test.txt", "--verbose", "--url=http://nu.nl", "fiets", "race"}, new TestCommand());
    }
}
