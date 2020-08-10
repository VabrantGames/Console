package de.tomgrill.gdxtesting;

import static org.junit.Assert.assertTrue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import com.vabrant.console.DebugLogger;
import com.vabrant.console.SectionSpecifier;
import com.vabrant.console.commandsections.DoubleArgument;
import com.vabrant.console.commandsections.FloatArgument;
import com.vabrant.console.commandsections.IntArgument;
import com.vabrant.console.commandsections.LongArgument;
import com.vabrant.console.commandsections.MethodArgument;
import com.vabrant.console.commandsections.InstanceReferenceArgument;
import com.vabrant.console.commandsections.StringArgument;

public class SpecifierTests {

	@Test
	public void GroupTest() {
		Pattern pattern = Pattern.compile("int plus int( plus int)+");
		Matcher matcher = pattern.matcher("int plus int plus int plus int plus int plus int");
		
		assertTrue(matcher.find());
	}
	
	@Test
	public void MethodArgumentSpecifierTest() {
		SectionSpecifier specifier = MethodArgument.createSpecifier();
		Matcher matcher = specifier.getPattern().matcher("");
		String s = null;
		
		s = "object1.method";
		matcher.reset(s);
		assertTrue(matcher.matches());
		
		s = ".m3thod";
		matcher.reset(s);
		assertTrue(matcher.matches());
	}

	@Test
	public void FloatSpecifierTest() {
		SectionSpecifier specifier = FloatArgument.createSpecifier();
		Matcher matcher = specifier.getPattern().matcher("");
		String s = null;
		
		s = "100f";
		matcher.reset(s);
		assertTrue(matcher.matches());
		
		s = ".100f";
		matcher.reset(s);
		assertTrue(matcher.matches());
		
		s = "100.0F";
		matcher.reset(s);
		assertTrue(matcher.matches());
	}
	
	@Test
	public void DoubleSpecifierTest() {
		SectionSpecifier specifier = DoubleArgument.createSpecifier();
		Matcher matcher = specifier.getPattern().matcher("");
		String s = null;
		
		s = "100.";
		matcher.reset(s);
		assertTrue(matcher.matches());
		
		s = "100.d";
		matcher.reset(s);
		assertTrue(matcher.matches());
		
		s = ".100";
		matcher.reset(s);
		assertTrue(matcher.matches());
	}
	
	@Test
	public void IntSpecifierTest() {
		SectionSpecifier specifier = IntArgument.createSpecifier();
		Matcher matcher = specifier.getPattern().matcher("");
		String s = null;
		
		s = "100";
		matcher.reset(s);
		assertTrue(matcher.matches());
		
		s = "0x64";
		matcher.reset(s);
		assertTrue(matcher.matches());
		
		s = "#64";
		matcher.reset(s);
		assertTrue(matcher.matches());
		
		s = "01100100";
		matcher.reset(s);
		assertTrue(matcher.matches());
	}
	
	@Test
	public void LongSpecifierTest() {
		SectionSpecifier specifier = LongArgument.createSpecifier();
		Matcher matcher = specifier.getPattern().matcher("");
		String s = null;
		
		s = "50l";
		matcher.reset(s);
		assertTrue(matcher.matches());
	}
	
	@Test
	public void ObjectSpecifierTest() {
		SectionSpecifier specifier = InstanceReferenceArgument.createSpecifier();
		Matcher matcher = specifier.getPattern().matcher("");
		String s = null;
			
		s = "someObject2";
		matcher.reset(s);
		assertTrue(matcher.matches());
	}
	
	@Test
	public void StringSpecifierTest() {
		SectionSpecifier specifier = StringArgument.createSpecifier();
		Matcher matcher = specifier.getPattern().matcher("");
		String s = null;
		
		s = "\" Hello World 0123456789 ~`!@#$%^&*()_+{}[]|:<>? \"";
		matcher.reset(s);
		assertTrue(matcher.matches());
	}
			

}
