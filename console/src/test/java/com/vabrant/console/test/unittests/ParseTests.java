
package com.vabrant.console.test.unittests;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.vabrant.console.commandextension.ClassReference;
import com.vabrant.console.commandextension.CommandData;
import com.vabrant.console.commandextension.CommandCache;
import com.vabrant.console.commandextension.DefaultCommandCache;
import com.vabrant.console.commandextension.parsers.*;
import com.vabrant.console.test.TestMethods;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ParseTests {

	private static ParserContext context;
	private static CommandData data;
	private static Application application;

	@BeforeAll
	public static void init () {
		application = new HeadlessApplication(new ApplicationAdapter() {});
		context = new ParserContext();
		data = new CommandData();
		context.setData(data);
	}

	@BeforeEach
	public void bob () {
		context.clear();
		data.setConsoleCache(null);
	}

	@Test
	void FloatTest () {
		FloatArgumentParser parser = new FloatArgumentParser();
		assertDoesNotThrow( () -> parser.parse(context.setText("15f")));
		assertDoesNotThrow( () -> parser.parse(context.setText(".15f")));
		assertDoesNotThrow( () -> parser.parse(context.setText("15.0f")));
	}

	@Test
	void IntTest () {
		IntArgumentParser parser = new IntArgumentParser();
		assertDoesNotThrow( () -> parser.parse(context.setText("100")));
		assertDoesNotThrow( () -> parser.parse(context.setText("0x64")));
		assertDoesNotThrow( () -> parser.parse(context.setText("#64")));
		assertDoesNotThrow( () -> parser.parse(context.setText("0b01100100")));
	}

	@Test
	void DoubleTest () {
		DoubleArgumentParser parser = new DoubleArgumentParser();
		assertDoesNotThrow( () -> parser.parse(context.setText("100.0")));
		assertDoesNotThrow( () -> parser.parse(context.setText("100.0d")));
		assertDoesNotThrow( () -> parser.parse(context.setText(".0d")));
	}

	@Test
	void LongTest () {
		LongArgumentParser parser = new LongArgumentParser();
		assertDoesNotThrow( () -> parser.parse(context.setText("100l")));
		assertDoesNotThrow( () -> parser.parse(context.setText("100L")));
	}

	@Test
	void ClassReferenceTest () {
		Object ob1 = new Object();
		Object ob2 = new Object();
		ClassReferenceParser arg = new ClassReferenceParser();
		CommandCache cache = new DefaultCommandCache();
		cache.addReference(ob1, "ob1");
		cache.addReference(ob2, "ob2");

		data.setConsoleCache(cache);

		assertDoesNotThrow( () -> arg.parse(context.setText("ob1")));
		assertDoesNotThrow( () -> arg.parse(context.setText("ob2")));
	}

	@Test
	void StringTest () {
		StringArgumentParser parser = new StringArgumentParser();

		String str1 = "Bob";
		String str2 = "\"" + str1 + "\"";
		assertEquals(str1, parser.parse(context.setText(str1)));
		assertEquals(str1, parser.parse(context.setText(str2)));
	}

	@Test
	void MethodTest () {
		CommandCache cache = new DefaultCommandCache();

		data.setConsoleCache(cache);

		cache.getLogger().setLevel(Logger.DEBUG);
		ClassReference<?> ref = cache.addReference(new TestMethods(), "bob");
		cache.addAll(ref);

		MethodParser parser = new MethodParser();

		// Zero arg method
		assertDoesNotThrow( () -> parser.parse(setupMethodParserContext("hello", new Array<>(new Object[] {}), context)));

		// Using method specifier '.' .method
		assertDoesNotThrow( () -> parser.parse(setupMethodParserContext(".print", new Array<>(new Object[] {5}), context)));

		// Reference + method reference.method
		assertDoesNotThrow( () -> parser.parse(setupMethodParserContext("bob.print", new Array<>(new Object[] {5}), context)));

		// No method specifier (First section of a MethodContainer)
		assertDoesNotThrow( () -> parser.parse(setupMethodParserContext("print", new Array<>(new Object[] {5}), context)));
	}

	ParserContext setupMethodParserContext (String str, Array<Object> args, ParserContext context) {
		context.setText(str);
		args.reverse();
		context.setArgs(args);
		return context;
	}

	@Test
	void BooleanTest () {
		BooleanArgumentParser parser = new BooleanArgumentParser();
		assertTrue(parser.parse(context.setText("true")));
		assertTrue(parser.parse(context.setText("TRUE")));
		assertFalse(parser.parse(context.setText("false")));
		assertFalse(parser.parse(context.setText("FALSE")));
	}
}
