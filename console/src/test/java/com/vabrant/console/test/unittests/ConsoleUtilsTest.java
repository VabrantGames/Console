
package com.vabrant.console.test.unittests;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.vabrant.console.ConsoleUtils.areArgsEqual;
import static org.junit.jupiter.api.Assertions.*;

public class ConsoleUtilsTest {

	private static Application application;

	@BeforeAll
	public static void init () {
		application = new HeadlessApplication(new ApplicationAdapter() {});
	}

	@Test
	void areArgsEqualsTest () {
		assertTrue(areArgsEqual(new Class[] {int.class}, new Class[] {int.class}));
	}
}
