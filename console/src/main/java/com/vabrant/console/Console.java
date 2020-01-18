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
	
	public static final String FONT_TEXTURE_PATH = "consolas.png";
	public static final String FONT_FNT_PATH = "consolas.fnt";
	
	final Rectangle bounds;
	private final TextBox textBox;
	private final ShapeDrawer shapeDrawer;
	private final Batch batch;
	
	private ObjectMap<String, CommandObject> commandObjects;
	private ObjectMap<String, ObjectSet<CommandObject>> commandMethods;
	
	private final ConsoleSettings settings;
	
	public Console(Batch batch) {
		this(new ConsoleSettings(),null, batch);
	}
	
	public Console(ConsoleSettings settings, Batch batch) {
		this(settings, null, batch);
	}
	
	public Console(ConsoleSettings settings, TextureRegion pixelRegion, Batch batch) {
		this.settings = settings;
		
		//TODO REMOVE! FOR DEBUGGING ONLY!
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		
		if(batch == null) throw new IllegalArgumentException("Batch is null");
		
		this.batch = batch;
		
		shapeDrawer = new ShapeDrawer(batch, pixelRegion == null ? createPixelRegion() :pixelRegion);
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
			
			if(m.isAnnotationPresent(ConsoleMethod.class)) {
				if(m.isPrivate() && !settings.addPrivateMethods) continue;
				m.setAccessible(true);
				CommandMethod commandMethod = new CommandMethod(m);
				commandObject.addMethod(commandMethod);
				if(logger != null) logger.debug("Added console method", commandObject.getName() + " - " +  commandMethod.toString());
			}
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
		
		//add the methods to the all methods array
		Iterator<Entry<String, Array<CommandMethod>>> iterator = commandObject.methods.iterator();
		Entry<String, Array<CommandMethod>> entry = null;
		while(iterator.hasNext()) {
			entry = iterator.next();

			ObjectSet<CommandObject> objectsWithName = commandMethods.get(entry.key);
			
			if(objectsWithName == null) {
				objectsWithName = new ObjectSet<>(2);
				commandMethods.put(entry.key, objectsWithName);
			}
			
			objectsWithName.add(commandObject);
		}
	}

	CommandObject getCommandObject(String name) {
		return commandObjects.get(name);
	}
	
	ObjectSet<CommandObject> getCommandObjectsThatHaveMethod(String name){
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
		textBox.update(delta);
	}
	
	public void draw() {
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

}
