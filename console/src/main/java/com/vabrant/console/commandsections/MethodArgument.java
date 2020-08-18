package com.vabrant.console.commandsections;

import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.badlogic.gdx.utils.Pools;
import com.vabrant.console.ConsoleCache;
import com.vabrant.console.ConsoleUtils;
import com.vabrant.console.MethodInfo;
import com.vabrant.console.SectionSpecifier;
import com.vabrant.console.SectionSpecifier.Builder.Rules;
import com.vabrant.console.commandsections.MethodArgument.MethodArgumentInfo;

public class MethodArgument implements Argument, Parsable<MethodArgumentInfo>, Executable {
	
	public static SectionSpecifier createSpecifier() {
		return new SectionSpecifier.Builder()
				.specifiedSection(MethodArgument.class)
				
				.addRule(Rules.CHARACTER)
				.addRule(Rules.CHARACTER | Rules.DIGIT | Rules.ZERO_OR_MORE)
				.addRule(Rules.CUSTOM, ".")
				.addRule(Rules.CHARACTER)
				.addRule(Rules.CHARACTER | Rules.DIGIT | Rules.ZERO_OR_MORE)
				.or()
				
				.addRule(Rules.CUSTOM, ".")
				.addRule(Rules.CHARACTER)
				.addRule(Rules.CHARACTER | Rules.DIGIT | Rules.ZERO_OR_MORE)
				.build();
	}
	
	@Override
	public MethodArgumentInfo parse(ConsoleCache cache, String sectionText) throws RuntimeException {
		String name;
		ObjectSet<MethodInfo> methods;
		
		if(sectionText.contains(".")) {
			if(sectionText.charAt(0) == '.') {
				name = sectionText.substring(1, sectionText.length());
				methods = cache.getAllMethodsWithName(name);
			}
			else {
				String[] parts = sectionText.split("[.]");
				name = parts[1];
				methods = cache.getAllMethodsByReference(parts[0]);
			}
		}
		else {
			name = sectionText;
			methods = cache.getAllMethodsWithName(name);
		}
		
//		if(methods == null) throw new RuntimeException("Method not found: " + sectionText);
//		if(methods == null) throw new RuntimeException("Method (" + name + ") not found.");
		
		return Pools.obtain(MethodArgumentInfo.class).set(name, methods);
	}
	
	@Override
	public Object execute(Object... executableInfo) throws RuntimeException {
		MethodArgumentInfo methodArgumentInfo = (MethodArgumentInfo) executableInfo[0];
		Object[] arguments = executableInfo.length > 1 ? (Object[]) executableInfo[1] : ConsoleUtils.EMPTY_ARGUMENTS;
		
		Class<?>[] argumentTypes = ConsoleUtils.EMPTY_ARGUMENT_TYPES;
		if(arguments.length > 0) {
			argumentTypes = new Class[arguments.length];
			for(int i = 0; i < arguments.length; i++) {
				argumentTypes[i] = arguments[i].getClass();
			}
		}
		
		ObjectSet<MethodInfo> methods = methodArgumentInfo.getMethods();
		MethodInfo info = null;
		for(MethodInfo m : methods) {
			if(!methodArgumentInfo.equals(m)) continue;
			if(!ConsoleUtils.equals(m.getArgs(), argumentTypes)) continue;
			info = m;
			break;
		}
		if(info == null) throw new RuntimeException("Method not found.");

		Object argument = null;
		try {
			argument = info.invoke(arguments);
		}
		catch(ReflectionException e) {
			e.printStackTrace();
		}
		
		return argument;
	}

	public static class MethodArgumentInfo implements Poolable {
		private String methodName;
		private String referenceName;
		private ObjectSet<MethodInfo> methods;
		
		public MethodArgumentInfo set(String methodName, ObjectSet<MethodInfo> methods) {
			this.methodName = methodName;
			this.methods = methods;
			return this;
		}
		
		public String getMethodName() {
			return methodName;
		}
		
		public ObjectSet<MethodInfo> getMethods() {
			return methods;
		}
		
		public boolean equals(MethodInfo info) {
			if(referenceName != null && !referenceName.equals(info.getClassReference().getName())) return false;
			if(!methodName.equals(info.getMethodReference().getName())) return false;
			return true; 
		}
		
		@Override
		public void reset() {
			methodName = null;
			methods = null;
		}
	}

}
