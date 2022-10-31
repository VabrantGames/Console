package com.vabrant.console.arguments.strategies.pattern;

import com.vabrant.console.arguments.Argument;

import java.util.regex.Pattern;

import static com.vabrant.console.arguments.strategies.pattern.PatternBuilder.*;

public class PatternMethodArgumentStrategy implements Argument.ArgumentStrategy<PatternStrategyInput> {

    private Pattern pattern;

    public PatternMethodArgumentStrategy() {
        pattern = PatternBuilder.getInstance()
                .addRule(CHARACTER)
                .addRule(CHARACTER | DIGIT | ZERO_OR_MORE)
                .addRule(CUSTOM, ".")
                .addRule(CHARACTER)
                .addRule(CHARACTER | DIGIT | ZERO_OR_MORE)
                .or()

                .addRule(CUSTOM, ".")
                .addRule(CHARACTER)
                .addRule(CHARACTER | DIGIT | ZERO_OR_MORE)
                .build();
    }

    @Override
    public boolean isType(PatternStrategyInput patternStrategyInput) {
        return false;
    }
}
