package com.vabrant.console.parsers;

import com.badlogic.gdx.utils.Pools;
import com.vabrant.console.ConsoleCache;

public class MethodArgumentParser implements Parsable<ConsoleCacheAndStringData, MethodArgumentInfo> {

    @Override
    public MethodArgumentInfo parse(ConsoleCacheAndStringData data) throws RuntimeException {
        MethodArgumentInfo info = Pools.obtain(MethodArgumentInfo.class);
        String text = data.getText();
        ConsoleCache cache = data.getCache();

        if (text.charAt(0) == '.') {
//            info.methodName = text.substring(1);
            info.setMethodName(text.substring(1));
            info.setMethods(cache.getAllMethodsWithName(info.getMethodName()));
//            info.methods = cache.getAllMethodsWithName(info.methodName);
        } else if (Character.isAlphabetic(text.charAt(0))) {
            if (text.contains(".")) {
                int idx = text.indexOf('.');
//                info.referenceName = text.substring(0, idx);
//                info.classReference = cache.getReference(info.referenceName);

                info.setReferenceName(text.substring(0, idx));
                info.setClassReference(cache.getReference(info.getReferenceName()));

                if (info.getClassReference() == null) throw new RuntimeException("No reference found");

//                info.methodName = text.substring(idx + 1);
                info.setMethodName(text.substring(idx + 1));

                if (cache.hasMethodWithName(info.getClassReference(), info.getMethodName())) throw new RuntimeException("Reference has no method with name added");

//                info.methods = cache.getAllMethodsByReference(info.referenceName);
                info.setMethods(cache.getAllMethodsByReference(info.getReferenceName()));
            } else {
//                info.methodName = text;
//                info.methods = cache.getAllMethodsWithName(info.methodName);
                info.setMethodName(text);
                info.setMethods(cache.getAllMethodsWithName(info.getMethodName()));
            }
        } else {
            throw new RuntimeException("Error parsing method");
        }

        return info;
    }

//    public static class MethodArgumentInfo implements Pool.Poolable {
//        private String methodName;
//        private String referenceName;
//        private ObjectSet<MethodInfo> methods;
//        private ClassReference classReference;
//
//        public MethodArgumentInfo set(String methodName, ObjectSet<MethodInfo> methods) {
//            this.methodName = methodName;
//            this.methods = methods;
//            return this;
//        }
//
//        public void setClassReference(ClassReference classReference) {
//            this.classReference = classReference;
//        }
//
//        public ClassReference getClassReference() {
//            return classReference;
//        }
//
//        public String getMethodName() {
//            return methodName;
//        }
//
//        public ObjectSet<MethodInfo> getMethods() {
//            return methods;
//        }
//
//        public boolean equals(MethodInfo info) {
//            if(referenceName != null && !referenceName.equals(info.getClassReference().getName())) return false;
//            if(!methodName.equals(info.getMethodReference().getName())) return false;
//            return true;
//        }
//
//        @Override
//        public void reset() {
//            methodName = null;
//            methods = null;
//        }
//    }
}
