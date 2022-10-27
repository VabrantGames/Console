package com.vabrant.console.arguments.strategies.pattern;

import com.vabrant.console.SectionSpecifier;
import com.vabrant.console.arguments.Argument;
import com.vabrant.console.arguments.StringArgument;

public class PatternStringArgumentStrategy implements Argument.ArgumentStrategy<PatternStrategyInput> {

    private final SectionSpecifier specifier;

    public PatternStringArgumentStrategy() {
        specifier = new SectionSpecifier.Builder()
                .specifiedSection(StringArgument.class)

                .addRule(SectionSpecifier.Builder.Rules.CUSTOM, "\"")
                .addRule(SectionSpecifier.Builder.Rules.EXPLICT, ".*")
                .addRule(SectionSpecifier.Builder.Rules.CUSTOM, "\"")
                .build();
    }

    @Override
    public boolean isType(PatternStrategyInput patternStrategyInput) {
        return false;
    }
}
