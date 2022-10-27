package com.vabrant.console;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.vabrant.console.executionstrategy.ExecutionStrategy;

public class GUIConsole extends Console {

    private boolean isHidden = true;
    private Skin skin;
    private Stage stage;
    private TextField textField;
    private ConsoleInput input;

    public GUIConsole(ExecutionStrategy strategy) {
        this(strategy, null, null);
    }

    public GUIConsole(ExecutionStrategy strategy, Skin skin) {
        this(strategy, null, skin);
    }

    public GUIConsole(ExecutionStrategy strategy, Batch batch, Skin skin) {
        super(strategy);

        input = new ConsoleInput();

        if (batch == null) {
            stage = new Stage(new ScreenViewport());
        } else {
            stage = new Stage(new ScreenViewport(), batch);
        }

        Table root = new Table();
        root.setFillParent(true);

        textField = new TextField("", skin);
        textField.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ENTER) {
                    if (execute(textField.getText())) {
                        textField.setText("");
                    }
                }
                return false;
            }
        });
        textField.setTouchable(Touchable.disabled);

        root.add(textField).expand().fillX().bottom();

        stage.addActor(root);
    }

    public InputProcessor getInput() {
        InputMultiplexer m = new InputMultiplexer();
        m.addProcessor(input);
        m.addProcessor(stage);
        return m;
    }

    public Stage getStage() {
        return stage;
    }

    public void setHidden(boolean hidden) {
        isHidden = hidden;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    public void draw() {
        if (isHidden) return;
        stage.act();
        stage.draw();
    }

    private class ConsoleInput extends InputAdapter {

        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            Actor actor = stage.hit(screenX, Gdx.graphics.getHeight() - screenY, false);

            if (actor != null && actor.equals(textField)) {
                System.out.println("Hit text field");
            } else {
                setHidden(true);
            }

            return super.touchDown(screenX, screenY, pointer, button);
        }

        @Override
        public boolean keyDown(int keycode) {
            switch (keycode) {
                case Input.Keys.ESCAPE:
                    if (isHidden()) return false;
                    setHidden(true);
                    stage.setKeyboardFocus(null);
                    textField.setTouchable(Touchable.disabled);
                    System.out.println("hide");
                    break;
                case Input.Keys.F12:
                    if (!isHidden()) return false;
                    setHidden(false);
                    stage.setKeyboardFocus(textField);
                    textField.setTouchable(Touchable.enabled);
                    break;
            }
            return super.keyDown(keycode);
        }
    }
}
