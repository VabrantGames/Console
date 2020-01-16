package com.vabrant.console;

import java.util.Iterator;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;
import com.badlogic.gdx.utils.ObjectSet;
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
	
	final Rectangle bounds;
	private final TextBox textBox;
	private ShapeDrawer shapeDrawer;
	
	private ObjectMap<String, CommandObject> commandObjects;
	private ObjectMap<String, ObjectSet<CommandObject>> commandMethods;
	
	private final ConsoleSettings settings;
	
	public Console(ConsoleSettings settings) {
		this.settings = settings;
		
		//TODO REMOVE! FOR DEBUGGING ONLY!
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		
		if(settings.batch == null) throw new IllegalArgumentException("Batch is null.");
		
		TextureRegion pixelRegion = settings.pixelRegion == null ? createPixelRegion() : settings.pixelRegion;
		
		shapeDrawer = new ShapeDrawer(settings.batch, pixelRegion);
		bounds = new Rectangle(0, 0, WIDTH, HEIGHT);
		textBox = new TextBox(this);
		commandObjects = new ObjectMap<>(20);
		commandMethods = new ObjectMap<>(50);
		
		Gdx.input.setInputProcessor(textBox);
	}
	
	private TextureRegion createPixelRegion() {
		Pixmap pix = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
		pix.setColor(Color.WHITE);
		pix.drawPixel(0, 0);
		
		TextureRegion region = new TextureRegion(new Texture(pix), 0, 0, 1, 1);
		pix.dispose();
		return region;
	}
	
	private CommandObject addCommandObject(String name, Object consoleObject) {
		if(consoleObject == null) throw new IllegalArgumentException("Could not add object (" + name + "). Object is null"); 

		if(consoleObject.getClass().isAnnotationPresent(ConsoleObject.class)) {
			ConsoleObject o = ClassReflection.getAnnotation(consoleObject.getClass(), ConsoleObject.class).getAnnotation(ConsoleObject.class);
			name = o.name().isEmpty() ? consoleObject.getClass().getSimpleName() : o.name();
		}
		
		if(name == null || name.isEmpty()) return null;
		
		//check if the same instance is already added
		Iterator<Entry<String, CommandObject>> iterator = commandObjects.iterator();
		Entry<String, CommandObject> entry = null;
		while(iterator.hasNext()) {
			entry = iterator.next();

			Object o = entry.value.getObject();

			if(consoleObject.equals(o)) {
				if(logger != null) logger.info("The same exact instance already exists", "(" + name + ") " + o.getClass().getName());
				return null;
			}
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
	
	private void addCommandMethods(CommandObject commandObject) {
		Method[] methods = ClassReflection.getMethods(commandObject.getObject().getClass());
		
		for(Method m : methods) {
			if(Console.FORCE_ACCESS) m.setAccessible(true);
			
			if(m.isAnnotationPresent(ConsoleMethod.class)) {
				CommandMethod commandMethod = new CommandMethod(m, Console.FORCE_ACCESS);
				commandObject.addMethod(commandMethod);
				if(logger != null) logger.debug("Added console method", commandObject.getName() + " - " +  commandMethod.toString());
			}
		}
		
		Iterator<Entry<String, Array<CommandMethod>>> iterator = commandObject.methods.iterator();
		Entry<String, Array<CommandMethod>> entry = null;
		while(iterator.hasNext()) {
			entry = iterator.next();

			ObjectSet<CommandObject> arrayOfEntriesWithName = commandMethods.get(entry.key);
			
			if(arrayOfEntriesWithName == null) {
				arrayOfEntriesWithName = new ObjectSet<>(2);
				commandMethods.put(entry.key, arrayOfEntriesWithName);
			}
			
			arrayOfEntriesWithName.add(commandObject);
		}
	}

	public CommandObject getCommandObject(String name) {
		return commandObjects.get(name);
	}
	
	ObjectSet<CommandObject> getMethodNameArray(String name){
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
	
	public void draw() {
		Batch batch = settings.batch;
		textBox.draw(batch, shapeDrawer);
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

	public void debug() {
//		drawFrame(renderer);
		textBox.debug(shapeDrawer);
	}
	
	public void drawFrame(ShapeRenderer renderer) {
		renderer.set(ShapeType.Line);
		renderer.setColor(Color.GREEN);
		renderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
	}
	
	static class ConsoleEntry<K, V> {
		public K key;
		public V value;
		
		public ConsoleEntry(K object, V method) {
			this.key = object;
			this.value = method;
		}
	}

}
