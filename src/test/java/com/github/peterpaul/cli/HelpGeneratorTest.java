package com.github.peterpaul.cli;

import org.junit.Test;

public class HelpGeneratorTest {
    @Test
    public void test() {
        String helpText = HelpGenerator.generateHelp(new TestCommand());
        System.out.println(helpText);
    }
}
