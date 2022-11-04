package com.vabrant.console;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Values;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.Method;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.vabrant.console.annotation.ConsoleMethod;
import com.vabrant.console.annotation.ConsoleObject;

import java.util.Iterator;

public class ConsoleCache {

    public static final ConsoleCache GLOBAL_CACHE = null;
    public static final String CLASS_REFERENCE_DESCRIPTION = "Reference:[%S] Name:[%s] Class:[%s] Full:[%s]";

    //References by name
    private final ObjectMap<String, ClassReference<?>> classReferences = new ObjectMap<>(20);

    //Methods for reference
    private final ObjectMap<ClassReference<?>, ObjectSet<MethodInfo>> methodsByReference = new ObjectMap<>();

    //Methods grouped by name
    private final ObjectMap<String, ObjectSet<MethodInfo>> methodsByName = new ObjectMap<>();

    private final MethodLookup methodLookup = new MethodLookup();
    private final DebugLogger logger = new DebugLogger(ConsoleCache.class, DebugLogger.DEBUG);

    public void setLogLevel(int level) {
        logger.setLevel(level);
    }

    /**
     * Returns an instance or static reference from the specified name. Null is returned if no reference is found.
     *
     * @param name
     * @return
     */
    public ClassReference getReference(String name) {
        return classReferences.get(name);
    }

    /**
     * Checks if the cache contains an instance or static reference from the specified name.
     *
     * @param name
     * @return
     */
    public boolean hasReference(String name) {
        return classReferences.containsKey(name);
    }

    public boolean hasInstanceReference(String name) {
        return getInstanceReference(name) != null;
    }

    public boolean hasInstanceReference(Object object) {
        return getInstanceReference(object) != null;
    }

    public InstanceReference getInstanceReference(String name) {
        if (name == null || name.isEmpty()) return null;
        ClassReference<?> ref = classReferences.get(name);
        if (!(ref instanceof InstanceReference)) return null;
        return (InstanceReference) ref;
    }

    public InstanceReference getInstanceReference(Object object) {
        if (object == null) return null;
        Values<ClassReference<?>> values = classReferences.values();
        for (ClassReference<?> r : values) {
            if (r instanceof InstanceReference) {
                InstanceReference instanceReference = (InstanceReference) r;
                if (instanceReference.getReference().equals(object)) return instanceReference;
            }
        }
        return null;
    }

    public boolean hasStaticReference(String name) {
        return getStaticReference(name) != null;
    }

    public boolean hasStaticReference(Class<?> clazz) {
        return getStaticReference(clazz) != null;
    }

    public StaticReference getStaticReference(String name) {
        if (name == null || name.isEmpty()) return null;
        ClassReference<?> ref = classReferences.get(name);
        if (!(ref instanceof StaticReference)) return null;
        return (StaticReference) ref;
    }

    public StaticReference getStaticReference(Class<?> clazz) {
        if (clazz == null) return null;
        Values<ClassReference<?>> values = classReferences.values();
        for (ClassReference<?> r : values) {
            if (r instanceof StaticReference) {
                StaticReference ref = (StaticReference) r;
                if (ref.getReferenceClass().equals(clazz)) return ref;
            }
        }
        return null;
    }

    /**
     * Check if a method exists.
     *
     * @param name
     * @return
     */
    public boolean hasMethodWithName(String name) {
        return methodsByName.get(name) != null;
    }

    public boolean hasMethodWithName(String referenceName, String methodName) {
        return hasMethodWithName(getReference(referenceName), methodName);
    }

    public boolean hasMethodWithName(ClassReference<?> classReference, String methodName) {
        ObjectSet<MethodInfo> methods = methodsByReference.get(classReference);

        if (methods == null) return false;

        for (MethodInfo m : methods) {
            if (m.getMethodReference().getName().equals(methodName)) return true;
        }
        return false;
    }

    public boolean hasMethod(String name, Class<?>... args) {
        ObjectSet<MethodInfo> methods = methodsByName.get(name);

        if (methods == null) return false;

        for (MethodInfo m : methods) {
            if (ConsoleUtils.areArgsEqual(m.getMethodReference().getArgs(), args)) return true;
        }
        return false;
    }

    /**
     * Check if a reference has a method added.
     *
     * @param referenceName
     * @param methodName
     * @param args
     * @return
     */
    public boolean hasMethod(String referenceName, String methodName, Class<?>... args) {
        return hasMethod(getReference(referenceName), methodName, args);
    }

    public boolean hasMethod(Object reference, String methodName, Class<?>... args) {
        return hasMethod(getInstanceReference(reference), methodName, args);
    }

