package com.github.peterpaul.cli;

import com.github.peterpaul.fn.Function;

public abstract class ActualOptionParser {
    public static Function<String, String> optionKey() {
        return new Function<String, String>() {
            @Override
            public String apply(String s) {
                return optionKey(s);
            }
        };
    }

    public static Function<String, String> optionValue() {
        return new Function<String, String>() {
            @Override
            public String apply(String s) {
                return optionValue(s);
            }
        };
    }

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
