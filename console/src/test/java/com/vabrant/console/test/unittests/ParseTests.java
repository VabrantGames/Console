
package com.vabrant.console.test.unittests;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.vabrant.console.CommandEngine.CommandCache;
import com.vabrant.console.CommandEngine.DefaultCommandCache;
import com.vabrant.console.CommandEngine.parsers.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ParseTests {

	private static ParserContext context;
	private static Application application;

	@BeforeAll
	public static void init () {
		application = new HeadlessApplication(new ApplicationAdapter() {});
		context = new DefaultParserContext();
	}

	@BeforeEach
	public void bob () {
		context.setArgs(null);
		context.setCache(null);
		context.setText(null);
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
		context.setCache(cache);

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

	private <T> T setTextAndParse (Parsable parser, String text) throws Exception {
		context.setText(text);
		return (T)parser.parse(context);
	}
}
