
package com.vabrant.console.test.unittests;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.vabrant.console.ConsoleUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ConsoleUtilsTest {

	private static Application application;

	@BeforeAll
	public static void init () {
		application = new HeadlessApplication(new ApplicationAdapter() {});
	}

	@Test
	void areArgsExact () {
		Class[] args = {float.class, int.class};
		Class[] userArgs = {int.class, int.class};

		// Compatible but not the exact method
		assertTrue(ConsoleUtils.areArgsEqual(args, userArgs, false));

		assertFalse(ConsoleUtils.areArgsEqual(args, userArgs, true));

		assertTrue(ConsoleUtils.areArgsEqual(userArgs, userArgs, true));
	}

	@Test
	void AreArgsEqualsTest () {
		// Float can parse ints and longs
		assertTrue(
			isCompatible(new Class[] {int.class, long.class, float.class, Float.class, Integer.class, Long.class}, float.class));
		assertTrue(
			isCompatible(new Class[] {int.class, long.class, float.class, Float.class, Integer.class, Long.class}, Float.class));

		assertTrue(isCompatible(new Class[] {int.class, Integer.class}, int.class));
		assertTrue(isCompatible(new Class[] {int.class, Integer.class}, Integer.class));

		// Double can parse ints, longs and floats
		assertTrue(isCompatible(
			new Class[] {int.class, long.class, float.class, double.class, Double.class, Float.class, Integer.class, Long.class},
			double.class));
		assertTrue(isCompatible(
			new Class[] {int.class, long.class, float.class, double.class, Double.class, Float.class, Integer.class, Long.class},
			Double.class));

		// Long can parse ints
		assertTrue(isCompatible(new Class[] {int.class, long.class, Long.class, Integer.class}, long.class));
		assertTrue(isCompatible(new Class[] {int.class, long.class, Long.class, Integer.class}, Long.class));

		assertTrue(isCompatible(new Class[] {boolean.class, Boolean.class}, boolean.class));
		assertTrue(isCompatible(new Class[] {boolean.class, Boolean.class}, Boolean.class));
	}

	private boolean isCompatible (Class[] args, Class against) {
		for (Class arg : args) {
			if (!ConsoleUtils.areArgsEqual(new Class[] {against}, new Class[] {arg})) {
				System.err.println("Arg:" + against.getSimpleName() + " - User:" + arg.getSimpleName());
				return false;
			}
		}
		return true;
	}

}
