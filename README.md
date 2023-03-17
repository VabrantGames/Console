<div style="margin-top: 20px" align="center">
    <img src="./logo.svg" width="40%" alt="logo"/>
</div>

<h1 align="center"> Console </h1>

<p align="center">A development console made for the libGDX framework to aid you in the creation of your application. </p>

<p align="center">
    <a href="https://jitpack.io/#com.vabrantgames/console"><img src="https://jitpack.io/v/com.vabrantgames.console/console.svg"></a>
</p>

[//]: # ([![]&#40;https://jitpack.io/v/com.vabrantgames.console/console.svg&#41;]&#40;https://jitpack.io/#com.vabrantgames/console&#41;)

## Compatibility

- Built with Java 8
- Built with libGDX 1.9.11
- Tested with Java 8 and 11

## Features

- Java like syntax ```object.method arg1 arg2...``` ```object.method(.method(arg1), 10)```

## How to add to project

Add [jitpack](https://jitpack.io/#VabrantGames/ActionSystem) as a repository in your main build.gradle then this project
as a dependency.

```grovy
repositories {
    maven {url 'https://jitpack.io'}
}

dependency {
    implementation 'com.vabrant:console:version'
}
```

## Usage

### Create a console instance in the main class of your app.

```java
GUIConsole console=new GUIConsole();
```

**_Note:_** *Only one instance of console is necessary, so it should be passed around or made static.*

### Create a cache for every screen.

Normal cache

```java 
ConsoleCache cache = new ConsoleCache();
```

Cache with shortcuts

```java
GUIConsoleCache cache=new GUIConsoleCache();
```

**_Note_** *This cache will keep track of all references you add for that screen.*

### Set cache

In the show method or anywhere you find appropriate set the cache.

```java 
console.setCache(cache); 
```

**_Note:_** *Everytime you switch screens you should change the cache of the console so only methods you have added for
that screen are called.*

### Input

Set the console input by calling `Gdx.input.setInputProcessor(console.getInput())`or you can add the input to a
multiplexer to use with your
other processors.

### Using the console

To open the console press the <kbd>`</kbd> (grave) key. To close the console the same key can be pressed again,
or you can click anywhere the console isn't being rendered. This keybind can be changed by calling
```console.setToggleKeybind(keybind)```

### Shortcuts

Shortcuts are essentially pieces of code that are executed when a keybind is pressed. Shortcuts added
in [GUIConsole](console/src/main/java/com/vabrant/console/gui/GUIConsole.java)
are global shortcuts while shortcuts
added [GUIConsoleCache](console/src/main/java/com/vabrant/console/gui/GUIConsoleCache.java) are tied to the cache.
Both have helper methods for adding shortcuts quickly.

```java 
addShortcut(keybind, consoleCommand);
```

More options are available by calling `getShortcutManager()`.

Shortcuts have a maximum length four keys and must contain a maximum of one non-modifier key. This can be used in
combination with <kbd>control</kbd> <kbd>shift</kbd> <kbd>alt</kbd> / <kbd>command</kbd>

e.g. `int[] keybind = {Keys.CONTROL_LEFT, Keys.SHIFT_LEFT, Keys.A};`

**_Note:_** *Modifier keys with a left and right key are treated the same.*

Shortcuts can also be given a scope that restricts where the shortcut can be called.

```java
addShortcut(keybind, consoleCommand, scope);
```

| Scope | Description                                  |
|-------|----------------------------------------------|
| `DEFAULT` | Can only be called when the console is hidden|
| `COMMAND_LINE` | Can only be called when the command line is active |
| `GLOBAL` | Can be called anywhere |

## TODO

- Implement Advanced Syntax ```object.method(.method(arg1), 10)```
- Implement Views (LogHistory, Commands, Shortcuts, CommandHistory)
- Implement More Arguments (Instance)
