
package com.vabrant.console.gui;

import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.kotcrab.vis.ui.VisUI;

public class WindowView extends View<Window> {

	public WindowView (String name, Panel panel) {
		this(name, new Window(name, VisUI.getSkin()), panel);
	}

	public WindowView (String name, Window window, Panel panel) {
		super(name, window, panel);
		setSizePercent(50, 50);
	}

}
