package com.vabrant.console.commandsections;

public class MethodExecutor implements Executable {

	@Override
	public Object execute(Object... executableInfo) throws RuntimeException {
//		MethodArgumentInfo methodArgumentInfo = (MethodArgumentInfo) executableInfo[0];
//		Object[] arguments = executableInfo.length > 0 ? (Object[]) executableInfo[1] : ConsoleUtils.EMPTY_ARGUMENTS;
//
//		Class<?>[] argumentTypes = ConsoleUtils.EMPTY_ARGUMENT_TYPES;
//		if(arguments.length > 0) {
//			argumentTypes = new Class[arguments.length];
//			for(int i = 0; i < arguments.length; i++) {
//				argumentTypes[i] = arguments[i].getClass();
//			}
//		}
//
//		ObjectSet<MethodInfo> methods = methodArgumentInfo.getMethods();
//		MethodInfo info = null;
//		for(MethodInfo m : methods) {
//			if(!methodArgumentInfo.equals(m)) continue;
////			if(!ConsoleUtils.areArgsEqual(m.getArgs(), argumentTypes)) continue;
//			info = m;
//			break;
//		}
//		if(info == null) throw new RuntimeException("Method not found.");
//
//		Object argument = null;
////		try {
//////			argument = info.invoke(arguments);
////		}
////		catch(ReflectionException e) {
////			e.printStackTrace();
////		}
//
		return null;
	}

}
