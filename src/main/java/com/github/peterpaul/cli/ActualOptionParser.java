package com.github.peterpaul.cli;

public class ActualOptionParser {
    public static String optionKey(String option) {
        int offset = option.indexOf("=");
        if (offset < 0) {
            return option;
        } else {
            return option.substring(0, offset);
        }
    }

    public static String optionValue(String option) {
        int offset = option.indexOf("=");
        if (offset < 0) {
            return "";
        } else {
            return option.substring(offset + 1, option.length());
        }
    }
}
