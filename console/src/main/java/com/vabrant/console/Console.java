package com.vabrant.console;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.Method;
import com.vabrant.console.annotation.ConsoleMethod;
import com.vabrant.console.annotation.ConsoleObject;

import space.earlygrey.shapedrawer.ShapeDrawer;

public class Console {
	
	public static boolean CHECK_FIELDS = false;
	public static boolean CHECK_SUBCLASS_FIELDS = false;
	public static boolean FORCE_ACCESS = false;
	
	public static final String FONT_TEXTURE_PATH = "ConsolasFont.png";
	public static final String FONT_FNT_PATH = "ConsolasFont.fnt";
	
	private AssetManager manager;
	final String DEBUG_ATLAS = "DebugAtlas.atlas";
	final Rectangle bounds;
	private final TextBox textBox;
	private ShapeDrawer shapeDrawer;
	
	public HashMap<String, CommandObject> commandObjects;
	public HashMap<CommandObject, Array<CommandMethod>> commandMethods;
	
	public Console(Batch batch, TextureRegion pixel, AssetManager assetManager) {
		this(batch, pixel, assetManager.get(FONT_TEXTURE_PATH, Texture.class));
	}
	
	public Console(Batch batch, TextureRegion pixel, Texture fontTexture) {
		shapeDrawer = new ShapeDrawer(batch, pixel);
		ConsoleFont.init(fontTexture);
		bounds = new Rectangle(0, 0, 480, 300);
		textBox = new TextBox(this);
		commandObjects = new HashMap<>(20);
		commandMethods = new HashMap<>(20);
		Gdx.input.setInputProcessor(textBox);
	}
	
	private void addCommandObject(String name, Object commandObject) {
		if(commandObjects.containsValue(commandObject)) {
			System.out.println("Object is already added");
			return;
		}
		
		if(commandObject.getClass().isAnnotationPresent(ConsoleObject.class)) {
			ConsoleObject consoleObject = ClassReflection.getAnnotation(commandObject.getClass(), ConsoleObject.class).getAnnotation(ConsoleObject.class);
			name = consoleObject.name().isEmpty() ? commandObject.getClass().getSimpleName() : consoleObject.name();
		}
		else if(name == null) {
			System.out.println("Name is null");
			return;
		}
		else if(name.isEmpty()) {
			name = commandObject.getClass().getSimpleName();
		}
		
		System.out.println("Added Console Object: " + name);
		commandObjects.put(name, new CommandObject(commandObject, name));
	}
	
	private void addCommandMethods(CommandObject commandObject) {
		Method[] methods = ClassReflection.getMethods(commandObject.get().getClass());
		
		if(!commandMethods.containsKey(commandObject)) commandMethods.put(commandObject, new Array<CommandMethod>());
		
		Array<CommandMethod> commandObjectMethods = commandMethods.get(commandObject);
		
		for(Method m : methods) {
			if(Console.FORCE_ACCESS) m.setAccessible(true);
			
			if(m.isAnnotationPresent(ConsoleMethod.class)) {
				System.out.println("BOb");
				commandObjectMethods.add(new CommandMethod(m, Console.FORCE_ACCESS));
				System.out.println(m.getName() + " is a Console Method");
			}
		}
	}
	
	private void checkForFieldCommandObjects(Class c) {
		Field[] fields = ClassReflection.getFields(c);
		
	}
	
	private void printAllCommandObjects() {
		Set<Map.Entry<String, CommandObject>> entries = commandObjects.entrySet();
		System.out.println("All Console Objects:");
		for(Map.Entry<String, CommandObject> entry : entries) {
			System.out.println("\t" + entry.getKey() + " : " + entry.getValue().getClassName());
		}
	}
	
	private void printAllCommandMethods() {
		Set<Map.Entry<CommandObject, Array<CommandMethod>>> entries = commandMethods.entrySet();
		System.out.println("All Console Methods:");
		for(Map.Entry<CommandObject, Array<CommandMethod>> entry : entries) {
			System.out.println("\t(CommandObject) " + entry.getKey().getClassName() + ":");
			Array<CommandMethod> methods = entry.getValue();
			for(CommandMethod m : methods) {
				System.out.println("\t\t(CommandMethod) " + m.getName());
			}
		}
	}
	
	public void add(String name, Object commandObject) {
		if(commandObject == null) {
			System.out.println("Object is null");
			return;
		}
		
		addCommandObject(name, commandObject);
		addCommandMethods(commandObjects.get(name));
		
		if(Console.CHECK_FIELDS) {
			Field[] fields = ClassReflection.getFields(commandObject.getClass());
		
			for(Field f : fields) {
				try {                         
					if(f.isAnnotationPresent(ConsoleObject.class)) {
						Object fieldObject = f.get(commandObject);
						if(fieldObject == null) continue;
						ConsoleObject consoleObject = f.getDeclaredAnnotation(ConsoleObject.class).getAnnotation(ConsoleObject.class);
						addCommandObject(consoleObject.name(), fieldObject);
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		printAllCommandObjects();
		printAllCommandMethods();
	}
	
	public void draw(Batch batch) {
		textBox.draw(shapeDrawer, batch);
	}
	
	public void debug(ShapeRenderer renderer) {
		drawFrame(renderer);
		textBox.debug(renderer);
	}
	
	public void drawFrame(ShapeRenderer renderer) {
		renderer.set(ShapeType.Line);
		renderer.setColor(Color.GREEN);
		renderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
	}

}
