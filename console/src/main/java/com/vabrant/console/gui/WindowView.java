package com.vabrant.console.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisWindow;

public class WindowView extends View<Window> {

    public WindowView(String name, Panel panel) {
        this(name, new Window(name, VisUI.getSkin()), panel);
    }

    public WindowView(String name, Window window, Panel panel) {
        super(name, window, panel);
        setSizePercent(50, 50);
    }

    public void centerX() {
        Window w = getRootTable();
        float x = (Gdx.graphics.getWidth() - w.getWidth()) * 0.5f;
        w.setX(x);
    }

}
