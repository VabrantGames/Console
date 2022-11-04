# Console

[![](https://jitpack.io/v/com.vabrantgames.console/console.svg)](https://jitpack.io/#com.vabrantgames/console)

A console made for the libGDX framework with a Java like syntax to aid you in the creation or debugging of your app.

## Features 
- Java like syntax ```object.method arg1 arg2...``` ```object.method(.method(arg1),  10)```

## Usage
##### Create a console instance in the main class of your app.

```java
GUIConsole console = new GUIConsole();
```
Only one instance of console is necessary so this instance should be passed to all screens or made static.

##### Create a cache for every screen.

```java 
ConsoleCache cache = new ConsoleCache();
```

This cache will keep track of all references you add for the screen.

##### Set cache

In the show method or anywhere you find appropriate set the cache.

```java 
console.setCache(cache); 
```

Everytime you switch screens you switch the current cache of the console to that of the screen. So the console can only call references you have added for that screen.

---

## How to add to project

Add [jitpack](https://jitpack.io/#VabrantGames/ActionSystem) as a repository in your main build.gradle then this project 
as a dependency.

```grovy
repositories {
    maven {url 'https://jitpack.io'}
}

dependency {
    implementation 'com.vabrant.console:console:version'
}
```

## TODO 
- Implement Advanced Syntax ```object.method(.method(arg1),  10)```
- Implement Shortcuts
- Implement Logging
- Implement Views (LogHistory, Commands, Shortcuts, CommandHistory)
- Implement More Arguments (String, Instance)
