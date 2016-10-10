package com.github.peterpaul.cli;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class OutputHelperTest {
    @Test
    public void testSpaces() {
        for (int i = 0; i < 10; i++) {
            assertThat(OutputHelper.spaces(i).length(), is(equalTo(i)));
        }
    }

    @Test
    public void testFormat() {
        String formattedOutput = OutputHelper.format("Een aap die geen bananen eet.", ImmutableSectionConfiguration.builder().lineWidth(20).firstLineIndentation(5).indentation(10).build());
        System.out.println(formattedOutput);
    }
}
