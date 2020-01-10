package com.vabrant.console;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.Method;
import com.vabrant.console.annotation.ConsoleMethod;
import com.vabrant.console.annotation.ConsoleObject;

import space.earlygrey.shapedrawer.ShapeDrawer;

public class Console {
	
	public static float WIDTH = 480;
	public static float HEIGHT = 320;
	
	public final DebugLogger logger = DebugLogger.getLogger(Console.class, DebugLogger.DEBUG);
	
	public static boolean CHECK_FIELDS = false;
	public static boolean CHECK_SUBCLASS_FIELDS = false;
	public static boolean FORCE_ACCESS = false;
	public static boolean CASE_SENSITIVE = false;
	
	public static final String FONT_TEXTURE_PATH = "consolas.png";
	public static final String FONT_FNT_PATH = "consolas.fnt";
	
	private AssetManager manager;
	final Rectangle bounds;
	private final TextBox textBox;
	private ShapeDrawer shapeDrawer;
	
	private HashMap<String, CommandObject> commandObjects;
	private ArrayList<CommandMethod> allCommandMethods; 
	
	public Console(Batch batch, TextureRegion pixel) {
		//TODO REMOVE! FOR DEBUGGING ONLY!
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		
		shapeDrawer = new ShapeDrawer(batch, pixel);
		bounds = new Rectangle(0, 0, WIDTH, HEIGHT);
		textBox = new TextBox(this);
		commandObjects = new HashMap<>(20);
		allCommandMethods = new ArrayList<>();
		Gdx.input.setInputProcessor(textBox);
	}
	
	private void addCommandObject(String name, Object consoleObject) {
		if(consoleObject == null) throw new IllegalArgumentException("Could not add object (" + name + "). Object is null."); 

		//check if the same instance is already added
		Iterator<Map.Entry<String, CommandObject>> iterator = commandObjects.entrySet().iterator();
		Map.Entry<String, CommandObject> entry = null;
		while(iterator.hasNext()) {
			entry = iterator.next();

			Object o = entry.getValue().getObject();

			//TODO is an object is null it should be marked as dead to be cleaned up later
			if(o == null) return;
			if(consoleObject.equals(o)) {
				if(logger != null) logger.info("The same exact instance already exists", "(" + name + ") " + o.getClass().getName());
				return;
			}
		}

		if(consoleObject.getClass().isAnnotationPresent(ConsoleObject.class)) {
			ConsoleObject o = ClassReflection.getAnnotation(consoleObject.getClass(), ConsoleObject.class).getAnnotation(ConsoleObject.class);
			name = o.name().isEmpty() ? consoleObject.getClass().getSimpleName() : o.name();
			System.out.println(name);
		}
		
		if(name == null || name.isEmpty()) {
			name = consoleObject.getClass().getSimpleName();
		}
		
		if(logger != null) logger.debug("Added console object:", name);
		commandObjects.put(name, new CommandObject(consoleObject, name));
	}
	
	private void addCommandMethods(CommandObject commandObject) {
		Method[] methods = ClassReflection.getMethods(commandObject.getObject().getClass());
		
		for(Method m : methods) {
			if(Console.FORCE_ACCESS) m.setAccessible(true);
			
			if(m.isAnnotationPresent(ConsoleMethod.class)) {
				CommandMethod commandMethod = new CommandMethod(m, Console.FORCE_ACCESS);
				commandObject.addMethod(commandMethod);
				allCommandMethods.add(commandMethod);
				if(logger != null) logger.debug("Added console method", commandObject.getName() + " - " +  commandMethod.toString());
			}
		}
	}
	
	private void checkForFieldCommandObjects(Class c) {
		Field[] fields = ClassReflection.getFields(c);
		
	}
	
	public CommandObject getCommandObject(String key) {
		return commandObjects.get(key);
	}

	/**
	 * Manually adds an object to the console. 
	 * @param name
	 * @param consoleObject
	 */
	public void add(String name, Object consoleObject) {
		addCommandObject(name, consoleObject);
		addCommandMethods(commandObjects.get(name));
		
		if(Console.CHECK_FIELDS) {
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
		textBox.update(delta);
	}
	
	public void draw(Batch batch) {
		textBox.draw(batch, shapeDrawer);
//		font.draw(batch, layout, 0, 0 + font.getCapHeight() + Math.abs(font.getDescent()));
	}
	
	public void printAllCommandObjects() {
		Iterator<Map.Entry<String, CommandObject>> iterator = commandObjects.entrySet().iterator();
		Map.Entry<String, CommandObject> entry = null;
		System.out.println("CommandObjects:");
		while(iterator.hasNext()) {
			entry = iterator.next();
			System.out.println("\t" + entry.getValue().getName());
		}
	}
	
	public void printAllCommandMethods() {
		Iterator<Map.Entry<String, CommandObject>> iterator = commandObjects.entrySet().iterator();
		Map.Entry<String, CommandObject> entry = null;
		while(iterator.hasNext()) {
			entry = iterator.next();
			CommandObject o = entry.getValue();
			o.printMethods();
		}
	}
	
	public void debug() {
//		drawFrame(renderer);
		textBox.debug(shapeDrawer);
	}
	
	public void drawFrame(ShapeRenderer renderer) {
		renderer.set(ShapeType.Line);
		renderer.setColor(Color.GREEN);
		renderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
	}

}
