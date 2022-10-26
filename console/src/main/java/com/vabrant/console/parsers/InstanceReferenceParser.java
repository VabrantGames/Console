package com.vabrant.console.parsers;

import com.vabrant.console.ConsoleCache;
import com.vabrant.console.InstanceReference;

public class InstanceReferenceParser implements Parsable<ConsoleCacheAndStringData, InstanceReference> {

    @Override
    public InstanceReference parse(ConsoleCacheAndStringData data) throws RuntimeException {
        InstanceReference instanceReference = data.getCache().getInstanceReference(data.getText());
        if (instanceReference == null) throw new RuntimeException("No instance found");
        return instanceReference;
    }
}
