
package com.vabrant.console.guitestlauncher;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3WindowConfiguration;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.io.File;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class GUITestLauncher {

	public static void main (String[] args) {

		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setWindowedMode(640, 480);
		config.setTitle("ConsoleGUITestLauncher");
		new Lwjgl3Application(new TestChooser(), config);
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface WindowSize {
		int width();

		int height();
	}

	static class TestChooser extends ApplicationAdapter {
		private Stage stage;
		private Skin skin;

		private ObjectMap<String, Class> mappedTests;

		private void addTest (Class c) {
			mappedTests.put(c.getSimpleName(), c);
		}

		private List<String> getTestNames () {
			List<String> names = new ArrayList<>(mappedTests.size);
			for (ObjectMap.Entry<String, Class> e : mappedTests.entries()) {
				names.add(e.key);
			}
			return names;
		}

		private ApplicationListener newTest (String name) {
			try {
				Class<? extends ApplicationListener> c = mappedTests.get(name);
				return c.getDeclaredConstructor().newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException | NoSuchMethodException e) {
				throw new RuntimeException(e);
			}
			return null;
		}

		public void create () {
			System.out.println("OpenGL renderer: " + Gdx.graphics.getGLVersion().getRendererString());
			System.out.println("OpenGL vendor: " + Gdx.graphics.getGLVersion().getVendorString());

			mappedTests = new ObjectMap<>();

			final String pathToTests = "console/src/test/java/com/vabrant/console/test/guitests";
			final String consoleDir = System.getProperty("user.dir");

			File[] files = new File(consoleDir, pathToTests).listFiles();

			if (files != null) {
				for (File f : files) {
					try {
						String path = f.getPath();
						String fullClassName = path.substring(path.indexOf("com/vabrant/console"), path.indexOf(".java"));

						Class<?> c = ClassReflection.forName(fullClassName.replaceAll("/", "."));

						if (ApplicationListener.class.isAssignableFrom(c)) {
							addTest(c);
						}
					} catch (Exception e) {
						e.printStackTrace();
						Gdx.app.exit();
					}
				}
			}

			stage = new Stage(new ScreenViewport());
			Gdx.input.setInputProcessor(stage);
			skin = new Skin(Gdx.files.internal("orangepeelui/uiskin.json"));

			Table container = new Table();
			stage.addActor(container);
			container.setFillParent(true);

			Table table = new Table();

			ScrollPane scroll = new ScrollPane(table, skin);
			scroll.setSmoothScrolling(false);
			scroll.setFadeScrollBars(false);
			stage.setScrollFocus(scroll);

			int tableSpace = 4;
			table.pad(10).defaults().expandX().space(tableSpace);
			for (final String testName : getTestNames()) {
				final TextButton testButton = new TextButton(testName, skin);
				testButton.setName(testName);
				table.add(testButton).fillX();
				table.row();
				testButton.addListener(new ChangeListener() {
					@Override
					public void changed (ChangeEvent event, Actor actor) {
						ApplicationListener test = newTest(testName);
						Lwjgl3WindowConfiguration winConfig = new Lwjgl3WindowConfiguration();
						winConfig.setTitle(testName);

						if (ClassReflection.isAnnotationPresent(test.getClass(), WindowSize.class)) {
							WindowSize ann = ClassReflection.getAnnotation(test.getClass(), WindowSize.class)
								.getAnnotation(WindowSize.class);
							winConfig.setWindowedMode(ann.width(), ann.height());
						} else {
							winConfig.setWindowedMode(500, 500);
						}

						winConfig.setWindowPosition(((Lwjgl3Graphics)Gdx.graphics).getWindow().getPositionX() + 40,
							((Lwjgl3Graphics)Gdx.graphics).getWindow().getPositionY() + 40);
						winConfig.useVsync(false);
						((Lwjgl3Application)Gdx.app).newWindow(test, winConfig);
						System.out.println("Started test: " + testName);
					}
				});
			}

			container.add(scroll).expand().fill();
			container.row();
		}

		@Override
		public void render () {
			ScreenUtils.clear(0, 0, 0, 1);
			stage.act();
			stage.draw();
		}

		@Override
		public void resize (int width, int height) {
			stage.getViewport().update(width, height, true);
		}

		@Override
		public void dispose () {
			skin.dispose();
			stage.dispose();
		}
	}
}
