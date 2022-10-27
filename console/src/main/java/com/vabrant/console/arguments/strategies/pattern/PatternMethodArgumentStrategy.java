package com.vabrant.console.arguments.strategies.pattern;

import com.vabrant.console.SectionSpecifier;
import com.vabrant.console.arguments.Argument;
import com.vabrant.console.arguments.MethodArgument;

public class PatternMethodArgumentStrategy implements Argument.ArgumentStrategy<PatternStrategyInput> {

    private SectionSpecifier specifier;

    public PatternMethodArgumentStrategy() {
        specifier = new SectionSpecifier.Builder()
                .specifiedSection(MethodArgument.class)

                .addRule(SectionSpecifier.Builder.Rules.CHARACTER)
                .addRule(SectionSpecifier.Builder.Rules.CHARACTER | SectionSpecifier.Builder.Rules.DIGIT | SectionSpecifier.Builder.Rules.ZERO_OR_MORE)
                .addRule(SectionSpecifier.Builder.Rules.CUSTOM, ".")
                .addRule(SectionSpecifier.Builder.Rules.CHARACTER)
                .addRule(SectionSpecifier.Builder.Rules.CHARACTER | SectionSpecifier.Builder.Rules.DIGIT | SectionSpecifier.Builder.Rules.ZERO_OR_MORE)
                .or()

                .addRule(SectionSpecifier.Builder.Rules.CUSTOM, ".")
                .addRule(SectionSpecifier.Builder.Rules.CHARACTER)
                .addRule(SectionSpecifier.Builder.Rules.CHARACTER | SectionSpecifier.Builder.Rules.DIGIT | SectionSpecifier.Builder.Rules.ZERO_OR_MORE)
                .build();
    }

    @Override
    public boolean isType(PatternStrategyInput patternStrategyInput) {
        return false;
    }
}
