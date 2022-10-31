package com.vabrant.console.arguments.strategies.pattern;

import com.vabrant.console.arguments.Argument;

import java.util.regex.Pattern;

import static com.vabrant.console.arguments.strategies.pattern.PatternBuilder.*;

public class PatternFloatArgumentStrategy implements Argument.ArgumentStrategy<PatternStrategyInput> {

    private Pattern pattern;

    public PatternFloatArgumentStrategy() {
        pattern = PatternBuilder.getInstance()
                //e.g 100f
                .addRule(DIGIT | ONE_OR_MORE)
                .addRule(CUSTOM, "fF")
                .or()

                //e.g .100f
                .addRule(CUSTOM, ".")
                .addRule(DIGIT | ONE_OR_MORE)
                .addRule(CUSTOM, "fF")
                .or()

                //e.g 100.0f
                .addRule(DIGIT | ONE_OR_MORE)
                .addRule(CUSTOM, ".")
                .addRule(DIGIT | ZERO_OR_MORE)
                .addRule(CUSTOM, "fF")
                .build();
    }

    @Override
    public boolean isType(PatternStrategyInput patternStrategyInput) {
        return false;
    }
}
