package com.github.peterpaul.cli;

import org.immutables.value.Value;

@Value.Immutable
public abstract class SectionConfiguration {
    public abstract int getLineWidth();

    public abstract int getFirstLineIndentation();

    public abstract int getIndentation();

    @Value.Check
    public void check() {
        if (getFirstLineIndentation() > getLineWidth()) {
            throw new IllegalArgumentException("firstLineIndentation(" + getFirstLineIndentation() + ") may not be larger than the lineWidth(" + getLineWidth() + ")");
        }
        if (getIndentation() > getLineWidth()) {
            throw new IllegalArgumentException("indentation(" + getIndentation() + ") may not be larger than the lineWidth(" + getLineWidth() + ")");
        }
    }
}
