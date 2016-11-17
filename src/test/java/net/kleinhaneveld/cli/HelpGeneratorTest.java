package net.kleinhaneveld.cli;

import org.junit.Test;

public class HelpGeneratorTest {
    @Test
    public void test() {
        String helpText = HelpGenerator.generateHelp(new TestCommand());
        System.out.println(helpText);
    }

    @Test
    public void testComposite() {
        String helpText = HelpGenerator.generateHelp(new TestCompositeCommand());
        System.out.println(helpText);
    }
}
