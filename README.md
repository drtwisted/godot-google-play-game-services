# Godot Google Play Game Services

![alt tag](https://pp.userapi.com/c837229/v837229821/590db/jU-IlKeQn-Q.jpg "Logo")

This is the Google Play Game Services module for Godot Engine (https://github.com/okamstudio/godot)
- Android only
- Leaderboards
- Achievements
- Player Information
- module is designed in a way to be easily extended with needed functionality


## How to use
  1. Drop the godot_gpgs directory in `[your/source/directory/for/godot]/modules`
  2. Get your APP_ID from [Developers Console](https://developers.google.com/games/services/console/enabling)
     Open `android/AndroidManifestChunk.xml` and replace YOUR_APP_ID value, it must begin with "\ ":
```xml
      <meta-data 
          android:name="com.google.android.gms.games.APP_ID"
          android:value="\ 012345678901" />
```
  NOTE: If another module has already a specified APP_ID, you should remove it (allowed only once in the manifest).

  3. Replace the `env.android_add_default_config("applicationId 'com.your.app.id'")` with your app unique ID in `android/config.py`

  4. [Recompile Android export template](http://docs.godotengine.org/en/latest/reference/compiling_for_android.html#compiling-export-templates).
  
  5. In your project goto Export->Target->Android:

	Options:
		Custom Package:
			- place your apk from build
		Permissions on:
			- Access Network State
			- Internet

## Configuring your game
To enable any Android module, add the module package name in the format of path to `modules` paramter (comma seperated if many) under `android`(all lower-case) section. You can do so by using Godot project settings GUI, or by editing it directly and appending the below text to your `engine.cfg`:

```ini
[android]
modules="org/godotengine/godot/GodotPlayGameServices"
```
## Common errors
If the plugin doesn't work:
* Take a look at [this page](https://developers.google.com/games/services/android/troubleshooting) and check if you've got everything setup as described 
* If you use both debug and release sign keys, than make sure you have two seperate APP entries for each key at Google Play Console > Game Services > Linked Apps 
* Drive API has to be enabled for the app. To check it go to:
  - Google Play Console > Game Services > Game details
  - Scroll down to the bottom of the page to section `API Console project` and follow the link with your app name
  - Click `ENABLE APIS AND SERVICES` find Drive API and enable it

# [API reference]

<h3 align="center">:GENERAL:</h3>

#### Methods
* **NOTE:** debug mode logs method calls and sensitive data, such as ids, use with caution

| Method name | Parameters | Returns | Description |
|-|-|-|-|
|**init**(instance_id)|*int* **instance_id** -- Godot app reference, can be obtained with GDScript's get_intance_ID() | - | Initialize GoogleApiClient |
|**init_with_debug**(instance_id)|*int* **instance_id** -- Godot app reference, can be obtained with GDScript's get_intance_ID() | - | Initialize GoogleApiClient with debug mode enabled |
|**set_debug**(state)| *bool* **state** | - | Change debug logging state during runtime |



<h3 align="center">:CONNECTION:</h3>

#### Methods
| Method name | Parameters | Returns | Description |
|-|-|-|-|
|**sign_in**()| - | - | Sign in to Google (Play Games) Services |
|**sign_out**()| - | - |  Sign out of Google (Play Games) Services (technically the same as disconnect) |
|**disconnect**()| - | - |  Disconnect client of Google (Play Games) Services |
|**reconnect**()| - | - |  Try to reconnect if connection is lost to Google (Play Games) Services |
|**is_signing_in**()| - | *bool* | Get connection status (is connecting) |
|**is_signed_in**()| - | *bool* | Get connection status (is connected) |
|**is_connection_suspended**()| - | *bool* | Get connection status (is suspended) |


#### Callback events
| Callback name | Parameters | Returns | Description |
|-|-|-|-|
|**_on_gpgs_connected**()| - | - | Callback on successful connection |
|**_on_gpgs_suspended**()| - | - |  Callback on connection suspended |
|**_on_gpgs_not_connected**()| - | - |  Callback on any operation when client is not connected |
|**_on_gpgs_not_signed_in**()| - | - |  Callback on any operation when client is not signed in |
|**_on_gpgs_connection_failed**(error_code, error_message)| *int* **error_code**; *String* **error_message** | - |  Callback on connection failed |




<h3 align="center">:LEADERBOARDS:</h3>

#### Methods
| Method name | Parameters | Returns | Description |
|-|-|-|-|
|**leaderboard_show_all_leaderboards**()| - | - | Show the list of leaderboards for your game |
|**leaderboard_show**(leaderbaord_id)| *String* **leaderbaord_id** -- leaderboard id obtained from your Developer Console | - |  Show leaderboard with specified id |
|**leaderboard_show_with_time_span**(id, time_span)| *String* **leaderbaord_id** -- leaderboard id obtained from your Developer Console; *int* **time_span** | - | Show leaderboard with specific id and a custom time span. Time span can be: 0 - DAILY, 1 - WEEKLY, 2 - ALL TIME |
|**leaderboard_submit_score**(leaderboard_id, score)| *String* **leaderboard_id** -- leaderboard id obtained from your Developer Console; *int* **score** -- score number the player earned | - | Submit a given score to the specified leaderboard |
|**leaderboard_submit_score_immediate**(leaderboard_id, score)| *String* **leaderboard_id** -- leaderboard id obtained from your Developer Console; *int* **score** -- score number the player earned | - |  Submit a given score to the specified leaderboard immediately with a callback result |

#### Callback events
| Callback name | Parameters | Returns | Description |
|-|-|-|-|
|**_on_leaderboard_score_submitted**(leaderboard_id, score, status)| *String* **leaderboard_id** -- leaderboard id obtained from your Developer Console; *int* **score** -- score number the player earned; *String* **status** -- status returned. "STATUS_OK" in case of success | - |  Callback on leaderboard_score_submitted_immediate |




<h3 align="center">:ACHIEVEMNTS:</h3>

#### Methods
| Method name | Parameters | Returns | Description |
|-|-|-|-|
|**achievement_show_list**()| - | - | Show achievements list |
|**achievement_unlock**(achievement_id)| *String* **achievement_id** -- achievement id obtained from your Developer Console | - |  Unlock an achievement with a given id |
|**achievement_unlock_immediate**(achievement_id)| *String* **achievement_id** -- achievement id obtained from your Developer Console | - | Unlock an achievement with a given id immediately with a callback result |
|**achievement_reveal**(achievement_id)| *String* **achievement_id** -- achievement id obtained from your Developer Console | - | Reveal a hidden achievement with a given id |
|**achievement_reveal_immediate**(achievement_id)| *String* **achievement_id** -- achievement id obtained from your Developer Console | - | Reveal a hidden achievement with a given id immediately with a callback result |
|**achievement_increment**(achievement_id, increment_amount)| *String* **achievement_id** -- achievement_id achievement id obtained from your Developer Console; *int* **increment_amount** -- ammount of points to increment the achievement by | - | Increment an achievement with a given id by a given amount of points |
|**achievement_increment_immediate**(achievement_id, increment_amount)| *String* **achievement_id** -- achievement_id achievement id obtained from your Developer Console; *int* **increment_amount** -- ammount of points to increment the achievement by | - | Increment an achievement with a given id by a given amount of points immediately with a callback result |

#### Callback events
| Callback name | Parameters | Returns | Description |
|-|-|-|-|
|**_on_achievements_loaded**()| - | - | Callback on achievements list loaded |
|**_on_achievement_unlocked**()| *String* **achievement_id** -- achievement_id achievement id obtained from your Developer Console; *String* **status** -- status returned. "STATUS_OK" in case of success| - | Callback on achievement unlocked (immediate) |
|**_on_achievement_revealed**()| *String* **achievement_id** -- achievement_id achievement id obtained from your Developer Console; *String* **status** -- status returned. "STATUS_OK" in case of success| - | Callback on achievement revealed (immediate) |
|**_on_achievement_incremented**()| *String* **achievement_id** -- achievement_id achievement id obtained from your Developer Console; *int* **increment_amount** -- increment_amount ammount of points to increment the achievement by; *String* **status** -- status returned. "STATUS_OK" in case of success| - | Callback on achievement unlocked (immediate) |




<h3 align="center">:PLAYER INFO:</h3>

#### Methods
* **NOTE:** If player info failed to load then every method will return an empty string

| Method name | Parameters | Returns | Description |
|-|-|-|-|
|**update_player_info**()| - | - | Update/download player info data from Google Play Game Services |
|**get_player_id**()| - | *String* **player_id** |  Get player ID |
|**get_player_display_name**()| - | *String* **player_display_name** -- e.g. Ed Boon | Get a player Display Name |
|**get_player_title**()|- | *String* **player_title** -- e.g. n00b | Get player Title |
|**get_player_icon_image_uri**()| - | *String* **player_icon_image_uri** | Get URI of Player's Icon Image if player info is loaded  |
|**get_player_current_level_number**()| - | *int* **player_level_number** -- current level number or -1| Get Player's Current Level if player info is loaded |
