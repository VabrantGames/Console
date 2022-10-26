package com.vabrant.console.arguments.strategies.pattern;

import com.vabrant.console.SectionSpecifier;
import com.vabrant.console.arguments.Argument;
import com.vabrant.console.arguments.InstanceReferenceArgument;

public class PatternInstanceReferenceArgumentStrategy implements Argument.ArgumentStrategy<PatternStrategyData> {

    private SectionSpecifier specifier;

    public PatternInstanceReferenceArgumentStrategy() {
        specifier = new SectionSpecifier.Builder()
                .specifiedSection(InstanceReferenceArgument.class)
                .addRule(SectionSpecifier.Builder.Rules.CHARACTER)
                .addRule(SectionSpecifier.Builder.Rules.CHARACTER | SectionSpecifier.Builder.Rules.DIGIT | SectionSpecifier.Builder.Rules.ZERO_OR_MORE)
                .build();
    }

    @Override
    public boolean isType(PatternStrategyData patternStrategyData) {
        return false;
    }
}
