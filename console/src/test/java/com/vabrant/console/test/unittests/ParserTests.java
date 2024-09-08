
package com.vabrant.console.test.unittests;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.utils.Array;
import com.vabrant.console.commandexecutor.*;
import com.vabrant.console.commandexecutor.parsers.*;
import com.vabrant.console.ConsoleRuntimeException;
import com.vabrant.console.DebugLogger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ParserTests {

	private static ParserContext parserContext;
	private static Application application;

	@BeforeAll
	public static void init () {
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		application = new HeadlessApplication(new ApplicationAdapter() {});
		parserContext = new DefaultParserContext();
	}

	@BeforeEach
	public void bob () {
		parserContext.setArgs(null);
		parserContext.setCache(null);
		parserContext.setText(null);
		parserContext.setGlobalCache(null);
	}

	@Test
	void FloatTest () {
		FloatArgumentParser parser = new FloatArgumentParser();
		assertDoesNotThrow( () -> setTextAndParse(parser, "15f"));
		assertDoesNotThrow( () -> setTextAndParse(parser, "-15f"));
		assertDoesNotThrow( () -> setTextAndParse(parser, ".15f"));
		assertDoesNotThrow( () -> setTextAndParse(parser, "15.0f"));
	}

	@Test
	void IntTest () {
		IntArgumentParser parser = new IntArgumentParser();
		assertDoesNotThrow( () -> setTextAndParse(parser, "100"));
		assertDoesNotThrow( () -> setTextAndParse(parser, "0x64"));
		assertDoesNotThrow( () -> setTextAndParse(parser, "#64"));
		assertDoesNotThrow( () -> setTextAndParse(parser, "0b01100100"));
	}

	@Test
	void DoubleTest () {
		DoubleArgumentParser parser = new DoubleArgumentParser();
		assertDoesNotThrow( () -> setTextAndParse(parser, "100.0"));
		assertDoesNotThrow( () -> setTextAndParse(parser, "100.0d"));
		assertDoesNotThrow( () -> setTextAndParse(parser, ".0d"));
	}

	@Test
	void LongTest () {
		LongArgumentParser parser = new LongArgumentParser();
		assertDoesNotThrow( () -> setTextAndParse(parser, "100l"));
		assertDoesNotThrow( () -> setTextAndParse(parser, "100L"));
		assertDoesNotThrow( () -> setTextAndParse(parser, "-100L"));
	}

	@Test
	void ClassReferenceTest () {
		Object ob1 = new Object();
		Object ob2 = new Object();
		ClassReferenceParser arg = new ClassReferenceParser();
		CommandCache cache = new DefaultCommandCache();
		cache.addReference("ob1", ob1);
		cache.addReference("ob2", ob2);
		parserContext.setCache(cache);

		assertDoesNotThrow( () -> setTextAndParse(arg, "ob1"));
		assertDoesNotThrow( () -> setTextAndParse(arg, "ob2"));
	}

	@Test
	void StringTest () {
		StringArgumentParser parser = new StringArgumentParser();

		String str1 = "Bob";
		String str2 = "\"" + str1 + "\"";
		assertDoesNotThrow( () -> setTextAndParse(parser, "str1"));
		assertDoesNotThrow( () -> setTextAndParse(parser, "str2"));
	}

	@Test
	void BooleanTest () {
		BooleanArgumentParser parser = new BooleanArgumentParser();
		assertDoesNotThrow( () -> setTextAndParse(parser, "true"));
		assertDoesNotThrow( () -> setTextAndParse(parser, "TRUE"));
		assertDoesNotThrow( () -> setTextAndParse(parser, "false"));
		assertDoesNotThrow( () -> setTextAndParse(parser, "FALSE"));
	}

	@Test
	void MethodParserTest () throws Exception {
		DefaultCommandCache cache = new DefaultCommandCache();
		cache.setLogLevel(DebugLogger.DEBUG);
		cache.addAll(new TestClass());
		cache.addCommand("hello", new CustomCommand());
		cache.addReference("bob", new Object());

		parserContext.setCache(cache);
// parserContext.setArgs(new Array<Object>());

		MethodParser parser = new MethodParser();

		// Test the command specifier without a reference
		assertEquals(cache.getCommand("hello"), setupParserContext(parser, ".hello", new Array<>()));
		assertEquals(cache.getMethodCommand("ref", "hello", int.class, String.class),
			setupParserContext(parser, ".hello", asArray(5, "Hello")));

		// Test the command specifier with a reference
		assertEquals(cache.getMethodCommand("ref", "hello", int.class, String.class),
			setupParserContext(parser, "ref.hello", asArray(5, "Hello")));
		assertThrows(ConsoleRuntimeException.class, () -> setupParserContext(parser, "ref.someCommand", asArray("Hello")));

		// Test no command specifier just the command name
		assertEquals(cache.getMethodCommand((String)null, "hello", String.class),
			setupParserContext(parser, "hello", asArray("Hello")));
	}

// @Test
// void GlobalClassReferenceTest () {
// DefaultCommandCache cache = new DefaultCommandCache();
// cache.addReference("obj", new Object());
//
// parserContext.setGlobalCache(cache);
//
// GlobalClassReferenceParser parser = new GlobalClassReferenceParser();
//
// assertDoesNotThrow(() -> setTextAndParse(parser, "obj"));
// }

	private Array<Object> asArray (Object... obj) {
		Array<Object> a = new Array<>(obj);
		a.reverse();
		return a;
	}

	private <T> T setTextAndParse (Parsable parser, String text) throws Exception {
		parserContext.setText(text);
		return (T)parser.parse(parserContext);
	}

	private <T> T setupParserContext (Parsable parsable, String commandName, Array<Object> args) throws Exception {
		parserContext.setText(commandName);
		parserContext.setArgs(args);
		return (T)parsable.parse(parserContext);
	}

	@ConsoleReference("ref")
	static class TestClass {

		@ConsoleCommand
		public void hello () {
		}

		@ConsoleCommand
		public void hello (int i) {
		}

		@ConsoleCommand
		public void hello (String str) {
		}

		@ConsoleCommand
		public void hello (int i, String str) {
		}
	}

	static class CustomCommand implements Command {

		@Override
		public void setSuccessMessage (String successMessage) {

		}

		@Override
		public String getSuccessMessage () {
			return "";
		}

		@Override
		public Object execute (Object[] args) throws Exception {
			return null;
		}

		@Override
		public Class getReturnType () {
			return null;
		}
	}
}
