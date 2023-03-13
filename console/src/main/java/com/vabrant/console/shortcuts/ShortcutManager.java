package com.vabrant.console.shortcuts;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.utils.IntMap;
import com.vabrant.console.ConsoleCommand;

import java.util.Arrays;

public class ShortcutManager extends InputAdapter {
    //========== Guide ==========//
    //0 = Control
    //1 = Shift
    //2 = Alt
    //3 = key

    private static final int MAX_KEYS = 4;

    private boolean dirty;
    private int currentlyPressedKeysPacked;
    private final int[] packHelper;
    private final int[] pressedKeys;
    private final IntMap<ConsoleCommand> shortcuts;

    public ShortcutManager() {
        shortcuts = new IntMap<>();
        pressedKeys = new int[MAX_KEYS];
        packHelper = new int[MAX_KEYS];
    }

    public int getCurrentlyPressedKeysPacked() {
        return currentlyPressedKeysPacked;
    }

    public int add(int[] keys, ConsoleCommand command) {
        if (command == null) throw new IllegalArgumentException("Command con not be null.");

        isValidKeybind(keys);

        Arrays.fill(packHelper, 0);
        for (int i : keys) {
            setKey(packHelper, i);
        }

        int packed = packKeys(keys);
        shortcuts.put(packed, command);
        return packed;
    }

    /**
     * Replaces the keybind of an existing command.
     *
     * @param oldKeybind
     * @param newKeybind
     * @return newKeybind if oldKeybind exists, otherwise oldKeybind
     */
    public int replace(int oldKeybind, int[] newKeybind) {
        ConsoleCommand command = shortcuts.remove(oldKeybind);
        if (command == null) return oldKeybind;
        return add(newKeybind, command);
    }

    public boolean contains(int packedKeybind) {
        return shortcuts.containsKey(packedKeybind);
    }

    public ConsoleCommand remove(int packedKeybind) {
        return shortcuts.remove(packedKeybind);
    }

    //Only modifiers is invalid
    //Empty is invalid
    //Using a restricted keybind is invalid
    //Only one non modifier key is allowed
    private void isValidKeybind(int[] keys) {
        if (keys == null || keys.length == 0 || keys.length > MAX_KEYS) {
            throw new InvalidShortcutException("Keybind must not be null and have a length between 0 and " + MAX_KEYS);
        }

        boolean allModifiers = true;
        boolean hasNormalKey = false;
        boolean hasShift = false;
        boolean hasAlt = false;
        boolean hasControl = false;

        for (int i = 0; i < keys.length; i++) {
            //Treat left and right modifier keys the same
            switch (keys[i]) {
                case Input.Keys.ALT_LEFT:
                case Input.Keys.ALT_RIGHT:
                    if (hasAlt) throw new InvalidShortcutException("Alt key already added.");
                    hasAlt = true;
                    break;
                case Input.Keys.CONTROL_LEFT:
                case Input.Keys.CONTROL_RIGHT:
                    if (hasControl) throw new InvalidShortcutException("Control key already added.");
                    hasControl = true;
                    break;
                case Input.Keys.SHIFT_LEFT:
                case Input.Keys.SHIFT_RIGHT:
                    if (hasShift) throw new InvalidShortcutException("Shift key already added.");
                    hasShift = true;
                    break;
                default:
                    if (hasNormalKey)
                        throw new InvalidShortcutException("Keybind must have a maximum of 1 non modifier key");

                    hasNormalKey = true;
                    allModifiers = false;
            }
        }

        if (allModifiers) throw new InvalidShortcutException("All modifier keys are not allowed.");
    }

    private void setKey(int[] keys, int keycode) {
        switch (keycode) {
            case Input.Keys.CONTROL_LEFT:
            case Input.Keys.CONTROL_RIGHT:
                if (keys[0] == 0) {
                    keys[0] = Input.Keys.CONTROL_LEFT;
                    dirty = true;
                }
                break;
            case Input.Keys.SHIFT_LEFT:
            case Input.Keys.SHIFT_RIGHT:
                if (keys[1] == 0) {
                    keys[1] = Input.Keys.SHIFT_LEFT;
                    dirty = true;
                }
                break;
            case Input.Keys.ALT_LEFT:
            case Input.Keys.ALT_RIGHT:
                if (keys[2] == 0) {
                    keys[2] = Input.Keys.ALT_LEFT;
                    dirty = true;
                }
                break;
            default:
                if (keys[3] == 0) {
                    keys[3] = keycode;
                    dirty = true;
                }
                break;
        }
    }

    private void clearKey(int keycode) {
        switch (keycode) {
            case Input.Keys.CONTROL_LEFT:
            case Input.Keys.CONTROL_RIGHT:
                if (pressedKeys[0] > 0) {
                    pressedKeys[0] = 0;
                    dirty = true;
                }
                break;
            case Input.Keys.SHIFT_LEFT:
            case Input.Keys.SHIFT_RIGHT:
                if (pressedKeys[1] > 0) {
                    pressedKeys[1] = 0;
                    dirty = true;
                }
                break;
            case Input.Keys.ALT_LEFT:
            case Input.Keys.ALT_RIGHT:
                if (pressedKeys[2] > 0) {
                    pressedKeys[2] = 0;
                    dirty = true;
                }
                break;
            default:
                if (pressedKeys[3] > 0) {
                    pressedKeys[3] = 0;
                    dirty = true;
                }
                break;
        }
    }

    @Override
    public boolean keyDown(int keycode) {
        //Modifier keys have to be pressed before the normal key
        //ctrl -> shift -> o != o -> ctrl -> shift
        if (pressedKeys[3] != 0) return false;

        setKey(pressedKeys, keycode);
        pack();

        ConsoleCommand command = shortcuts.get(currentlyPressedKeysPacked);
        if (command != null) {
            command.execute();
            return true;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        clearKey(keycode);
        pack();
        return false;
    }

    private int packKeys(int[] keys) {
        int idx = 0;
        int packed = 0;
        for (int i = 0; i < keys.length; i++) {
            if (keys[i] == 0) continue;
            packed |= (keys[i] & 0xFF) << (idx++ << 3);
        }
        return packed;
    }

    private void pack() {
        if (!dirty) return;
        dirty = false;
        currentlyPressedKeysPacked = packKeys(pressedKeys);
    }

}
