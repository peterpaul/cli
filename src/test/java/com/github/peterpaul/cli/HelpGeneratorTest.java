package com.github.peterpaul.cli;

import org.junit.Test;

public class HelpGeneratorTest {
    @Test
    public void test() {
        ValueParserProvider valueParserProvider = new ValueParserProvider();
        ArgumentParserMatcher argumentParserMatcher = new ArgumentParserMatcher(valueParserProvider);
        HelpGenerator helpGenerator = new HelpGenerator(argumentParserMatcher);
        String helpText = helpGenerator.generateHelp(new TestCommand());
        System.out.println(helpText);
    }
}
