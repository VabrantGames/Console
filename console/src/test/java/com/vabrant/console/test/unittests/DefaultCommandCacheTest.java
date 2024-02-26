package com.vabrant.console.test.unittests;

import com.vabrant.console.CommandEngine.ClassReference;
import com.vabrant.console.CommandEngine.Command;
import com.vabrant.console.CommandEngine.DefaultCommandCache;
import com.vabrant.console.ConsoleRuntimeException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DefaultCommandCacheTest {

	@Test
	void ReferenceTest() {
		DefaultCommandCache cache = new DefaultCommandCache();

		final String ID = "test";
		cache.addReference(ID, new TestObject());

		assertNotNull(cache.getReference(ID));
		assertThrows(Exception.class, () -> {
			cache.addReference(ID, new TestObject());
		});
	}

	@Test
	void CommandTest() {
		DefaultCommandCache cache = new DefaultCommandCache();

		final String ID = "test";
		cache.addCommand(ID, new TestCommand());

		assertNotNull(cache.getCommand(ID));
	}

	@Test
	void MethodCommandTest()  {
		DefaultCommandCache cache = new DefaultCommandCache();

		final String ID = "test";
		ClassReference ref = cache.addReference(ID, new TestObject());
		cache.addCommand(ref, "hello", int.class);
		cache.addCommand(ref, "hello", String.class);

		assertNotNull(cache.getCommand(ref, "hello", int.class));
		assertNotNull(cache.getCommand(ref, "hello", String.class));
	}

	@Test
	void WrongMethodCommandType() {
		DefaultCommandCache cache = new DefaultCommandCache();

		final String ID = "test";

		// Owns hello command
		cache.addCommand("hello", new TestCommand());

		// Since owning command type if not of MethodCommandManager, method commands cannot be added
		assertThrows(ConsoleRuntimeException.class, () -> cache.addCommand(new TestObject(), "hello", int.class));
	}

	class TestObject {

		public void hello (int arg) {
			System.out.println("Hello: " + arg);
		}

		public void hello (String s) {
			System.out.println("String: " + s);
		}
	}

	class TestCommand implements Command {

		@Override
		public void setSuccessMessage (String message) {

		}

		@Override
		public String getSuccessMessage () {
			return null;
		}

		@Override
		public Class getReturnType () {
			return null;
		}

		@Override
		public Object execute (Object[] args) throws Exception {
			return null;
		}
	}
}