    public boolean hasMethod(ClassReference<?> classReference, String methodName, Class<?>... args) {
        if (classReference == null || methodName == null) return false;

        ObjectSet<MethodInfo> methods = methodsByReference.get(classReference);
        if (methods == null) return false;

        for (MethodInfo info : methods) {
            MethodReference ref = info.getMethodReference();
            if (ref.getName().equals(methodName) && ConsoleUtils.areArgsEqual(ref.getArgs(), ConsoleUtils.defaultIfNull(args, ConsoleUtils.EMPTY_ARGUMENT_TYPES)))
                return true;
        }
        return false;
    }

    public ObjectSet<MethodInfo> getAllMethodsWithName(String name) {
        return methodsByName.get(name);
    }

    public ObjectSet<MethodInfo> getAllMethodsByReference(String referenceName) {
        ClassReference<?> reference = getReference(referenceName);
        if (reference == null) throw new RuntimeException("Reference " + referenceName + " not found.");

        ObjectSet<MethodInfo> methods = methodsByReference.get(reference);
        if (methods == null) throw new RuntimeException("Reference " + referenceName + " has 0 methods added.");
        return methods;
    }

    public void addReference(Object object) {
        addReference(object, null);
    }

    /**
     * Adds an object to the cache as a reference. This same object can have methods added to it later.
     *
     * @param object      Object used as a reference.
     * @param referenceID Name used to call the reference.
     */
    public void addReference(Object object, String referenceID) {
        if (object == null) throw new IllegalArgumentException("Object is null");

        InstanceReference reference = getInstanceReference(object);

        //Check if an instance reference is using the object as a reference
        if (reference != null) {
            logger.debug(
                    "[Conflict]" + " (Reference already exists)",
                    String.format(
                            CLASS_REFERENCE_DESCRIPTION,
                            "Instance",
                            reference.getName(),
                            reference.getReferenceSimpleName(),
                            reference.getReferenceClass().getCanonicalName()));
            return;
        }

        if (referenceID == null || referenceID.isEmpty()) {
            if (object.getClass().isAnnotationPresent(ConsoleObject.class)) {
                ConsoleObject o = ClassReflection.getAnnotation(object.getClass(), ConsoleObject.class).getAnnotation(ConsoleObject.class);
                referenceID = !o.value().isEmpty() ? o.value() : object.getClass().getSimpleName();
            } else {
                referenceID = object.getClass().getSimpleName();
            }
        }

        //Check if an class reference is using the given name
        if (classReferences.containsKey(referenceID)) {
            ClassReference ref = getReference(referenceID);

            logger.debug(
                    "[Conflict]" + " (Name is already in use)",
                    String.format(
                            "UsedBy " +
                                    CLASS_REFERENCE_DESCRIPTION,
                            "Object",
                            ref.getName(),
                            ref.getReferenceSimpleName(),
                            ref.getReferenceClass().getCanonicalName()));
            return;
        }

        reference = new InstanceReference(referenceID, object);

        logger.info(
                "[Added]",
                String.format(
                        CLASS_REFERENCE_DESCRIPTION,
                        "Instance",
                        reference.getName(),
                        reference.getReferenceSimpleName(),
                        reference.getReferenceClass().getCanonicalName()));

        classReferences.put(referenceID, reference);
    }

    public void addReference(Class clazz) {
        addReference(clazz, null);
    }

    /**
     * Adds a class to the cache as a reference. This reference's purpose is to be used to call static methods. Maximum references for class references is one.
     * References created with objects can call static methods if it or one its subclasses declares one. <br><br>
     * <p>
     * e.g. Adding the reference <br>
     * <i>  addReference({@link MathUtils MathUtils.class}, "MathU"); </i><br>
     * <i>  addMethod({@link MathUtils MathUtils.class}, "sin", float.class); </i><br><br>
     * This will allow you to reference the <i>MathUtils</i> class and call static methods that you have added to the reference <br><br>
     * <p>
     * e.g Calling the reference <br>
     * <i>reference.setRotation MathU.sin(20)
     *
     * @param clazz       Class used as a reference.
     * @param referenceID Name used to call the reference.
     */
    public void addReference(Class clazz, String referenceID) {
        if (clazz == null) throw new IllegalArgumentException("Class is null");

        ClassReference reference = getStaticReference(clazz);

        if (reference != null) {
            logger.debug(
                    "[Conflict] (Reference already exists)",
                    String.format(
                            CLASS_REFERENCE_DESCRIPTION,
                            "Static",
                            reference.getName(),
                            reference.getReferenceSimpleName(),
                            reference.getReferenceClass().getCanonicalName()));
            return;
        }

        if (referenceID == null || referenceID.isEmpty()) {
            if (clazz.isAnnotationPresent(ConsoleObject.class)) {
                ConsoleObject o = ClassReflection.getAnnotation(clazz, ConsoleObject.class).getAnnotation(ConsoleObject.class);
                referenceID = !o.value().isEmpty() ? o.value() : clazz.getSimpleName();
            } else {
                referenceID = clazz.getSimpleName();
            }
        }

        //Check if an class reference is using the given name
        if (classReferences.containsKey(referenceID)) {
            ClassReference ref = getReference(referenceID);

            logger.debug(
                    "[Conflict] (Name is already in use)",
                    String.format(
                            "UsedBy " +
                                    CLASS_REFERENCE_DESCRIPTION,
                            "Static",
                            ref.getName(),
                            ref.getReferenceSimpleName(),
                            ref.getReferenceClass().getCanonicalName()));
            return;
        }

        reference = new StaticReference(referenceID, clazz);

        logger.info(
                "[Added]",
                String.format(
                        CLASS_REFERENCE_DESCRIPTION,
                        "Static",
                        reference.getName(),
                        reference.getReferenceSimpleName(),
                        reference.getReferenceClass().getCanonicalName()));

        classReferences.put(referenceID, reference);
    }

