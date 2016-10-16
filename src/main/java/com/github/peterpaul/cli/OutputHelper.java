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
        int splitPosition = getSplitPosition(indentation, content, sectionConfiguration);
        if (splitPosition < 0) {
            return Pair.of(spaces(indentation) + content, "");
        } else {
            return Pair.of(spaces(indentation) + content.substring(0, splitPosition),
                    content.substring(splitPosition, content.length()).trim());
        }
    }

    private static int getSplitPosition(int indentation, String content, SectionConfiguration sectionConfiguration) {
        Optional<Integer> lastWordPosition = Optional.empty();
        int currentIndex;
        int currentPosition;
        for (currentPosition = indentation, currentIndex = 0; currentPosition < sectionConfiguration.getLineWidth() && currentIndex < content.length(); currentIndex++, currentPosition++) {
            char currentChar = content.charAt(currentIndex);
            if (Character.isWhitespace(currentChar)) {
                lastWordPosition = Optional.of(currentIndex);
                if (currentChar == '\n') {
                    break;
                } else if (currentChar == '\t') {
                    currentPosition = (currentPosition / 8 + 1) * 8;
                }
            }
        }
        if (currentIndex == content.length()) {
            lastWordPosition = Optional.of(currentIndex);
        }
        return lastWordPosition.orElse(getFirstWhitespacePosition(content));
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

    public static String ofSize(String content, int size) {
        if (size > content.length()) {
            return content + spaces(size - content.length());
        } else {
            return content + " ";
        }
    }
}
