package com.vabrant.console.commandstrategy.gui;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.kotcrab.vis.ui.VisUI;
import com.vabrant.console.gui.Panel;

public class CommandLinePanel extends Panel {

    private boolean skipCharacter;
    private int myCursor;
    private TextField textField;
    private StringBuilder builder;

    public CommandLinePanel() {
        this(new TextField("Hello", VisUI.getSkin()));
    }

    public CommandLinePanel(TextField textField) {
        super("CommandLine", "CommandLine");
        this.textField = textField;
        builder = new StringBuilder(200);

//        textField.clearListeners();
//        textField.setFocusTraversal(false);

        contentTable.add(textField).expand().top().fillX();
    }

    public void clearCommandLine() {
        myCursor = 0;
        textField.setText("");
        textField.setCursorPosition(0);
    }

    void moveCursor(int amt) {
        myCursor = MathUtils.clamp(myCursor + amt, 0, builder.length());
		textField.setCursorPosition(myCursor);
    }

    public TextField getTextField() {
        return textField;
    }

    @Override
    public void create(Skin skin) {

    }

}
