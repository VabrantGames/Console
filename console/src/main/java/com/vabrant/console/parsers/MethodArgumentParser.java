package com.vabrant.console.parsers;

import com.badlogic.gdx.utils.Pools;
import com.vabrant.console.ConsoleCache;

public class MethodArgumentParser implements Parsable<ConsoleCacheAndStringInput, MethodArgumentInfo> {

    @Override
    public MethodArgumentInfo parse(ConsoleCacheAndStringInput input) throws RuntimeException {
        MethodArgumentInfo info = Pools.obtain(MethodArgumentInfo.class);
        String text = input.getText();
        ConsoleCache cache = input.getCache();

        if (text.charAt(0) == '.') {
            info.setMethodName(text.substring(1));
            info.setMethods(cache.getAllMethodsWithName(info.getMethodName()));

            if (info.getMethods() == null) throw new RuntimeException("[MethodNotFound] : [Name]:" + info.getMethodName());
        } else if (Character.isAlphabetic(text.charAt(0))) {
            if (text.contains(".")) {
                int idx = text.indexOf('.');

                String referenceName = text.substring(0, idx);
                info.setClassReference(cache.getReference(referenceName));

                if (info.getClassReference() == null) throw new RuntimeException("[ReferenceNotFound] : [Name]:" + referenceName);

                info.setMethodName(text.substring(idx + 1));

                if (!cache.hasMethodWithName(info.getClassReference(), info.getMethodName())) throw new RuntimeException("[MethodNotFound] : [Name]:" + info.getMethodName() + " [Reference]:" + referenceName);

                info.setMethods(cache.getAllMethodsByReference(referenceName));
            } else {
                info.setMethodName(text);
                info.setMethods(cache.getAllMethodsWithName(info.getMethodName()));

                if (info.getMethods() == null) throw new RuntimeException("[MethodNotFound] : [Name]:" + info.getMethodName());
            }
        }

        if (info.getClassReference() == null && info.getMethods() == null) throw new RuntimeException("[ParsingError] : [Input]:" + text);

        return info;
    }

}
