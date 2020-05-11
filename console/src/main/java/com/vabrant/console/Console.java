package com.vabrant.console;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.vabrant.console.shortcuts.ConsoleShortcuts;

public class Console extends Table{
	
	private static ConsoleSettings GLOBAL_SETTINGS = null;
	
	public static ConsoleSettings getGlobalSettings() {
		if(GLOBAL_SETTINGS == null) GLOBAL_SETTINGS = new ConsoleSettings();
		return GLOBAL_SETTINGS;
	}
	
	Skin skin;
	Stage stage;
	
	public final DebugLogger logger = new DebugLogger(Console.class, DebugLogger.DEBUG);
	private final TextBox textBox;
	private final Batch batch;
	
//	private final ConsoleSettings settings;

	public Console(Batch batch) {
		//TODO REMOVE! FOR DEBUGGING ONLY!
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		
		if(batch == null) throw new IllegalArgumentException("Batch is null");
		
		this.batch = batch;
		stage = new Stage(new ScreenViewport(), batch);
		skin = new Skin(Gdx.files.internal("orangepeelui/uiskin.json"));
//		skin = new Skin(Gdx.files.internal("rustyrobotui/rusty-robot-ui.json"));
//		skin = new Skin(Gdx.files.internal("quantumhorizonui/quantum-horizon-ui.json"));
		setFillParent(true);
		setDebug(true);
		
		textBox = new TextBox(this);
		
		stage.addActor(this);
		stage.setKeyboardFocus(textBox);
		
//		Table textBoxTable = new Table();
//		textBoxTable.setDebug(true);
//		textBoxTable.add(textBox).
//		textBoxTable.add(textBox).prefHeight(30).minHeight(30).expandY().growX().bottom();

		this.setDebug(true);
		textBox.setSize(this.add(textBox));
//		this.add(textBoxTable).prefHeight(30).grow();
//		this.add(textBox).height(30).expandY().growX().bottom();
		
		Gdx.input.setInputProcessor(new InputMultiplexer(ConsoleShortcuts.instance, stage));
	}
	
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
	}
	
//	private void addMethods(Class c, CommandObject commandObject) {
//		Method[] methods = ClassReflection.getDeclaredMethods(c);
//		for(Method m : methods) {
//			addMethod(commandObject, m);
////			if(m.isAnnotationPresent(ConsoleMethod.class)) {
////				if(m.isPrivate() && !settings.addPrivateMethods) continue;
////				m.setAccessible(true);
////				CommandMethod commandMethod = new CommandMethod(m);
////				commandObject.addMethod(commandMethod);
////				if(logger != null) logger.debug("Added console method", commandObject.getName() + " - " +  commandMethod.toString());
////			}
//		}
//	}
//	
//	private void addMethod(CommandObject commandObject, Method method) {
//		if(method.isAnnotationPresent(ConsoleMethod.class)) {
//			CommandMethod commandMethod = new CommandMethod(method);
//			commandObject.addMethod(commandMethod);
//			
//			//---------// Add methods to the commandMethods map which puts all objects that have a method with the same name together //----------//
//			ObjectSet<CommandObject> objectsWithMethodName = commandMethods.get(commandMethod.getName());
//			
//			if(objectsWithMethodName == null) {
//				objectsWithMethodName = new ObjectSet<>(2);
//				commandMethods.put(commandMethod.getName(), objectsWithMethodName);
//			}
//			
//			objectsWithMethodName.add(commandObject);
//			
//			if(logger != null) logger.debug("Added console method", commandObject.getName() + " " +  commandMethod.toString());
//		}
//	}
	
//	private void addCommandMethods(CommandObject commandObject) {
//		if(settings.searchSubclasses) {
//			Class c = commandObject.getObject().getClass();
//			while(c != null) {
//				addMethods(c, commandObject);
//				c = c.getSuperclass();
//			}
//		}
//		else {
//			addMethods(commandObject.getObject().getClass(), commandObject);
//		}
//		
////		//add the methods to the all methods array
////		Iterator<Entry<String, Array<CommandMethod>>> iterator = commandObject.methods.iterator();
////		Entry<String, Array<CommandMethod>> entry = null;
////		while(iterator.hasNext()) {
////			entry = iterator.next();
////
////			ObjectSet<CommandObject> objectsWithName = commandMethods.get(entry.key);
////			
////			if(objectsWithName == null) {
////				objectsWithName = new ObjectSet<>(2);
////				commandMethods.put(entry.key, objectsWithName);
////			}
////			
////			objectsWithName.add(commandObject);
////		}
//	}

//	public void add(String name, Object consoleObject) {
//		CommandObject commandObject = addCommandObject(name, consoleObject);
//		if(commandObject != null) {
//			addCommandMethods(commandObject);
//		}
//		
//		if(settings.checkFields) {
//			Field[] fields = ClassReflection.getFields(consoleObject.getClass());
//		
//			for(Field f : fields) {
//				try {                         
//					if(f.isAnnotationPresent(ConsoleObject.class)) {
//						Object fieldObject = f.get(consoleObject);
//						if(fieldObject == null) continue;
//						ConsoleObject o = f.getDeclaredAnnotation(ConsoleObject.class).getAnnotation(ConsoleObject.class);
//						addCommandObject(o.value(), fieldObject);
//					}
//				}
//				catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		}
//	}
	
	public void update(float delta) {
		stage.act(delta);
	}
	
	public void draw() {
		stage.draw();
	}
	
//	public void printObjects() {
//		Iterator<Entry<String, CommandObject>> iterator = commandObjects.iterator();
//		Entry<String, CommandObject> entry = null;
//		System.out.println("CommandObjects:");
//		while(iterator.hasNext()) {
//			entry = iterator.next();
//			System.out.println("\t" + entry.value.getName());
//		}
//	}
//	
//	public void printMethods() {
//		Iterator<Entry<String, CommandObject>> iterator = commandObjects.iterator();
//		Entry<String, CommandObject> entry = null;
//		while(iterator.hasNext()) {
//			entry = iterator.next();
//			CommandObject o = entry.value;
//			o.printMethods();
//		}
//	}

}
