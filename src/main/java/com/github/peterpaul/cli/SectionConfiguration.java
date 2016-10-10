package com.github.peterpaul.cli;

import org.immutables.value.Value;

@Value.Immutable
public interface SectionConfiguration {
    int getLineWidth();

    int getFirstLineIndentation();

    int getIndentation();

    @Value.Check
    default void check() {
        if (getFirstLineIndentation() > getLineWidth()) {
            throw new IllegalArgumentException("firstLineIndentation(" + getFirstLineIndentation() + ") may not be larger than the lineWidth(" + getLineWidth() + ")");
        }
        if (getIndentation() > getLineWidth()) {
            throw new IllegalArgumentException("indentation(" + getIndentation() + ") may not be larger than the lineWidth(" + getLineWidth() + ")");
        }
    }
}
