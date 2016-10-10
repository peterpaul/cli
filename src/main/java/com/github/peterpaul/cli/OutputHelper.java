package com.github.peterpaul.cli;

import com.github.peterpaul.cli.fn.Pair;

import java.util.Optional;

public class OutputHelper {
    public static String format(String content, SectionConfiguration sectionConfiguration) {
        Pair<String, String> nextLine = renderNextLine(sectionConfiguration.getFirstLineIndentation(), content, sectionConfiguration);
        String output = nextLine.getLeft();
        while (nextLine.getRight().length() > 0) {
            nextLine = renderNextLine(sectionConfiguration.getIndentation(), nextLine.getRight(), sectionConfiguration);
            output += "\n" + nextLine.getLeft();
        }
        return output;
    }

    public static Pair<String, String> renderNextLine(int indentation, String content, SectionConfiguration sectionConfiguration) {
        Optional<Integer> lastWordPosition = Optional.empty();
        int currentPosition;
        if (content.length() <= sectionConfiguration.getLineWidth() - indentation) {
            lastWordPosition = Optional.of(content.length());
        } else {
            for (currentPosition = 0; currentPosition < sectionConfiguration.getLineWidth() - indentation; currentPosition++) {
                if (Character.isWhitespace(content.charAt(currentPosition))) {
                    lastWordPosition = Optional.of(currentPosition);
                }
            }
        }
        int splitPosition = lastWordPosition.orElse(getFirstWhitespacePosition(content));
        if (splitPosition < 0) {
            return Pair.of(spaces(indentation) + content, "");
        } else {
            return Pair.of(spaces(indentation) + content.substring(0, splitPosition),
                    content.substring(splitPosition, content.length()).trim());
        }
    }

    public static int getFirstWhitespacePosition(String content) {
        int i;
        for (i = 0; i < content.length(); i++) {
            if (Character.isWhitespace(content.charAt(i))) {
                return i;
            }
        }
        return -1;
    }

    public static String spaces(int numberOfSpaces) {
        if (numberOfSpaces == 0) {
            return "";
        }
        if (numberOfSpaces == 1) {
            return " ";
        } else {
            int remainder = numberOfSpaces % 2;
            String rec = spaces(numberOfSpaces / 2);
            return rec + rec + spaces(remainder);
        }
    }
}
