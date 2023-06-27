package com.vabrant.console.test.guitests;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.vabrant.console.commandstrategy.gui.CommandLinePanel;
import com.vabrant.console.gui.*;

public class NewGUITests extends ApplicationAdapter {

    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setWindowedMode(960, 640);
		config.setTitle("GUIConsoleTest");
		new Lwjgl3Application(new NewGUITests(), config);
    }

    private Stage stage;
    private Array<View> views;

    @Override
    public void create() {
        VisUI.load();
        stage = new Stage(new ScreenViewport());
//        stage.setDebugAll(true);

        Panel commandLinePanel = new CommandLinePanel();

//        MultiPanelView v = new NormalView();
//        View v = new NormalView();
//        v.setActivePanel(commandLinePanel);
//        View v = new WindowView("TestWindow");
//        v.addPanel(new ButtonPanel());
//        v.setActivePanel("Button");
//        v.addPanel(new CommandLinePanel());
//        CommandLineView v = new CommandLineView((CommandLinePanel)commandLinePanel);
//        WindowView v = new WindowView("");
        View v = new TableView("", new CommandLinePanel());
        v.getRootTable().pack();
//        v.setSize(10, 40);
        v.setWidthPercent(80);
//        v.setHeightPercent(8);
        v.setPosition(0, Gdx.graphics.getHeight() - v.getRootTable().getHeight());
//        v.setStage(stage);
        stage.addActor(v.getRootTable());

        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
//                finalV.setHidden(!finalV.isHidden());
                return super.keyDown(event, keycode);
            }
        });

        v = new WindowView("Hello", new CommandLinePanel());
//        v.getRootTable().setTouchable(Touchable.childrenOnly);
//        v.getRootTable().setTouchable(Touchable.disabled);
//        ((Window)v.getViewTable()).setModal(true);
        v.setSizePercent(50, 50);
        stage.addActor(v.getRootTable());

        v = new WindowView("", new CommandLinePanel());
        v.setSizePercent(50, 50);
//        stage.addActor(v.getRootTable());

        Gdx.input.setInputProcessor(stage);
    }

    public void addView() {

    }

    private class ButtonPanel extends Panel {

        ButtonPanel() {
            super("Button", "Button");

            contentTable.add(new VisTextButton("Hello")).expand().row();
            contentTable.add(new VisTextButton("Hello")).expand().row();
        }

        @Override
        public void create(Skin skin) {

        }
    }


    @Override
    public void render() {
        ScreenUtils.clear(Color.WHITE);
        stage.act();
        stage.draw();
    }

}
