package com.vabrant.console;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.vabrant.console.commandsections.DoubleArgument;
import com.vabrant.console.commandsections.FloatArgument;
import com.vabrant.console.commandsections.InstanceReferenceArgument;
import com.vabrant.console.commandsections.IntArgument;
import com.vabrant.console.commandsections.LongArgument;
import com.vabrant.console.commandsections.MethodArgument;
import com.vabrant.console.commandsections.StringArgument;

public class SpecifierTests {

	@Disabled
	@Test
	public void GroupTest() {
		Pattern pattern = Pattern.compile("int plus int( plus int)+");
		Matcher matcher = pattern.matcher("int plus int plus int plus int plus int plus int");
		assertTrue(matcher.find());
	}
	
	@ParameterizedTest
	@ValueSource(strings = {
			"object123.method",
			".m3thod123"
	})
	public void MethodArgumentSpecifierTest(String s) {
		SectionSpecifier specifier = MethodArgument.createSpecifier();
		Matcher matcher = specifier.getPattern().matcher("");
		assertTrue(matcher.reset(s).matches());
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"100f",
			".100f",
			"100.0F"
	})
	public void FloatSpecifierTest(String s) {
		SectionSpecifier specifier = FloatArgument.createSpecifier();
		Matcher matcher = specifier.getPattern().matcher("");
		assertTrue(matcher.reset(s).matches());
	}
	
	@ParameterizedTest
	@ValueSource(strings = {
			"100.",
			"100.d",
			".100"
	})
	public void DoubleSpecifierTest(String s) {
		SectionSpecifier specifier = DoubleArgument.createSpecifier();
		Matcher matcher = specifier.getPattern().matcher("");
		assertTrue(matcher.reset(s).matches());
	}
	
	@ParameterizedTest
	@ValueSource(strings = {
			"100",
			"0x64",
			"#64",
			"01100100"
	})
	public void IntSpecifierTest(String s) {
		SectionSpecifier specifier = IntArgument.createSpecifier();
		Matcher matcher = specifier.getPattern().matcher("");
		assertTrue(matcher.reset(s).matches());
	}
	
	@ParameterizedTest
	@ValueSource(strings = {
			"50l",
			"1050L"
	})
	public void LongSpecifierTest(String s) {
		SectionSpecifier specifier = LongArgument.createSpecifier();
		Matcher matcher = specifier.getPattern().matcher("");
		assertTrue(matcher.reset(s).matches());
	}
	
	@ParameterizedTest
	@ValueSource(strings = {
			"someObject123"
	})
	public void ObjectSpecifierTest(String s) {
		SectionSpecifier specifier = InstanceReferenceArgument.createSpecifier();
		Matcher matcher = specifier.getPattern().matcher("");
		assertTrue(matcher.reset(s).matches());
	}
	
	@Test
	public void StringSpecifierTest() {
		SectionSpecifier specifier = StringArgument.createSpecifier();
		Matcher matcher = specifier.getPattern().matcher("");
		String s = "\" Hello World 0123456789 ~`!@#$%^&*()_+{}[]|:<>? \"";
		assertTrue(matcher.reset(s).matches());
	}
			

}
