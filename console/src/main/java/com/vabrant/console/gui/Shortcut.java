package com.vabrant.console.gui;

import com.vabrant.console.ConsoleCommand;

public class Shortcut {

    private int[] keybind;
    private String scope;
    private ConsoleCommand command;
    private String description;

    public Shortcut() {
        keybind = new int[4];
    }

    public void setKeybind(int[] keybind) {
       if (keybind.length > 4) {
//           throw new RuntimeException("Keybind")
       }
       this.keybind = keybind;
    }

    public int[] getKeybind() {
        return keybind;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getScope() {
        return scope;
    }

    public void setConsoleCommand(ConsoleCommand command) {
        this.command = command;
    }

    public ConsoleCommand getConsoleCommand() {
        return command;
    }
}