    //Adds a method of an object
    public void addMethod(Object object, String methodName, Class... args) {
        if (object == null) throw new IllegalArgumentException("Object is null.");
        if (methodName == null || methodName.isEmpty()) throw new IllegalArgumentException("Invalid method name");

        //Check if the reference contains the same method
        InstanceReference instanceReference = getInstanceReference(object);
        if (instanceReference != null) {
            if (hasMethod(instanceReference, methodName, args)) return;
        }

        Method method = null;
        try {
            method = ClassReflection.getMethod(object.getClass(), methodName, args);
        } catch (ReflectionException e) {
            logger.error(e.getMessage());
            return;
        }

        //Get the reference to the specified object
        if (instanceReference == null) {
            addReference(object, object.getClass().getSimpleName());
            instanceReference = getInstanceReference(object);
        }

        addMethodToCache(instanceReference, method);
    }

    public void addMethod(Class clazz, String methodName, Class... args) {
        if (clazz == null) throw new IllegalArgumentException("Class is null");
        if (methodName == null || methodName.isEmpty()) throw new IllegalArgumentException("Invalid method name");

        StaticReference staticReference = getStaticReference(clazz);
        if (staticReference != null) {
            if (hasMethod(staticReference, methodName, args)) return;
        }

        Method method = null;

        try {
            method = ClassReflection.getMethod(clazz, methodName, args);
            if (!method.isStatic()) throw new Exception("Method must be static");
        } catch (Exception e) {
            logger.error(e.getMessage());
            return;
        }

        if (staticReference == null) {
            addReference(clazz, clazz.getSimpleName());
            staticReference = getStaticReference(clazz);
        }

        addMethodToCache(staticReference, method);
    }

    private void addMethodToCache(ClassReference classReference, Method method) {
        MethodReference methodReference = methodLookup.getReferenceMethod(method);
        if (methodReference == null) {
            methodReference = methodLookup.addReferenceMethod(method);
        }

        MethodInfo info = new MethodInfo(classReference, methodReference);

        //Get a set containing all added methods for the reference
        ObjectSet<MethodInfo> allMethodsForReference = methodsByReference.get(classReference);
        if (allMethodsForReference == null) {
            allMethodsForReference = new ObjectSet<>();
            methodsByReference.put(classReference, allMethodsForReference);
        }
        allMethodsForReference.add(info);

        ObjectSet<MethodInfo> methodsWithSameName = this.methodsByName.get(method.getName());
        if (methodsWithSameName == null) {
            methodsWithSameName = new ObjectSet<>();
            methodsByName.put(method.getName(), methodsWithSameName);
        }
        methodsWithSameName.add(info);
    }

