package com.vabrant.console.arguments.strategies.pattern;

import com.vabrant.console.SectionSpecifier;
import com.vabrant.console.arguments.Argument;
import com.vabrant.console.arguments.DoubleArgument;

public class PatternDoubleArgumentStrategy implements Argument.ArgumentStrategy<PatternStrategyData> {

    private final SectionSpecifier specifier;

    public PatternDoubleArgumentStrategy() {
        specifier = new SectionSpecifier.Builder()
                .specifiedSection(DoubleArgument.class)

                //e.g
                //100.
                //100.0
                //100.0d
                .addRule(SectionSpecifier.Builder.Rules.DIGIT | SectionSpecifier.Builder.Rules.ONE_OR_MORE)
                .addRule(SectionSpecifier.Builder.Rules.CUSTOM, ".")
                .addRule(SectionSpecifier.Builder.Rules.DIGIT | SectionSpecifier.Builder.Rules.ZERO_OR_MORE)
                .addRule(SectionSpecifier.Builder.Rules.CUSTOM | SectionSpecifier.Builder.Rules.ONCE_OR_NONE, "dD")
                .or()

                .addRule(SectionSpecifier.Builder.Rules.CUSTOM, ".")
                .addRule(SectionSpecifier.Builder.Rules.DIGIT | SectionSpecifier.Builder.Rules.ONE_OR_MORE)
                .addRule(SectionSpecifier.Builder.Rules.CUSTOM | SectionSpecifier.Builder.Rules.ONCE_OR_NONE, "dD")
                .build();
    }

    @Override
    public boolean isType(PatternStrategyData patternStrategyData) {
        return false;
    }
}
