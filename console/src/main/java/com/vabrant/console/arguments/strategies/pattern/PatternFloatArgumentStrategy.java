package com.vabrant.console.arguments.strategies.pattern;

import com.vabrant.console.SectionSpecifier;
import com.vabrant.console.arguments.Argument;
import com.vabrant.console.arguments.FloatArgument;

public class PatternFloatArgumentStrategy implements Argument.ArgumentStrategy<PatternStrategyInput> {

    private final SectionSpecifier specifier;

    public PatternFloatArgumentStrategy() {
        specifier = new SectionSpecifier.Builder()
                .specifiedSection(FloatArgument.class)

                //e.g 100f
                .addRule(SectionSpecifier.Builder.Rules.DIGIT | SectionSpecifier.Builder.Rules.ONE_OR_MORE)
                .addRule(SectionSpecifier.Builder.Rules.CUSTOM, "fF")
                .or()

                //e.g .100f
                .addRule(SectionSpecifier.Builder.Rules.CUSTOM, ".")
                .addRule(SectionSpecifier.Builder.Rules.DIGIT | SectionSpecifier.Builder.Rules.ONE_OR_MORE)
                .addRule(SectionSpecifier.Builder.Rules.CUSTOM, "fF")
                .or()

                //e.g 100.0f
                .addRule(SectionSpecifier.Builder.Rules.DIGIT | SectionSpecifier.Builder.Rules.ONE_OR_MORE)
                .addRule(SectionSpecifier.Builder.Rules.CUSTOM, ".")
                .addRule(SectionSpecifier.Builder.Rules.DIGIT | SectionSpecifier.Builder.Rules.ZERO_OR_MORE)
                .addRule(SectionSpecifier.Builder.Rules.CUSTOM, "fF")
                .build();
    }

    @Override
    public boolean isType(PatternStrategyInput patternStrategyInput) {
        return false;
    }
}