    public void add(Object object, String objectID) {
        if (object == null) throw new IllegalArgumentException("Object is null.");
        if (objectID == null || objectID.isEmpty()) throw new IllegalArgumentException("Invalid object id");

        //Check if an instance reference is using the object as a reference
        InstanceReference instanceReference = getInstanceReference(object);

        classReferenceCheck:
        if (instanceReference == null) {
            if (object.getClass().isAnnotationPresent(ConsoleObject.class)) {

                if (hasReference(objectID)) {
                    ConsoleObject c = ClassReflection.getAnnotation(object.getClass(), ConsoleObject.class).getAnnotation(ConsoleObject.class);
                    objectID = !c.value().isEmpty() ? c.value() : object.getClass().getSimpleName();
                }

                if (hasReference(objectID)) {
                    logger.error("[Error]", "Could not create object");
                    break classReferenceCheck;
                }

                addReference(object, objectID);
                instanceReference = getInstanceReference(object);
            }
        }

        //Add methods with the ConsoleMethod annotation
        if (instanceReference != null) {
            Method[] methods = ClassReflection.getMethods(object.getClass());

            for (Method m : methods) {
                if (!m.isPublic() || !m.isAnnotationPresent(ConsoleMethod.class)) continue;
                addMethodToCache(instanceReference, m);
            }
        }

        //Add fields with the ConsoleReference annotation
        Field[] fields = ClassReflection.getFields(object.getClass());
        for (Field f : fields) {
            if (!f.isPublic() || !f.isAnnotationPresent(ConsoleObject.class)) continue;

            try {
                String name = f.getDeclaredAnnotation(ConsoleObject.class).getAnnotation(ConsoleObject.class).value();
                addReference(f.get(object), name);
            } catch (ReflectionException e) {
                logger.error(e.getMessage());
            }
        }
    }

    /**
     * Saves an instance of a method for a class to be shared by other instances of the class. <br><br>
     */
    public static class MethodLookup {

        private final ObjectMap<Class<?>, ObjectSet<MethodReference>> references = new ObjectMap<>();
        private final DebugLogger logger = new DebugLogger(MethodLookup.class, DebugLogger.DEBUG);

        private MethodLookup() {
        }

        ObjectSet<MethodReference> getMethods(Class<?> c) {
            return references.get(c);
        }

        boolean hasReferenceMethod(Method method) {
            return getReferenceMethod(method) != null;
        }

        boolean hasReferenceMethod(MethodInfo info) {
            return getReferenceMethod(info.getMethodReference().getMethod()) != null;
        }

        boolean hasReferenceMethod(Class<?> declaringClass, String name, Class<?>... args) {
            return getReferenceMethod(declaringClass, name, args) != null;
        }

        MethodReference getReferenceMethod(Method m) {
            return getReferenceMethod(m.getDeclaringClass(), m.getName(), m.getParameterTypes());
        }

        MethodReference getReferenceMethod(MethodInfo info) {
            return getReferenceMethod(info.getMethodReference().getMethod());
        }

        MethodReference getReferenceMethod(Class<?> declaringClass, String name, Class<?>... argTypes) {
            ObjectSet<MethodReference> classReference = references.get(declaringClass);
            if (classReference == null) return null;

            Iterator<MethodReference> it = classReference.iterator();
            while (it.hasNext()) {
                MethodReference ref = it.next();

                if (ref.getName().equals(name) && ConsoleUtils.areArgsEqual(ref.getArgs(), ConsoleUtils.defaultIfNull(argTypes, ConsoleUtils.EMPTY_ARGUMENT_TYPES))) {
                    return ref;
                }
            }
            return null;
        }

        private String argsToString(Class<?>[] args) {
            if (args.length == 0) return "";

            StringBuilder builder = new StringBuilder();

            for (int i = 0; i < args.length; i++) {
                builder.append(args[i].getSimpleName());
                if (i < (args.length - 1)) builder.append(", ");
            }

            return builder.toString();
        }

        MethodReference addReferenceMethod(Method method) {
            ObjectSet<MethodReference> classMethodReferences = references.get(method.getDeclaringClass());

            //If there is no reference to this class create one
            if (classMethodReferences == null) {
                classMethodReferences = new ObjectSet<MethodReference>();
                references.put(method.getDeclaringClass(), classMethodReferences);

                StringBuilder builder = new StringBuilder();
                builder.append("Reference:[Class] ");
                builder.append("Name:[");
                builder.append(method.getDeclaringClass().getSimpleName());
                builder.append("] Full:[");
                builder.append(method.getDeclaringClass().getCanonicalName());
                builder.append(']');
                logger.info("[Added]", builder.toString());
            }

            MethodReference reference = getReferenceMethod(method);

            if (reference == null) {
                reference = new MethodReference(method);
                classMethodReferences.add(reference);

                StringBuilder builder = new StringBuilder()
                        .append("Reference:[Method] ")
                        .append("Name:[")
                        .append(method.getName())
                        .append("] Args:[")
                        .append(argsToString(method.getParameterTypes()))
                        .append("] DeclaringClass:[")
                        .append(method.getDeclaringClass().getSimpleName())
                        .append("] Full:[")
                        .append(method.getDeclaringClass().getCanonicalName())
                        .append(']');
                logger.info("[Added]", builder.toString());
            }

            return reference;
        }

    }

}
