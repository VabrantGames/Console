package com.vabrant.console;

import java.util.Iterator;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.Method;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.vabrant.console.annotation.ConsoleMethod;
import com.vabrant.console.annotation.ConsoleObject;
import com.vabrant.console.shortcuts.ConsoleShortcuts;

public class Console extends Table{
	
	Skin skin;
	Stage stage;
	
	public final DebugLogger logger = DebugLogger.getLogger(Console.class, DebugLogger.DEBUG);
	
	public static final String FONT_TEXTURE_PATH = "consolas.png";
	public static final String FONT_FNT_PATH = "consolas.fnt";
	
	private final TextBox textBox;
	private final Batch batch;
	
	private ObjectMap<String, CommandObject> commandObjects;
	private ObjectMap<String, ObjectSet<CommandObject>> commandMethods;
	
	private final ConsoleSettings settings;

	public Console(ConsoleSettings settings, Batch batch) {
		this.settings = settings;
		
		//TODO REMOVE! FOR DEBUGGING ONLY!
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		
		if(batch == null) throw new IllegalArgumentException("Batch is null");
		
		this.batch = batch;
		stage = new Stage(new ScreenViewport(), batch);
		skin = new Skin(Gdx.files.internal("orangepeelui/uiskin.json"));
		setFillParent(true);
		setDebug(true);
		
		commandObjects = new ObjectMap<>(20);
		commandMethods = new ObjectMap<>(50);
		
		textBox = new TextBox(this);
		
		stage.addActor(this);
		stage.setKeyboardFocus(textBox);
		
		Table textBoxTable = new Table();
		textBoxTable.setDebug(true);
		textBoxTable.add(textBox).expandY().growX().bottom();
		
		this.add(textBoxTable).grow();
		
		Gdx.input.setInputProcessor(new InputMultiplexer(ConsoleShortcuts.instance, stage));
	}
	
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
	}
	
	private TextureRegion createPixelRegion() {
		Pixmap pix = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
		pix.setColor(Color.WHITE);
		pix.drawPixel(0, 0);
		
		TextureRegion region = new TextureRegion(new Texture(pix), 0, 0, 1, 1);
		pix.dispose();
		return region;
	}
	
	public CommandObject addCommandObject(String name, Object consoleObject) {
		if(consoleObject == null) throw new IllegalArgumentException("Could not add object (" + name + "). Object is null"); 

		if(consoleObject.getClass().isAnnotationPresent(ConsoleObject.class)) {
			ConsoleObject o = ClassReflection.getAnnotation(consoleObject.getClass(), ConsoleObject.class).getAnnotation(ConsoleObject.class);
			name = o.name().isEmpty() ? consoleObject.getClass().getSimpleName() : o.name();
		}
		
		if(name == null || name.isEmpty()) return null;
		
		Iterator<Entry<String, CommandObject>> iterator = commandObjects.iterator();
		Entry<String, CommandObject> entry = null;
		while(iterator.hasNext()) {
			entry = iterator.next();

			Object o = entry.value.getObject();

			//check if the same instance is already added
			if(consoleObject.equals(o)) {
				if(logger != null) logger.info("The same exact instance already exists", "(" + name + ") " + o.getClass().getName());
				return null;
			}//check if the current key has already being used
			else if(entry.key.equals(name)) {
				if(logger != null) logger.error("Console object with name " + name + " already exists");
				return null;
			}
		}
		
		if(logger != null) logger.debug("Added console object", name);
		
		CommandObject commandObject = new CommandObject(consoleObject, name);
		commandObjects.put(name, commandObject);
		
		return commandObject;
	}
	
	private void addMethods(Class c, CommandObject commandObject) {
		Method[] methods = ClassReflection.getDeclaredMethods(c);
		for(Method m : methods) {
			addMethod(commandObject, m);
//			if(m.isAnnotationPresent(ConsoleMethod.class)) {
//				if(m.isPrivate() && !settings.addPrivateMethods) continue;
//				m.setAccessible(true);
//				CommandMethod commandMethod = new CommandMethod(m);
//				commandObject.addMethod(commandMethod);
//				if(logger != null) logger.debug("Added console method", commandObject.getName() + " - " +  commandMethod.toString());
//			}
		}
	}
	
	private void addMethod(CommandObject commandObject, Method method) {
		if(method.isAnnotationPresent(ConsoleMethod.class)) {
			CommandMethod commandMethod = new CommandMethod(method);
			commandObject.addMethod(commandMethod);
			
			//---------// Add methods to the commandMethods map which puts all objects that have a method with the same name together //----------//
			ObjectSet<CommandObject> objectsWithMethodName = commandMethods.get(commandMethod.getName());
			
			if(objectsWithMethodName == null) {
				objectsWithMethodName = new ObjectSet<>(2);
				commandMethods.put(commandMethod.getName(), objectsWithMethodName);
			}
			
			objectsWithMethodName.add(commandObject);
			
			if(logger != null) logger.debug("Added console method", commandObject.getName() + " " +  commandMethod.toString());
		}
	}
	
	private void addCommandMethods(CommandObject commandObject) {
		if(settings.searchSubclasses) {
			Class c = commandObject.getObject().getClass();
			while(c != null) {
				addMethods(c, commandObject);
				c = c.getSuperclass();
			}
		}
		else {
			addMethods(commandObject.getObject().getClass(), commandObject);
		}
		
//		//add the methods to the all methods array
//		Iterator<Entry<String, Array<CommandMethod>>> iterator = commandObject.methods.iterator();
//		Entry<String, Array<CommandMethod>> entry = null;
//		while(iterator.hasNext()) {
//			entry = iterator.next();
//
//			ObjectSet<CommandObject> objectsWithName = commandMethods.get(entry.key);
//			
//			if(objectsWithName == null) {
//				objectsWithName = new ObjectSet<>(2);
//				commandMethods.put(entry.key, objectsWithName);
//			}
//			
//			objectsWithName.add(commandObject);
//		}
	}

	public CommandObject getCommandObject(String name) {
		return commandObjects.get(name);
	}
	
	public ObjectSet<CommandObject> getCommandObjectsThatHaveMethod(String name){
		return commandMethods.get(name);
	}
	
	public boolean hasMethod(String name) {
		return commandMethods.containsKey(name);
	}
	
	public boolean hasObject(String name) {
		return commandObjects.containsKey(name);
	}

	public void add(Object consoleObject) {
		add(null, consoleObject);
	}
	
	public void add(String name, Object consoleObject) {
		CommandObject commandObject = addCommandObject(name, consoleObject);
		if(commandObject != null) {
			addCommandMethods(commandObject);
		}
		
		if(settings.checkFields) {
			Field[] fields = ClassReflection.getFields(consoleObject.getClass());
		
			for(Field f : fields) {
				try {                         
					if(f.isAnnotationPresent(ConsoleObject.class)) {
						Object fieldObject = f.get(consoleObject);
						if(fieldObject == null) continue;
						ConsoleObject o = f.getDeclaredAnnotation(ConsoleObject.class).getAnnotation(ConsoleObject.class);
						addCommandObject(o.name(), fieldObject);
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void update(float delta) {
		stage.act(delta);
	}
	
	public void draw() {
		stage.draw();
	}
	
	public void printObjects() {
		Iterator<Entry<String, CommandObject>> iterator = commandObjects.iterator();
		Entry<String, CommandObject> entry = null;
		System.out.println("CommandObjects:");
		while(iterator.hasNext()) {
			entry = iterator.next();
			System.out.println("\t" + entry.value.getName());
		}
	}
	
	public void printMethods() {
		Iterator<Entry<String, CommandObject>> iterator = commandObjects.iterator();
		Entry<String, CommandObject> entry = null;
		while(iterator.hasNext()) {
			entry = iterator.next();
			CommandObject o = entry.value;
			o.printMethods();
		}
	}

}
