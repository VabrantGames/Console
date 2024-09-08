
package com.vabrant.console.commandexecutor;

import com.badlogic.gdx.utils.ObjectSet;

public interface CommandCache {

	/** Checks if the cache contains an instance or static reference for the specified name.
	 *
	 * @param name
	 * @return */
	boolean hasReference (String name);

	/** Returns an instance or static reference for the specified name. Null is returned if no reference is found.
	 *
	 * @param name
	 * @return */
	ClassReference getReference (String name);

	/** Adds an object to the cache as a reference that can be used as an argument or to hold commands. Passing in a <i>Class</i>
	 * object will make this reference a {@link StaticReference static reference}, that can only call static methods. Only one
	 * static reference per <i>Class</i> object can be created, while unlimited instance references can be created as long as the
	 * instance is unique.
	 *
	 * @param object Object used as a reference.
	 * @param referenceID ID to call the reference. */
	ClassReference addReference (String referenceID, Object object);

	boolean hasCommand (String name);

	void addCommand (String name, Command command);

	Command getCommand (String name);

	boolean hasMethodCommand (String referenceName, String methodName, Class<?>... args);

	MethodCommand getMethodCommand (String referenceName, String methodName, Class<?>... args);

	void addMethodCommand (ClassReference<?> classReference, String methodName, Class<?>... args);

	ObjectSet<Command> getAllCommandsWithName (String name);

	ObjectSet<MethodCommand> getAllCommandsForReference (ClassReference reference);
}
