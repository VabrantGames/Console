package com.vabrant.console.commandsections;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.vabrant.console.ClassReference;
import com.vabrant.console.ConsoleCache;
import com.vabrant.console.ConsoleCache.ConsoleCacheClassAdndMethodReference;
import com.vabrant.console.ConsoleUtils;
import com.vabrant.console.DebugLogger;
import com.vabrant.console.MethodReference;
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
		String methodName;
		ObjectSet<?> methodsTemp;
		
		MethodArgumentInfo info = (MethodArgumentInfo)extra;
		
		//Get a set of methods from a specified object or methods with the same name
		if(sectionText.contains(".")) {
			if(sectionText.charAt(0) == '.') {
				methodName = sectionText.substring(1, sectionText.length());
				methodsTemp = cache.getAllMethodsWithName(methodName);
			}
			else {
				String[] parts = sectionText.split("[.]");
				methodName = parts[1];
				
				ClassReference classReference = cache.getClassReference(parts[0]);
				
				if(classReference == null) {
					throw new RuntimeException("ClassReference " + parts[0] + " not found.");
				}
				
				methodsTemp = cache.getAllMethodsByReference(classReference);
				info.setClassReference(cache.getClassReference(classReference));
			}
		}
		else {
			methodName = sectionText;
			methodsTemp = cache.getAllMethodsWithName(methodName);
		}
		
		info.setName(methodName);
		info.createArrayOfReturnValuesFromUserArguments();
		
		if(logger.getLogLevel() == DebugLogger.DEBUG) {
			StringBuilder builder = new StringBuilder();
			builder.append("Method:[").append(methodName).append("] ");
			builder.append("Args:[");
			
			Class<?>[] argumentTypes = info.getUserArgumentsReturnTypes();
			for(int i = 0; i < argumentTypes.length; i++) {
				builder.append(argumentTypes[i].getSimpleName());
				if(i < (argumentTypes.length - 1)) builder.append(',');
			}
			builder.append("]");
			logger.debug("[Parsing]", builder.toString());
		}

		MethodReference methodReference = null;
		if(info.hasClassReference()) {
			ObjectSet<MethodReference> methods = (ObjectSet<MethodReference>)methodsTemp;
			for(MethodReference m : methods) {
				if(!info.equalsMethodReference(m)) continue;
				methodReference = m;
				break;
			}
		}
		else {
			ObjectSet<ConsoleCacheClassAdndMethodReference> methods = (ObjectSet<ConsoleCacheClassAdndMethodReference>)methodsTemp;
			for(ConsoleCacheClassAdndMethodReference m : methods) {
				if(!info.equalsMethodReference(m.getMethodReference())) continue;
				methodReference = m.getMethodReference();
				info.setClassReference(m.getClassReference());
				break;
			}
		}
		
		if(methodReference == null) {
			StringBuilder builder = new StringBuilder();
			builder.append("Method: ").append(methodName).append(" not found with arguments [");
			
			Class<?>[] argumentTypes = info.getUserArgumentsReturnTypes();
			for(int i = 0; i < argumentTypes.length; i++) {
				builder.append(argumentTypes[i].getSimpleName());
				if(i < (argumentTypes.length - 1)) builder.append(',');
			}
			
			throw new RuntimeException(builder.toString());
		}
		
		info.setMethodReference(methodReference);

		return info;
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
		
//		ObjectSet<MethodInfo> methods = methodArgumentInfo.getMethods();
//		MethodInfo info = null;
//		for(MethodInfo m : methods) {
//			if(!methodArgumentInfo.equals(m)) continue;
//			if(!ConsoleUtils.equals(m.getArgs(), argumentTypes)) continue;
//			info = m;
//			break;
//		}
//		if(info == null) throw new RuntimeException("Method not found.");
//
//		Object argument = null;
//		try {
//			argument = info.invoke(arguments);
//		}
//		catch(ReflectionException e) {
//			e.printStackTrace();
//		}
//		
		return null;
	}

	public static class MethodArgumentInfo implements Poolable {
		private String methodName;
		private ClassReference classReference;
		private MethodReference methodReference;
		private Array<CommandSection> userArguments = new Array<>();
		private Class<?>[] userArgumentsReturnTypes;
		
		public void setMethodReference(MethodReference methodReference) {
			this.methodReference = methodReference;
		}
		
		public MethodReference getMethodReference() {
			return methodReference;
		}
		
		public void setClassReference(ClassReference classReference) {
			this.classReference = classReference;
		}
		
		public ClassReference getClassReference() {
			return classReference;
		}
		
		public boolean hasClassReference() {
			return classReference != null;
		}

		public void addArgumentSection(CommandSection type) {
			userArguments.add(type);
		}
		
		public void createArrayOfReturnValuesFromUserArguments() {
			if(userArguments.size == 0) {
				userArgumentsReturnTypes = ConsoleUtils.EMPTY_ARGUMENT_TYPES;
				return;
			}
			
			userArgumentsReturnTypes = new Class<?>[userArguments.size];
			final int startIndex = userArgumentsReturnTypes.length - 1;
			for(int i = startIndex; i >= 0; i--) {
				CommandSection s = userArguments.get(i);
				userArgumentsReturnTypes[startIndex - i] = s.getReturnType();
			}
		}
		
		public Class<?>[] getUserArgumentsReturnTypes() {
			return userArgumentsReturnTypes;
		}
		
		public int getUserArgumentLength() {
			return userArguments.size;
		}

		public void setName(String methodName) {
			this.methodName = methodName;
		}
		
		public String getMethodName() {
			return methodName;
		}

		public boolean equalsMethodReference(MethodReference reference) {
			if(!methodName.equals(reference.getName()) || !ConsoleUtils.equals(reference.getArgs(), userArgumentsReturnTypes)) return false;
			return true;
		}

		@Override
		public void reset() {
			methodName = null;
			classReference = null;
			userArguments.clear();
			userArgumentsReturnTypes = null;
		}
	}

}
