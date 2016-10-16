package com.github.peterpaul.cli;

import com.github.peterpaul.cli.fn.Pair;
import org.junit.Test;

import static com.github.peterpaul.cli.OutputHelper.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class OutputHelperTest {
    public static final String NEWLINE = "newline";
    public static final String A_STRING = "A string";
    public static final String A_STRING_WITHOUT_A = "A string without a";
    public static final String A_STRING_WITHOUT_A_NEWLINE = A_STRING_WITHOUT_A + " " + NEWLINE;
    public static final String WITH_A_NEWLINE = "with a newline";
    public static final int FIRST_LINE_INDENTATION = 5;
    public static final int LINE_WIDTH = 20;
    public static final int INDENTATION = 10;
    public static final ImmutableSectionConfiguration SECTION_CONFIGURATION = ImmutableSectionConfiguration
            .builder()
            .lineWidth(LINE_WIDTH)
            .firstLineIndentation(FIRST_LINE_INDENTATION)
            .indentation(INDENTATION)
            .build();
    public static final String WITHOUT_A = "without a";
    private static final String A_STRING_WITH_A_NEWLINE = A_STRING + "\n " + WITH_A_NEWLINE;
    private static final String A_STRING_WITH_A_NEWLINE_IN_ANOTHER_PLACE = "A string with a\n newline";

    @Test
    public void testSpaces() {
        for (int i = 0; i < 10; i++) {
            assertThat(spaces(i).length(), is(equalTo(i)));
        }
    }

    @Test
    public void renderNextLineHandlerNewLine() {
        assertThat(renderNextLine(0, A_STRING_WITH_A_NEWLINE, SECTION_CONFIGURATION), is(equalTo(Pair.of(A_STRING, WITH_A_NEWLINE))));
    }

    @Test
    public void renderNextLineHandlerWithoutNewLine() {
        assertThat(renderNextLine(0, A_STRING_WITHOUT_A_NEWLINE, SECTION_CONFIGURATION), is(equalTo(Pair.of(A_STRING_WITHOUT_A, NEWLINE))));
    }

    @Test
    public void renderNextLineWithTab() {
        assertThat(renderNextLine(5, "aaaa\taa\taa\taa", SECTION_CONFIGURATION), is(equalTo(Pair.of("     aaaa\taa", "aa\taa"))));
    }

    @Test
    public void formatWithoutNewLine() {
        assertThat(format(A_STRING_WITHOUT_A_NEWLINE, SECTION_CONFIGURATION), is(equalTo(spaces(FIRST_LINE_INDENTATION) + A_STRING + "\n" + spaces(INDENTATION) + WITHOUT_A + "\n" + spaces(INDENTATION) + NEWLINE)));
    }

    @Test
    public void formatWithNewLine() {
        assertThat(format(A_STRING_WITH_A_NEWLINE_IN_ANOTHER_PLACE, SECTION_CONFIGURATION), is(equalTo(spaces(FIRST_LINE_INDENTATION) + "A string with" + "\n" + spaces(INDENTATION) + "a" + "\n" + spaces(INDENTATION) + NEWLINE)));
    }

    @Test
    public void formatSimple() {
        assertThat(format("aa aa aa aa aa aa aa", SECTION_CONFIGURATION), is(equalTo(spaces(FIRST_LINE_INDENTATION) + "aa aa aa aa aa" + "\n" + spaces(INDENTATION) + "aa aa")));

    }
}
