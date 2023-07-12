
package com.vabrant.console.gui;

import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.kotcrab.vis.ui.VisUI;

public class MultiPanelWindowView extends MultiPanelView<Window> {

	public MultiPanelWindowView (String name) {
		this(name, new Window(name, VisUI.getSkin()));
	}

	public MultiPanelWindowView (String name, Window window) {
		super(name, window);
		setSizePercent(50, 50);
	}
}
