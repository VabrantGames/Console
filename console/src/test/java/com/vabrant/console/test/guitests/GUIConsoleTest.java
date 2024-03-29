
package com.vabrant.console.test.guitests;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.kotcrab.vis.ui.VisUI;
import com.vabrant.console.DebugLogger;
import com.vabrant.console.gui.*;
import com.vabrant.console.commandextension.annotation.ConsoleReference;
import com.vabrant.console.gui.shortcuts.DefaultKeyMap;
import com.vabrant.console.gui.shortcuts.KeyMap;
import com.vabrant.console.test.GUITestLauncher.WindowSize;

@ConsoleReference
@WindowSize(width = 1080, height = 720)
public class GUIConsoleTest extends ApplicationAdapter {

	private GUIConsole console;

	@Override
	public void create () {
		console = new DefaultGUIConsole();
		console.getLogger().setLevel(DebugLogger.DEBUG);
		console.addShortcut( () -> System.out.println("Hello Console"), new int[] {Input.Keys.NUM_1});

		final String view1 = "View1";
		final String view2 = "View2";

		TestPanel p = new TestPanel(view1 + "TestPanel");
		p.getKeyMap().add( () -> System.out.println("Hello " + view1 + "TestPanel1"), new int[] {Keys.NUM_5});
		DefaultView v = new WindowView(view1, VisUI.getSkin(), p);
		v.setSizePercent(50, 50);
		v.getLogger().setLevel(DebugLogger.DEBUG);
		console.addView(v);
		v.setHidden(false);

		WindowView vv = new WindowView(view2, console.getSkin());
		vv.setSizePercent(50, 50);
		vv.getLogger().setLevel(DebugLogger.DEBUG);
		p = new TestPanel(view2 + "TestPanel1");
		p.<DefaultKeyMap> getKeyMap().add( () -> System.out.println("Hello " + view2 + "TestPanel1"), new int[] {Keys.NUM_5});
		vv.addPanel(p);
		p = new TestPanel(view2 + "TestPanel2");
		p.<DefaultKeyMap> getKeyMap().add( () -> System.out.println("Hello " + view2 + "TestPanel2"), new int[] {Keys.NUM_5});
		vv.addPanel(p);
		console.addView(vv);
		vv.setHidden(false);

		TestFocusObject focusObject = new TestFocusObject();

		console.addShortcut( () -> {
			if (console.getFocusObject().equals(focusObject)) {
				console.removeFocusObject(focusObject);
			} else {
				console.focus(focusObject);
			}
		}, new int[] {Keys.NUM_9});

		Gdx.input.setInputProcessor(console.getInput());
	}

	private static class TestFocusObject implements FocusObject {

		@Override
		public void focus () {
			System.out.println("Hello TestFocusObject");
		}

		@Override
		public void unfocus () {
			System.out.println("Goodbye TestFocusObject");
		}

		@Override
		public ConsoleScope getScope () {
			return null;
		}

		@Override
		public KeyMap getKeyMap () {
			return null;
		}

		@Override
		public boolean lockFocus () {
			return true;
		}

		@Override
		public String getName () {
			return "TestFocusObject";
		}
	}

	private static class TestPanel extends Panel<Table, DefaultKeyMap> {
		protected TestPanel (String name) {
			super(name, Table.class, DefaultKeyMap.class);
		}
	}

	@Override
	public void resize (int width, int height) {
		console.resize(width, height);
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		console.draw();
	}

}
