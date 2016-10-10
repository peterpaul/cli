package com.github.peterpaul.cli;

import java.util.Objects;
import java.util.Optional;

public class AnnotationHelper {
    public static Optional<String> fromEmpty(String s) {
        return Objects.equals("", s)
                ? Optional.empty()
                : Optional.ofNullable(s);
    }
}
