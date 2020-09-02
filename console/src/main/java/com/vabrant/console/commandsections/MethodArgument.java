package com.vabrant.console.commandsections;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.vabrant.console.ConsoleCache;
import com.vabrant.console.ConsoleUtils;
import com.vabrant.console.DebugLogger;
import com.vabrant.console.MethodInfo;
import com.vabrant.console.SectionSpecifier;
import com.vabrant.console.SectionSpecifier.Builder.Rules;
import com.vabrant.console.commandsections.MethodArgument.MethodArgumentInfo;

public class MethodArgument implements Argument, Parsable<MethodArgumentInfo>, Executable {
	
	private final DebugLogger logger = new DebugLogger(MethodArgument.class, DebugLogger.DEBUG);
	
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
	public MethodArgumentInfo parse(ConsoleCache cache, String sectionText, Object extra) throws RuntimeException {
		String name;
		ObjectSet<MethodInfo> methods;
		
		MethodArgumentInfo methodArgumentInfo = (MethodArgumentInfo)extra;
		
		//Find a set of method from a specified object or of methods with the same name
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
		
		methodArgumentInfo.set(name, methods);
		methodArgumentInfo.prepareUserArguments();
		
		if(logger.getLogLevel() == DebugLogger.DEBUG) {
			StringBuilder b = new StringBuilder();
			b.append("[Searching] ");
			b.append("Method:[").append(name).append("]");
			b.append("Args:[");
			
			Class<?>[] argumentTypes = methodArgumentInfo.getArgumentTypes();
			for(int i = 0; i < argumentTypes.length; i++) {
				b.append(argumentTypes[i].getSimpleName());
				if(i < (argumentTypes.length - 1)) b.append(',');
			}
			b.append("]");
			logger.debug("[Parsing]", b.toString());
		}
		
		//Find a method with the same reference, name, and args
		MethodInfo methodInfo = null;
		for(MethodInfo i : methods) {
			if(!methodArgumentInfo.equals(i)) continue;
			methodInfo = i;
			break;
		}
		
		if(methodInfo == null) {
			StringBuilder builder = new StringBuilder();
			builder.append("Method: ").append(name).append(" not found with arguments [");
			
			Class<?>[] argumentTypes = methodArgumentInfo.getArgumentTypes();
			for(int i = 0; i < argumentTypes.length; i++) {
				builder.append(argumentTypes[i].getSimpleName());
				if(i < (argumentTypes.length - 1)) builder.append(',');
			}
			
//			throw new RuntimeException("Method not found");
			throw new RuntimeException(builder.toString());
		}
		
		methodArgumentInfo.setMethodInfo(methodInfo);

		return methodArgumentInfo;
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
		private Array<CommandSection> userArguments = new Array<>();
		private Class<?>[] userArgumentReturnTypes;
		private MethodInfo methodInfo;
		
		public void setMethodInfo(MethodInfo methodInfo) {
			this.methodInfo = methodInfo;
		}
		
		public MethodInfo getMethodInfo() {
			return methodInfo;
		}
		
		public void addArgumentSection(CommandSection type) {
			userArguments.add(type);
		}
		
		public void prepareUserArguments() {
			if(userArguments.size == 0) userArgumentReturnTypes = ConsoleUtils.EMPTY_ARGUMENT_TYPES;
			userArgumentReturnTypes = new Class<?>[userArguments.size];
			for(int i = 0; i < userArgumentReturnTypes.length; i++) {
				CommandSection s = userArguments.get(i);
				userArgumentReturnTypes[i] = s.getReturnType();
			}
		}
		
		public Class<?>[] getArgumentTypes() {
			return userArgumentReturnTypes;
		}
		
		public int getArgLength() {
			return userArguments.size;
		}

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
			if(!ConsoleUtils.equals(info.getArgs(), userArgumentReturnTypes)) return false;
			return true; 
		}
		
		@Override
		public void reset() {
			methodName = null;
			referenceName = null;
			methods = null;
			userArguments.clear();
			userArgumentReturnTypes = null;
			methodInfo = null;
		}
	}

}
