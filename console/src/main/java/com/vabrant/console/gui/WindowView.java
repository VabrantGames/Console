
package com.vabrant.console.gui;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;

public class WindowView extends DefaultView<Window> {

	public WindowView (String name, Skin skin, Panel<?, ?>... panel) {
		this(name, new Window(name, skin), skin, panel);
	}

	public WindowView (String name, Window window, Skin skin, Panel<?, ?>... panel) {
		super(name, window, skin, panel);
	}

	public WindowView (String name, Window window, Skin skin, TableSetup rootTableSetup, int maxPanels, Panel<?, ?> panels) {
		super(name, window, skin, rootTableSetup, maxPanels, panels);
	}

}
