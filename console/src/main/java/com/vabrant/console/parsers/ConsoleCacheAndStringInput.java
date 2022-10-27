package com.vabrant.console.parsers;

import com.vabrant.console.ConsoleCache;

public class ConsoleCacheAndStringInput {

    private ConsoleCache cache;
    private String text;

    public ConsoleCacheAndStringInput setConsoleCache(ConsoleCache cache) {
        this.cache = cache;
        return this;
    }

    public ConsoleCacheAndStringInput setText(String text) {
        this.text = text;
        return this;
    }

    public ConsoleCache getCache() {
        return cache;
    }

    public String getText() {
        return text;
    }
}
