package com.vabrant.console.gui;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.StringBuilder;
import com.vabrant.console.shortcuts.ShortcutManager;

public class CommandLine extends TextField {

    private int consoleToggleKeybindPacked;
    private int myCursor;
    private StringBuilder builder;
    private GUIConsole console;
    private CommandLineInput commandLineInput;

    public CommandLine(GUIConsole console, Skin skin) {
        super("", skin);
        clearListeners();
        setFocusTraversal(false);
        this.console = console;
        builder = new StringBuilder(200);
        commandLineInput = new CommandLineInput();
    }

    public void clearCommandLine() {
        myCursor = 0;
        builder.clear();
        setText("");
        setCursorPosition(0);
    }

    void moveCursor(int amt) {
        myCursor = MathUtils.clamp(myCursor + amt, 0, builder.length());
        setCursorPosition(myCursor);
    }

    public void setToggleKeybind(int packed) {
        consoleToggleKeybindPacked = packed;
    }

    public InputAdapter getInput() {
        return commandLineInput;
    }

    private class CommandLineInput extends InputAdapter {

        @Override
        public boolean keyDown(int keycode) {
            if (console.isHidden()) return false;

            switch (keycode) {
                case Input.Keys.LEFT:
                    moveCursor(-1);
                    return true;
                case Input.Keys.RIGHT:
                    moveCursor(1);
                    return true;
                case Input.Keys.HOME:
                    myCursor = 0;
                    setCursorPosition(myCursor);
                    return true;
                case Input.Keys.END:
                    myCursor = builder.length();
                    setCursorPosition(myCursor);
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public boolean keyTyped(char character) {
            ShortcutManager shortcutManager = console.getShortcutManager();
            if (shortcutManager.getCurrentlyPressedKeysPacked() == consoleToggleKeybindPacked) return true;
            if (console.isHidden()) return false;

            switch (character) {
                //Backspace
                case 8:
                    break;
                default:
                    if (character < 32) return false;
            }

            switch (character) {
                case 8:
                    if (builder.length() == 0) return false;
                    builder.deleteCharAt(myCursor - 1);
                    setText(builder.toString());
                    setCursorPosition(--myCursor);
                    return true;
                case '"':
                    builder.insert(myCursor, character);
                    builder.insert(myCursor, character);
                    setText(builder.toString());
                    setCursorPosition(++myCursor);
                    return true;
                default:
                    builder.insert(myCursor, character);
                    setText(builder.toString());
                    setCursorPosition(++myCursor);
                    return true;
            }
        }
    }

}
