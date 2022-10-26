package com.vabrant.console.parsers;

import com.vabrant.console.ConsoleCache;

public class ConsoleCacheAndStringData {

    private ConsoleCache cache;
    private String text;

    public ConsoleCacheAndStringData setConsoleCache(ConsoleCache cache) {
        this.cache = cache;
        return this;
    }

    public ConsoleCacheAndStringData setText(String text) {
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
