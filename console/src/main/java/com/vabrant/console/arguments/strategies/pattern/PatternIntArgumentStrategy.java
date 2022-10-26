package com.vabrant.console.arguments.strategies.pattern;

import com.vabrant.console.SectionSpecifier;
import com.vabrant.console.arguments.Argument;
import com.vabrant.console.arguments.IntArgument;

public class PatternIntArgumentStrategy implements Argument.ArgumentStrategy<PatternStrategyData> {

    private SectionSpecifier specifier;

    public PatternIntArgumentStrategy() {
        specifier = new SectionSpecifier.Builder()
                .specifiedSection(IntArgument.class)

                //e.g 100
                .addRule(SectionSpecifier.Builder.Rules.DIGIT | SectionSpecifier.Builder.Rules.ONE_OR_MORE)
                .or()

                //e.g 0x64
                .addRule(SectionSpecifier.Builder.Rules.EXPLICT, "0x")
                .addRule(SectionSpecifier.Builder.Rules.CUSTOM | SectionSpecifier.Builder.Rules.ONE_OR_MORE, "a-fA-F0-9")
                .or()

                //e.g #64
                .addRule(SectionSpecifier.Builder.Rules.CUSTOM, "#")
                .addRule(SectionSpecifier.Builder.Rules.CUSTOM | SectionSpecifier.Builder.Rules.ONE_OR_MORE, "a-fA-F0-9")
                .or()

                //e.g 0b01100100
                .addRule(SectionSpecifier.Builder.Rules.EXPLICT, "0b")
                .addRule(SectionSpecifier.Builder.Rules.CUSTOM | SectionSpecifier.Builder.Rules.ONE_OR_MORE, "01")
                .build();
    }

    @Override
    public boolean isType(PatternStrategyData patternStrategyData) {
        return false;
    }
}
