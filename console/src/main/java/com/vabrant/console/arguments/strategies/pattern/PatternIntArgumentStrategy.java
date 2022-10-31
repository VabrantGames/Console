package com.vabrant.console.arguments.strategies.pattern;

import com.vabrant.console.arguments.Argument;

import java.util.regex.Pattern;

import static com.vabrant.console.arguments.strategies.pattern.PatternBuilder.*;

public class PatternIntArgumentStrategy implements Argument.ArgumentStrategy<PatternStrategyInput> {

    private Pattern pattern;

    public PatternIntArgumentStrategy() {
        pattern = PatternBuilder.getInstance()
                //e.g 100
                .addRule(DIGIT | ONE_OR_MORE)
                .or()

                //e.g 0x64
                .addRule(EXPLICT, "0x")
                .addRule(CUSTOM | ONE_OR_MORE, "a-fA-F0-9")
                .or()

                //e.g #64
                .addRule(CUSTOM, "#")
                .addRule(CUSTOM | ONE_OR_MORE, "a-fA-F0-9")
                .or()

                //e.g 0b01100100
                .addRule(EXPLICT, "0b")
                .addRule(CUSTOM | ONE_OR_MORE, "01")
                .build();
    }

    @Override
    public boolean isType(PatternStrategyInput patternStrategyInput) {
        return false;
    }
}
