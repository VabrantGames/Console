package com.vabrant.console;

public class CommandExecutionData implements ExecutionData {

    public static final String FAIL_EVENT = "fail";
	public static final String SUCCESS_EVENT = "success";

    private ConsoleCache cache;
    private CommandExecutionEvent event;
    private EventManager eventManager;
    private CommandExecutionSettings settings;

    public CommandExecutionData() {
       this(null);
    }

    public CommandExecutionData(CommandExecutionSettings settings) {
        this.settings = settings;
        eventManager = new EventManager(FAIL_EVENT, SUCCESS_EVENT);
        event = new CommandExecutionEvent(this);
    }

    public void setSettings(CommandExecutionSettings settings) {
        this.settings = settings;
    }

    public CommandExecutionSettings getSettings() {
        if (settings == null) {
            settings = new CommandExecutionSettings();
        }
        return settings;
    }

    public void subscribeToEvent(String event, EventListener<CommandExecutionEvent> listener) {
        eventManager.subscribe(event, listener);
    }

    public void unsubscribeFromEvent(String event, EventListener<CommandExecutionEvent> listener) {
        eventManager.unsubscribe(event, listener);
    }

    public void fireEvent(String type, CommandExecutionEvent event) {
       eventManager.fire(type, event);
    }

    public CommandExecutionEvent getEvent() {
        return event;
    }

    public EventManager getEventManager() {
        return eventManager;
    }

    public void setConsoleCache(ConsoleCache cache) {
        this.cache = cache;
        event.clear();
    }

    public ConsoleCache getConsoleCache() {
        return cache;
    }
}
