package com.vabrant.console.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.StringBuilder;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.vabrant.console.Console;
import com.vabrant.console.ConsoleCommand;
import com.vabrant.console.executionstrategy.ExecutionStrategy;
import com.vabrant.console.executionstrategy.SimpleExecutionStrategy;
import com.vabrant.console.shortcuts.ShortcutManager;

public class GUIConsole extends Console {

    private final int TOGGLE_KEYBIND = 0;
    private final int EXECUTE_COMMAND_KEYBIND = Input.Keys.ENTER;
    private boolean isHidden;

    private int hideShowKeybindPacked;
    private Stage stage;
    private Table rootTable;
    StringBuilder builder;
    private ShortcutManager shortcutManager;
    private CommandLine commandLine;
    private ConsoleInputMultiplexer inputMultiplexer;

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
        commandLine = new CommandLine(this, skin);
        inputMultiplexer = new ConsoleInputMultiplexer(this);

        setToggleKeybind(new int[]{Input.Keys.GRAVE});
        shortcutManager.add(new int[]{Input.Keys.ENTER}, new ExecuteCommandCommand(this));

        rootTable = new Table(skin);
        rootTable.setFillParent(true);
        rootTable.pad(4);
        rootTable.add(commandLine).expand().fillX().bottom();
        stage.addActor(rootTable);

        setHidden(true);

        inputMultiplexer.add(new CloseWhenTouchedOutsideBounds());
        inputMultiplexer.add(shortcutManager);
        inputMultiplexer.add(commandLine.getInput());
    }

    public void setToggleKeybind(int[] keybind) {
        int packed = shortcutManager.add(keybind, new ToggleConsoleCommand(this));
        commandLine.setToggleKeybind(packed);
    }

    public InputProcessor getInput() {
        return inputMultiplexer;
    }

    public Stage getStage() {
        return stage;
    }

    public ShortcutManager getShortcutManager() {
        return shortcutManager;
    }

    CommandLine getCommandLine() {
        return commandLine;
    }

    public int addShortcut(int[] keysbind, ConsoleCommand command) {
        return shortcutManager.add(keysbind, command);
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
            stage.setKeyboardFocus(commandLine);
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

    private class CloseWhenTouchedOutsideBounds extends InputAdapter {
        @Override
        public boolean touchDown(int x, int y, int pointer, int button) {
            Actor actor = stage.hit(x, y, true);

            if (actor == null || !actor.equals(commandLine)) {
                setHidden(true);
                return true;
            }
            return false;
        }
    }

}
