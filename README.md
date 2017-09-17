# Godot Google Play Services

This is the Google Play Game Services module for Godot Engine (https://github.com/okamstudio/godot)
- Android only
- Leaderboards
- Achievements
- Player Information
- module is designed so that it can be easily extended with needed functionality


## How to use
  1. Drop the godot_gpgs directory inside `[your/source/directory/for/godot]/modules`
  2. Get your APP_ID from Developers Console (https://developers.google.com/games/services/console/enabling)
     In android/AndroidManifestChunk.xml and replace YOUR_APP_ID value, it must begin with "\ ":
```xml
      <meta-data 
          android:name="com.google.android.gms.games.APP_ID"
          android:value="\ 012345678901" />
```
  NOTE: If another module has already a specified APP_ID, you should remove it (allowed only once in the manifest).

  3. [Recompile Android export template](http://docs.godotengine.org/en/latest/reference/compiling_for_android.html#compiling-export-templates).
  
  4. In your project goto Export->Target->Android:

	Options:
		Custom Package:
			- place your apk from build
		Permissions on:
			- Access Network State
			- Internet

## Configuring your game
To enable any Android module, add the module package name in the format of path to `modules` paramter (comma seperated if many) under `android` section (NOTE: all lower-case). You can do so using Godot project settings GUI, or by directly editing it and appending:

```ini
[android]
modules="org/godotengine/godot/GodotPlayGameServices"
```
## Common errors
If the plugin doesn't work:
* Take a look at [this page](https://developers.google.com/games/services/android/troubleshooting) and check if you've got everything setup as described 
* If you use both debug and release sign keys, than make sure you have two seperate APP entries for each key at Google Play Console > Game Services > Linked Apps 
* Drive API has to enabled for the app. To check go to:
  - Google Play Console > Game Services > Game details
  - Scroll down to the bottom of the page to section `API Console project` and follow the link with your app name
  - Click `ENABLE APIS AND SERVICES` find Drive API and enable it

## API reference
  **TODO**
