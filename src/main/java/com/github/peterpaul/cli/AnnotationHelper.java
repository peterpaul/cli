package com.github.peterpaul.cli;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class AnnotationHelper {
    public static Optional<String> fromEmpty(String s) {
        return Objects.equals("", s)
                ? Optional.empty()
                : Optional.ofNullable(s);
    }

    public static Optional<Stream<String>> valueStream(String[] values) {
        return values.length != 0
                ? Optional.of(Arrays.stream(values))
                : Optional.empty();
    }

    public static Optional<String> checkedValue(String value, String[] values) {
        Optional<Stream<String>> valueStream = valueStream(values);
        return valueStream.isPresent()
                ? valueStream.get().filter(v -> v.equals(value)).findFirst()
                : Optional.of(value);
    }
}
