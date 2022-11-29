package com.vabrant.console;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.StringBuilder;
import com.vabrant.console.executionstrategy.ExecutionStrategy;
import com.vabrant.console.executionstrategy.SimpleExecutionStrategy;
import com.vabrant.console.shortcuts.ShortcutManager;

public class GUIConsole extends Console {

    private final int HIDE_SHOW_KEYBIND = Input.Keys.GRAVE;
    private final int EXECUTE_COMMAND_KEYBIND = Input.Keys.ENTER;
    private boolean isHidden;
    private Stage stage;
    private TextField textField;
    private Table rootTable;
    private StringBuilder builder;
    private ShortcutManager shortcutManager;

    public GUIConsole() {
        this(null, null, new Skin(Gdx.files.classpath("orangepeelui/uiskin.json")));
    }

    public GUIConsole(Batch batch) {
       this(null, batch, new Skin(Gdx.files.classpath("orangepeelui/uiskin.json")));
    }

    public GUIConsole(ExecutionStrategy strategy, Batch batch, Skin skin) {
        super(strategy == null ? new SimpleExecutionStrategy() : strategy);

        if (batch == null) {
            stage = new Stage(new ScreenViewport());
        } else {
            stage = new Stage(new ScreenViewport(), batch);
        }

        builder = new StringBuilder();
        shortcutManager = new ShortcutManager();

        rootTable = new Table(skin);
        rootTable.setFillParent(true);
        rootTable.pad(4);

        textField = new TextField("", skin);
        textField.setFocusTraversal(false);
        textField.setTextFieldListener(new TextField.TextFieldListener() {
            @Override
            public void keyTyped(TextField textField, char c) {
                if (Character.toString(c).equalsIgnoreCase(Input.Keys.toString(HIDE_SHOW_KEYBIND))) {
                    String s = textField.getText();
                    textField.setText(s.substring(0, s.length() - 1));
                } else if (c == '"'){
                    int cursorPosition = textField.getCursorPosition();
                    String commandStr = textField.getText();

                    builder.clear();
                    builder.append(commandStr.substring(0, cursorPosition));
                    builder.append('"');

                    if (cursorPosition != commandStr.length() - 1) {
                        builder.append(commandStr.substring(cursorPosition, commandStr.length()));
                    }

                    textField.setText(builder.toString());
                    textField.setCursorPosition(cursorPosition);
                }
            }
        });

        rootTable.add(textField).expand().fillX().bottom();
        stage.addActor(rootTable);

        setHidden(true);

        stage.addListener(new MainInput());
    }

    public InputProcessor getInput() {
        return stage;
    }

    public Stage getStage() {
        return stage;
    }

    public void setHidden(boolean hidden) {
        if (isHidden() == hidden) return;
        isHidden = hidden;

        if (hidden) {
            rootTable.setTouchable(Touchable.disabled);
            rootTable.setVisible(false);
            stage.setKeyboardFocus(null);
        } else {
            rootTable.setTouchable(Touchable.enabled);
            rootTable.setVisible(true);
            stage.setKeyboardFocus(textField);
        }
    }

    public boolean isHidden() {
        return isHidden;
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    public void draw() {
        stage.act();

        if (isHidden()) return;

        stage.getViewport().apply();
        stage.draw();
    }

    private class MainInput extends InputListener {
        @Override
        public boolean keyDown(InputEvent event, int keycode) {
            switch (keycode) {
                case EXECUTE_COMMAND_KEYBIND:
                    if (isHidden()) break;
                    execute(textField.getText());
                    textField.setText("");
                    return true;
                case HIDE_SHOW_KEYBIND:
                    setHidden(!isHidden());
                    return true;
            }
            return false;
        }

        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            Actor actor = stage.hit(x, y, true);

            if (actor == null || !actor.equals(textField)) {
                setHidden(true);
                return true;
            }

            return false;
        }
    }

}
