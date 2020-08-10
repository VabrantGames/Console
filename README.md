# Console

A console made for the libGDX framework with a Java like syntax to aid you in the creation or debugging of your app.

## Features 
- Java like syntax ```object.method arg1 arg2...``` ```object.method(.method(arg1),  10)```

## Usage
##### Create a console instance in the main class of your app.

```java
Console console = new Console();
```
Only one instance of console is necessary so this instance should be passed to all screens or made static.

##### Create a cache for every screen.

```java 
ConsoleCache cache = new ConsoleCache();
```

This cache will keep track of all references you add for the screen.

##### Set cache

```java 
public void show() {
  console.setCache(cache); 
}
```

Everytime you switch screens you switch the current cache of the console to that of the screen. So the console can only call references you have added for that screen.

## TODO 
- Implement advanced syntax ```object.method(.method(arg1),  10)```
- Cleanup and finish shortcuts
- Implement console views (History, Methods, Objects, Commands, etc)
