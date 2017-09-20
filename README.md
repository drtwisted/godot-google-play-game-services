# Godot Google Play Services

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

## API reference
```python
"""            CALLBACK EVENTS
        
        NOTE: All data-changing events are invoked on calling *_immediate methods

        Google Play Games Services:
            _on_gpgs_connected
            _on_gpgs_suspended
            _on_gpgs_connection_failed(int error_code, String error_message)
        Leaderboards:
            _on_leaderboard_loaded
            _on_all_leaderboards_loaded
            _on_leaderboard_score_submitted(String leaderboard_id, int score, String status) [*_immediate]

        Achievements:
            _on_achievements_loaded
            _on_achievement_unlocked(String achievement_id, String status) [*_immediate]
            _on_achievement_revealed(String achievement_id, String status) [*_immediate]
            _on_achievement_incremented(String achievement_id, int increment_ammount, String status) [*_immediate]


"""

##############
# CONNECTION #
##############

"""
 Initialize GoogleApiClient
 @param int instance_id Godot app reference, can be obtained with GDScript's get_intance_ID()
 
 Usage:
      gpgs = Globals.get_singleton("GodotPlayGamesService")
      gpgs.init(get_instance_ID())
"""
init(instance_id) 

"""
 Sign in to Google (Play Games) Services
 
 Usage:
      gpgs.sign_in()
"""
sign_in() 


"""
 Sign out of Google (Play Games) Services
 (technically the same as disconnect)
 
 Usage:
      gpgs.sign_out()
"""
sign_out() 


"""
 Disconnect client of Google (Play Games) Services
 
 Usage:
      gpgs.disconnect()
"""
disconnect() 

"""
 Try to reconnect if connection is lost to Google (Play Games) Services
 
 Usage:
      gpgs.reconnect()
"""
reconnect() 


"""
 Get connection status
 
 Usage:
 
      if gpgs.is_signed_in():
          //Do stuff
 
"""
is_signed_in()

"""
 Callback on successful connection
"""
_on_gpgs_connected()

"""
 Callback on connection suspended
"""
_on_gpgs_suspended()

"""
 Callback on connection failed
 @param int error_code
 @param String error_message
"""
_on_gpgs_connection_failed(error_code, error_message)


################
# LEADERBOARDS #
################

"""
 Show the list of leaderboards for your game
 
 Usage:
      gpgs.leaderboard_show_all_leaderboards()
"""
leaderboard_show_all_leaderboards() 


"""
 Show leaderboard with specified id
 @param String id  leaderboard id obtained from your Developer Console
 
 Usage:
      var leaderboard_id = "exampleLeaderbord123ID"
      gpgs.leaderboard_show(leaderbaord_id)
"""
leaderboard_show(id) 


"""
 Show leaderboard with specific id and a custom time span
 @param String id leaderboard id obtained from your Developer Console
 @param int timeSpan 0 - DAILY, 1 - WEEKLY, 2 - ALL TIME
                 (com.google.android.gms.games.leaderboard.LeaderboardVariant.TIME_SPAN_*)
 
 Usage:
      enumDAILY, WEEKLY, ALL_TIME}
      var leaderboard_id = "exampleLeaderbord123ID"
 
      ...
 
      gpgs.leaderboard_show_with_time_span(leaderboard_id, WEEKLY)
"""
leaderboard_show_with_time_span(id, timeSpan) 


"""
 Submit a given score to the specified leaderboard
 @param String id leaderboard id obtained from your Developer Console
 @param int score score number the player earned
 
 Usage:
      var leaderboard_id = "exampleLeaderbord123ID"
 
      gpgs.leaderboard_submit_score(leaderboard_id, 9001)
"""
leaderboard_submit_score(id, score) 


"""
 Submit a given score to the specified leaderboard immediately
 with a callback result
 @param String id leaderboard id obtained from your Developer Console
 @param int score score number the player earned
 
 Usage:
      const ST_OK = "STATUS_OK"
      var leaderboard_id = "exampleLeaderbord123ID"
 
      ...
 
      gpgs.leaderboard_submit_score_immediate(leaderboard_id, 9001)
 
      ...
 
      _on_leaderboard_score_submitted(id, score, status):
          if status == ST_OK:
              //succeeded action
          else:
              //failed action
"""
leaderboard_submit_score_immediate(id, score) 

"""
 Callback on leaderboard loaded
"""
_on_leaderboard_loaded

"""
 Callback on all leaderboards list loaded
"""
_on_all_leaderboards_loaded

"""
 Callback on leaderboard_score_submitted_immediate
 @param String leaderboard_id
 @param int score
 @param String status returned status "STATUS_OK" in case of success
"""
_on_leaderboard_score_submitted(leaderboard_id, score, status)


###############
# ACHIEVEMNTS #
###############

"""
 Unlock an achievement with a given id
 
 @param String achievement_id achievement id obtained from your Developer Console
 
 Usage:
      var achievement_toasty = "achievemnt123ID"
      gpgs.achievement_unlock(achievement_toasty)
"""
achievement_unlock(achievement_id) 

"""
 Unlock an achievement with a given id immediately
 with a callback result
 
 @param String achievement_id achievement id obtained from your Developer Console
 
 Usage:
      const ST_OK = "STATUS_OK"
      var achievement_toasty = "achievemnt123ID"
 
      ...
 
      gpgs.achievement_unlock_immediate(achievement_toasty)
 
 
      ...
 
      _on_achievement_unlocked(id, status):
          if status == ST_OK:
              //succeeded action
          else:
              //failed action
"""
achievement_unlock_immediate(achievement_id) 


"""
 Reveal a hidden achievement with a given id
 
 @param String achievement_id achievement id obtained from your Developer Console
 
 Usage:
      var achievement_toasty = "achievemnt123ID"
 
      gpgs.achievement_reveal(achievement_toasty)
"""
achievement_reveal(achievement_id) 


"""
 Reveal a hidden achievement with a given id immediately
 with a callback result
 
 @param String achievement_id achievement id obtained from your Developer Console
 
 Usage:
      const ST_OK = "STATUS_OK"
      var achievement_toasty = "achievemnt123ID"
 
      ...
 
      gpgs.achievement_reveal_immediate(achievement_toasty)
 
      ....
 
      _on_achievement_revealed(id, status):
          if status == ST_OK:
              //succeeded action
          else:
              //failed action
"""
achievement_reveal_immediate(achievement_id) 


"""
 Increment an achievement with a given id by a given amount of points
 
 @param String achievement_id achievement id obtained from your Developer Console
 @param int increment_amount ammount of points to increment the achievement by
 
 Usage:
      var achievement_toasty = "achievemnt123ID"
 
      gpgs.achievement_increment(achievement_toasty, 5)
"""
achievement_increment(achievement_id, increment_amount)


"""
 Increment an achievement with a given id by a given amount of points immediately
 with a callback result
 
 @param String achievement_id achievement id obtained from your Developer Console
 @param int increment_amount ammount of points to increment the achievement by
 
 Usage:
      const ST_OK = "STATUS_OK"
      var achievement_toasty = "achievemnt123ID"
 
      ...
 
      gpgs.achievement_reveal_immediate(achievement_toasty)
 
      ....
 
      _on_achievement_revealed(id, increment_amount, status):
          if status == ST_OK:
              //succeeded action
          else:
              //failed action
"""
achievement_increment_immediate(achievement_id, increment_amount) 


"""
 Show achievements list
 
 Usage:
 
      gpgs.achievement_show_list()
"""
achievement_show_list() 

"""
 Callback on achievements list loaded
"""
_on_achievements_loaded

"""
 Callback on achievement unlocked (immediate)
 @param String achievement_id
 @param String status returned status "STATUS_OK" in case of success
"""
_on_achievement_unlocked(achievement_id, status)

"""
 Callback on achievement revealed (immediate)
 @param String achievement_id
 @param String status returned status "STATUS_OK" in case of success
"""
_on_achievement_revealed(achievement_id, status)

"""
 Callback on achievement incremented (immediate)
 @param String achievement_id
 @param int increment_ammount
 @param String status returned status "STATUS_OK" in case of success
"""
_on_achievement_incremented(achievement_id, increment_ammount, status)


###############
# PLAYER INFO #
###############

"""
 Update player info from Google Play Game Services
 
 Usage:
 
      gpgs.update_player_info()
"""
update_player_info() 


"""
 Get player ID if player info is loaded
 
 @return String user id or an empty string
 
 Usage:
 
      var player_id = gpgs.get_player_id()
"""
get_player_id() 


"""
 Get a player Display Name if player info is loaded
 
 @return String display name or an empty string
 
 Usage:
 
      var player_display_name = gpgs.get_player_display_name()
"""
get_player_display_name() 


"""
 Get player Title if player info is loaded
 
 @return String title or an empty string
 
 Usage:
 
      var player_title = gpgs.get_player_title()
"""
get_player_title() 

"""
 Get URI of Player's Icon Image if player info is loaded
 
 @return String URI of Player's Icon Image or an empty string
 
 Usage:
 
      var player_icon_image_uri = gpgs.get_player_icon_image_uri()
"""
get_player_icon_image_uri() 


"""
 Get Player's Current Level if player info is loaded
 
 @return int current level number or -1
 
 Usage:
 
      var player_level_number = gpgs.get_player_cureent_level_number()
"""
get_player_current_level_number() 

```
