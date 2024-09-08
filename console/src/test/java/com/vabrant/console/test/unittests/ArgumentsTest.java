
package com.vabrant.console.test.unittests;

import com.vabrant.console.commandexecutor.arguments.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ArgumentsTest {

	@Test
	void DoubleArgument () {
		DoubleArgument arg = new DoubleArgument();
		assertTrue(arg.isType("0d"));
		assertTrue(arg.isType("-6D"));
		assertTrue(arg.isType("554.43d"));
		assertTrue(arg.isType(".57680d"));

		assertFalse(arg.isType("6"));
		assertFalse(arg.isType("fjfiowjfowjf"));
		assertFalse(arg.isType("6f"));
	}

	@Test
	void IntArgument () {
		IntArgument arg = new IntArgument();
		assertTrue(arg.isType("0"));
		assertTrue(arg.isType("-15"));
		assertTrue(arg.isType("8020"));
		assertTrue(arg.isType("09073"));
		assertTrue(arg.isType("0898"));

		assertFalse(arg.isType("0.3"));
		assertFalse(arg.isType("0f"));
		assertFalse(arg.isType("jfiojwfowjo"));
	}

	@Test
	void FloatArgument () {
		FloatArgument arg = new FloatArgument();
		assertTrue(arg.isType("0f"));
		assertTrue(arg.isType("-.05f"));
		assertTrue(arg.isType("045.78"));
		assertTrue(arg.isType("0.4f"));
		assertTrue(arg.isType(".89F"));

		assertFalse(arg.isType("0"));
		assertFalse(arg.isType("0d"));
		assertFalse(arg.isType("gifjweofow"));
	}

	@Test
	void LongArgument () {
		LongArgument arg = new LongArgument();
		assertTrue(arg.isType("0l"));
		assertTrue(arg.isType("74824728L"));
		assertTrue(arg.isType("-3728L"));

		assertFalse(arg.isType("0.99L"));
		assertFalse(arg.isType("l898"));
		assertFalse(arg.isType("89"));
	}

	@Test
	void ClassReferenceArgument () {
		ClassReferenceArgument arg = new ClassReferenceArgument();
		assertTrue(arg.isType("object"));

		assertFalse(arg.isType("object.method"));
		assertFalse(arg.isType("0.4f"));
	}

	@Test
	void GlobalClassReferenceArgument () {
		GlobalClassReferenceArgument arg = new GlobalClassReferenceArgument();
		assertTrue(arg.isType("$name"));
	}

	@Test
	void StringArgument () {
		StringArgument arg = new StringArgument();
		assertTrue(arg.isType("\"Hello World\""));

		assertFalse(arg.isType("fowfjwof"));
	}

	@Test
	void BooleanArgument () {
		BooleanArgument arg = new BooleanArgument();
		assertTrue(arg.isType("TRUE"));
		assertTrue(arg.isType("faLse"));

		assertFalse(arg.isType("false0"));
	}

	@Test
	void MethodArgument () {
		MethodArgument arg = new MethodArgument();
		assertTrue(arg.isType(".method"));
		assertTrue(arg.isType("object.method"));

		assertFalse(arg.isType("0"));
	}

}
