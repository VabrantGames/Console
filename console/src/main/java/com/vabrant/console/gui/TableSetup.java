package com.vabrant.console.gui;

import com.badlogic.gdx.scenes.scene2d.ui.Table;

public interface TableSetup<T, U extends Table> {
	void setup (T object, U table);
}
