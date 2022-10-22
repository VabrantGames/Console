package com.vabrant.console.commandsections;

import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.Pools;
import com.vabrant.console.*;
import com.vabrant.console.SectionSpecifier.Builder.Rules;
import com.vabrant.console.commandsections.MethodArgument.MethodArgumentInfo;

public class MethodArgument implements Argument, Parsable<MethodArgumentInfo> {
	
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
		MethodArgumentInfo info = Pools.obtain(MethodArgumentInfo.class);

		if (sectionText.charAt(0) == '.') {
			info.methodName = sectionText.substring(1);
			info.methods = cache.getAllMethodsWithName(info.methodName);
		} else if (Character.isAlphabetic(sectionText.charAt(0))) {
			if (sectionText.contains(".")) {
				int idx = sectionText.indexOf('.');
				info.referenceName = sectionText.substring(0, idx);
				info.classReference = cache.getReference(info.referenceName);

				if (info.classReference == null) throw new RuntimeException("No reference found");

				info.methodName = sectionText.substring(idx + 1);

				if (cache.hasMethodWithName(info.classReference, info.methodName)) throw new RuntimeException("Reference has no method with name added");

				info.methods = cache.getAllMethodsByReference(info.referenceName);
			} else {
				info.methodName = sectionText;
				info.methods = cache.getAllMethodsWithName(info.methodName);
			}
		} else {
			throw new RuntimeException("Error parsing method");
		}


//		if(sectionText.contains(".")) {
//			if(sectionText.charAt(0) == '.') {
//				name = sectionText.substring(1, sectionText.length());
//				methods = cache.getAllMethodsWithName(name);
//			}
//			else {
//				String[] parts = sectionText.split("[.]");
//				name = parts[1];
////				methods = cache.getClassMethodReference().get
////				methods = cache.getAllMethodsByReference(parts[0]);
//			}
//		}
//		else {
//			name = sectionText;
//			methods = cache.getAllMethodsWithName(name);
//		}
		
//		if(methods == null) throw new RuntimeException("Method not found: " + sectionText);
//		if(methods == null) throw new RuntimeException("Method (" + name + ") not found.");
		
		return info;
	}

	public static class MethodArgumentInfo implements Poolable {
		private String methodName;
		private String referenceName;
		private ObjectSet<MethodInfo> methods;
		private ClassReference classReference;

		public MethodArgumentInfo set(String methodName, ObjectSet<MethodInfo> methods) {
			this.methodName = methodName;
			this.methods = methods;
			return this;
		}

		public void setClassReference(ClassReference classReference) {
			this.classReference = classReference;
		}

		public ClassReference getClassReference() {
			return classReference;
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
