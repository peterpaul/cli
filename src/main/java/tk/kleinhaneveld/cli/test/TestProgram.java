package tk.kleinhaneveld.cli.test;

import org.junit.Test;
import tk.kleinhaneveld.cli.ProgramRunner;

/**
 * Created by peterpaul on 7-10-16.
 */
public class TestProgram {
    @Test
    public void runTestCommand() {
        ProgramRunner programRunner = new ProgramRunner();
        programRunner.run(new String[]{"1", "2", "test.txt"}, new TestCommand());
    }
}
