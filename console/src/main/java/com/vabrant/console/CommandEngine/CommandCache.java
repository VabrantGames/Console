
package com.vabrant.console.CommandEngine;

import com.badlogic.gdx.utils.ObjectSet;

public interface CommandCache {

	/** Checks if the cache contains an instance or static reference from the specified name.
	 *
	 * @param name
	 * @return */
	boolean hasReference (String name);

	/** Returns an instance or static reference from the specified name. Null is returned if no reference is found.
	 *
	 * @param name
	 * @return */
	ClassReference getReference (String name);

	/** Adds an object to the cache as a reference, that can be used as an argument or to hold commands. Passing in a <i>Class</i>
	 * object will make this reference a {@link StaticReference static reference}, that can only call static methods. Only one
	 * static reference per <i>Class</i> object can be created, while unlimited instance references can be created as long as the
	 * instance is unique.
	 *
	 * @param object Object used as a reference.
	 * @param referenceID ID to call the reference. */
	ClassReference addReference (String referenceID, Object object);

	void addCommand (String name, Command command);

	boolean hasCommand (String name);

	Command getCommand (String name);

	default void addCommand (ClassReference<?> classReference, String name, Class<?>... args) {
	}

	default boolean hasCommand (String referenceName, String methodName, Class<?>... args) {
		return false;
	}

	default MethodCommand getCommand (String referenceName, String methodName, Class<?>... args) {
		return null;
	}

	default ObjectSet<Command> getAllCommands (String name) {
		return null;
	}
// /** Checks if the cache contains a specific command.
// * @param commandName
// * @param args
// * @return */

// boolean hasCommand (String commandName, Class<?>... args);
// /** Checks if the reference contains a specific command
// * @param classReference
// * @param commandName
// * @param args
// * @return */

// boolean hasCommand (ClassReference<?> classReference, String commandName, Class<?>... args);

// Command getCommand (String commandName, Class<?>... args);

// Command getCommand (ClassReference<?> classReference, String commandName, Class<?>... args);

// ObjectSet<Command> getAllCommandsWithName (String name);

// ObjectSet<Command> getAllCommandsForReference (ClassReference<?> reference);

// void addCommand (ClassReference<?> classReference, String methodName, Class<?>... args);

	void addAll (ClassReference<?> classReference);

}
