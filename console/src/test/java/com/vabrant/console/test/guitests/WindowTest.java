
package com.vabrant.console.test.guitests;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class WindowTest extends ApplicationAdapter {

	private Stage stage;
	private Skin skin;

	@Override
	public void create () {
		stage = new Stage(new ScreenViewport());
		skin = new Skin(Gdx.files.classpath("orangepeelui/uiskin.json"));

		Window window = new Window("", skin);
		window.setMovable(true);
		window.setResizable(true);
		window.setKeepWithinStage(true);
		window.setModal(false);

		Table root = new Table();
		root.setFillParent(true);

// ScrollPane scrollPane = new ScrollPane(new Table(), skin);

		TextField textField = new TextField("textfield", skin);

		window.defaults();
// root.add(scrollPane).expand().fill();
// root.row();
// textField.setFillParent(true);
		root.add(textField).expand().fillX().bottom();

// window.addActor(root);
		stage.addActor(root);
// stage.addActor(window);
		stage.isDebugAll();

		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		stage.act();
		stage.draw();
	}
}
