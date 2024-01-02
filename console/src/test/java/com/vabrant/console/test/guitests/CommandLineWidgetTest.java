package com.vabrant.console.test.guitests;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.vabrant.console.commandextension.gui.CommandLineWidget;

public class CommandLineWidgetTest extends ApplicationAdapter {

	private Skin skin;
	private Stage stage;

	@Override
	public void create () {
		skin = new Skin(Gdx.files.classpath("defaultskin/tinted/tinted.json"));
		stage = new Stage(new ScreenViewport());

		changeInputType(false);

		Gdx.input.setInputProcessor(stage);
	}

	private void changeInputType(boolean customInput) {
		stage.clear();

		Table t = new Table();
		t.setFillParent(true);

		TextButton normalInputButton = new TextButton("Normal", skin);
		normalInputButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				changeInputType(false);
			}
		});
		t.add(normalInputButton).expandY().top().padRight(10);

		TextButton customInputButton = new TextButton("CustomInput", skin);
		customInputButton.addListener(new ChangeListener() {
			@Override
			public void changed (ChangeEvent event, Actor actor) {
				changeInputType(true);
			}
		});
		t.add(customInputButton).top();
		stage.addActor(t);

		CommandLineWidget w = new CommandLineWidget(null, skin, customInput);
		Container c = new Container();
		c.setFillParent(true);
		c.setActor(w.getTextField());
		stage.addActor(c);
	}

	@Override
	public void render () {
		ScreenUtils.clear(Color.WHITE);
		stage.act();
		stage.draw();
	}
}
