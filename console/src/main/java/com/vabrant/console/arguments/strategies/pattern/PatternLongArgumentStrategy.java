package com.vabrant.console.arguments.strategies.pattern;

import com.vabrant.console.arguments.Argument;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.vabrant.console.arguments.strategies.pattern.PatternBuilder.*;

public class PatternLongArgumentStrategy implements Argument.ArgumentStrategy<PatternStrategyInput> {

    private Pattern pattern;

    public PatternLongArgumentStrategy() {
        pattern = PatternBuilder.getInstance()
                .addRule(DIGIT | ONE_OR_MORE)
                .addRule(CUSTOM, "lL")
                .build();
    }

    @Override
    public boolean isType(PatternStrategyInput d) {
        Matcher matcher = d.getMatcher();
        matcher.usePattern(pattern);
        matcher.reset(d.getText());
        return matcher.matches();
    }
}
