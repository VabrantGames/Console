
package com.vabrant.console.gui.views;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.StringBuilder;
import com.vabrant.console.Utils;
import com.vabrant.console.gui.LogViewConfiguration;
import com.vabrant.console.gui.shortcuts.DefaultKeyMap;
import com.vabrant.console.log.Log;
import com.vabrant.console.log.LogLevel;
import com.vabrant.console.log.LogManager;
import com.vabrant.console.log.LogManager.LogManagerChangeListener;
import space.earlygrey.shapedrawer.ShapeDrawer;
import space.earlygrey.shapedrawer.scene2d.ShapeDrawerDrawable;

public class LogView<T extends Table> extends DefaultView<T, DefaultKeyMap> implements LogManagerChangeListener {

	public static LogView<Window> createWindowView (String name, LogViewConfiguration config) {
		return new LogView<>(name, new Window(name, config.skin), config);
	}

	public static LogView<Window> createWindowView (String name, LogManager logManager, Skin skin, ShapeDrawer shapeDrawer) {
		return new LogView<>(name, new Window(name, skin), logManager, skin, shapeDrawer);
	}

	public static LogView<Table> createTableView (String name, LogViewConfiguration config) {
		return new LogView<>(name, new Table(), config);
	}

	public static LogView<Table> createTableView (String name, LogManager logManager, Skin skin, ShapeDrawer shapeDrawer) {
		LogViewConfiguration config = new LogViewConfiguration(logManager, skin, shapeDrawer);
		config.createTitleBar(true);
		return createTableView(name, config);
	}

	private boolean displayLevelTag = true;
	private boolean displayLevelTextColoring = true;
	private Table logTable;
	private ScrollPane scrollPane;
	private LogManager logManager;
	private Skin skin;
	private Drawable backgroundDrawable;
	private LabelStyle logStyle;
	private StringBuilder stringBuilder;

	public LogView (String name, T rootTable, LogViewConfiguration config) {
		super(name, rootTable, config);

		displayLevelTag = config.displayLevelTag;
		displayLevelTextColoring = config.displayLevelTextColoring;

		init(config.logManager, config.skin, config.shapeDrawer);
	}

	public LogView (String name, T table, LogManager logManager, Skin skin, ShapeDrawer shapeDrawer) {
		this(name, table, new LogViewConfiguration(logManager, skin, shapeDrawer));
	}

	private void init (LogManager logManager, Skin skin, ShapeDrawer shapeDrawer) {
		if (logManager == null) {
			throw new IllegalArgumentException("LogManager can't be null");
		}

		this.logManager = logManager;
		logManager.addChangeListener(this);

		this.skin = skin;

		stringBuilder = new StringBuilder();
		logTable = new Table();
		logTable.padBottom(5);
		scrollPane = new ScrollPane(logTable, skin);
		scrollPane.setFadeScrollBars(false);
		scrollPane.setFlickScroll(false);
		scrollPane.setOverscroll(false, false);
		scrollPane.setSmoothScrolling(true);
		scrollPane.getStyle().background = null;

		rootTable.add(scrollPane).grow();
		rootTable.debugTable();

		skin.get(LabelStyle.class).font.getData().markupEnabled = true;

		backgroundDrawable = new ShapeDrawerDrawable(shapeDrawer) {

			@Override
			public void drawShapes (ShapeDrawer shapeDrawer, float x, float y, float width, float height) {
				shapeDrawer.filledRectangle(x, y, width, height, Color.WHITE);
				shapeDrawer.line(x + 10, y + height, x + width - 10, y + height, Color.LIGHT_GRAY);
			}
		};

		logTable.setBackground(Utils.createFilledRectangleDrawable(shapeDrawer, Color.LIGHT_GRAY));

		logStyle = new LabelStyle(skin.get(LabelStyle.class));
		logStyle.background = backgroundDrawable;

		refresh();

		if (rootTable.getWidth() == 0) {
			setWidthPercent(40);
		}

		if (rootTable.getHeight() == 0) {
			setHeightPercent(20);
		}
	}

	public void displayLevelTag (boolean display) {
		displayLevelTag = display;
		refresh();
	}

	public void displayLevelTextColoring (boolean display) {
		displayLevelTextColoring = display;
		refresh();
	}

	@Override
	public void onChange () {
		refresh();
	}

	public ScrollPane getScrollPane () {
		return scrollPane;
	}

	public void refresh () {
		logTable.clear();
		logTable.add().grow().row();

		Array<Log> logs = logManager.getAllEntries();
		for (Log l : logs) {
			stringBuilder.clear();

			switch (l.getLogLevel()) {
			case INFO:

				if (displayLevelTextColoring) {
					stringBuilder.append("[#");
					stringBuilder.append(LogLevel.INFO.getColorHexString());
					stringBuilder.append(']');
				}

				if (displayLevelTag) stringBuilder.append(" [INFO]");
				break;
			case DEBUG:
				if (displayLevelTextColoring) {
					stringBuilder.append("[#");
					stringBuilder.append(LogLevel.DEBUG.getColorHexString());
					stringBuilder.append(']');
				}

				if (displayLevelTag) stringBuilder.append(" [DEBUG]");
				break;
			case ERROR:
				if (displayLevelTextColoring) {
					stringBuilder.append("[#");
					stringBuilder.append(LogLevel.ERROR.getColorHexString());
					stringBuilder.append(']');
				}

				if (displayLevelTag) stringBuilder.append(" [ERROR]");
				break;
			case NORMAL:
				if (displayLevelTextColoring) {
					stringBuilder.append("[#");
					stringBuilder.append(LogLevel.NORMAL.getColorHexString());
					stringBuilder.append(']');
				}
				break;
			}

			l.appendSimpleString(stringBuilder);

			logTable.add(new Label(stringBuilder.toString(), logStyle)).growX().top().left().padLeft(5).padRight(5).row();
		}

		scrollPane.validate();
		scrollPane.setScrollPercentY(1);
	}

}
