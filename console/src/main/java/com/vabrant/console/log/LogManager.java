package com.vabrant.console.log;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;

public class LogManager {

    private int maxEntries;
    private Array<Log> entries;

    public LogManager() {
        this(100);
    }

    public LogManager(int maxEntries) {
        this.maxEntries = maxEntries;
        entries = new Array<>(maxEntries);
    }

    public Log add(String tag, String message, LogLevel level) {
        if ((entries.size + 1) > maxEntries) {
            Pools.free(entries.removeIndex(0));
        }

        Log log = Pools.obtain(Log.class)
                .setTag(tag)
                .setMessage(message)
                .setLogLevel(level);
        entries.add(log);
        return log;
    }

    public Array<Log> getEntries() {
        return entries;
    }
}
