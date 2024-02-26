
package commandextension;

public class StaticReference extends ClassReference<Class<?>> {

	public StaticReference (String name, Class<?> c) {
		super(name, c);
	}

	@Override
	public Class<?> getReference () {
		return getReferenceClass();
	}

}
