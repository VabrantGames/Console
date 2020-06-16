package de.tomgrill.gdxtesting;

import java.util.regex.MatchResult;
import java.util.regex.Matcher;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.vabrant.console.DebugLogger;
import com.vabrant.console.SectionSpecifier;
import com.vabrant.console.SectionSpecifier.Builder;
import com.vabrant.console.commandsections.MethodSection;

@RunWith(GdxTestRunner.class)
public class SpecifierTests {
	
	@BeforeClass
	public static void init() {
		DebugLogger.usSysOut();
	}

	@Test
	public void buildTest() {
		SectionSpecifier p = new SectionSpecifier.Builder()
				.specifiedSection(MethodSection.class)
				.specifier(".")
				.setRule(Builder.Rule.ANY_CHARACTER)
				.setRule(Builder.Rule.CHARACTER_OR_DIGIT, Builder.Quantifiers.ZERO_OR_MORE)
				.setRule(Builder.Rule.SPECIFIER)
				.setRule(Builder.Rule.ANY_CHARACTER)
				.setRule(Builder.Rule.CHARACTER_OR_DIGIT, Builder.Quantifiers.ZERO_OR_MORE)
				.setRule(Builder.Rule.OR)
				.setRule(Builder.Rule.SPECIFIER)
				.setRule(Builder.Rule.ANY_CHARACTER)
				.setRule(Builder.Rule.CHARACTER_OR_DIGIT, Builder.Quantifiers.ZERO_OR_MORE)
				.build();
		
		final String s = "object.method";
		Matcher matcher = p.getPattern().matcher(s);
		
		if(matcher.lookingAt()) {
			System.out.println(matcher.end());
			if(matcher.end() == s.length()) System.out.println("Hello bob"); 
		}
		else if(matcher.hitEnd()) {
			System.out.println("partial");
		}
		else {
			System.out.println("no");
		}
	}

}
