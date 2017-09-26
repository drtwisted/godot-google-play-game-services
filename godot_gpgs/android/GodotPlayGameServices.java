package org.godotengine.godot;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.app.Activity;
import android.content.Intent;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.achievement.Achievements;
import com.google.android.gms.games.leaderboard.Leaderboards;

import org.godotengine.godot.gpgs.Client;
import org.godotengine.godot.gpgs.PlayerInfo;

import static org.godotengine.godot.gpgs.Achievements.REQUEST_ACHIEVEMENTS;
import static org.godotengine.godot.gpgs.Leaderboards.REQUEST_ALL_LEADERBOARDS;
import static org.godotengine.godot.gpgs.Leaderboards.REQUEST_LEADERBOARD;

public class GodotPlayGameServices extends Godot.SingletonBase {

    private static final String TAG = "godot";
    private static final String MODULE = "GodotPlayGameServices";

    private static final int RC_SIGN_IN = 9001;
//    private static final int REQUEST_ACHIEVEMENTS = 9002;
    //private static final int REQUEST_ACHIEVEMENTS = 2002;
    //private static final int REQUEST_LEADERBOARD = 1002;
    //private static final int REQUEST_ALL_LEADERBOARDS = 1005;

    private static GodotPlayGameServices singletonReference;

    private int instance_id;
    private Activity activity; 

    /* Components */
    private Client client;
    private org.godotengine.godot.gpgs.PlayerInfo player;
    private org.godotengine.godot.gpgs.Leaderboards leaderboards;
    private org.godotengine.godot.gpgs.Achievements achievements;

    /* + GETTERS */
    public int getInstanceID() {
        return instance_id;
    }
    /* - GETTERS */

    static public Godot.SingletonBase initialize(Activity p_activity) {
        return (Godot.SingletonBase) new GodotPlayGameServices(p_activity);
    }


    public GodotPlayGameServices(Activity activity) {
       this.activity = activity;

       registerClass(MODULE, new String[] {
               "init",
               "sign_in", "sign_out", "reconnect", "disconnect", "is_signed_in",

               "leaderboard_submit_score", "leaderboard_submit_score_immediate",
               "leaderboard_show_all_leaderboards",
               "leaderboard_show", "leaderboard_show_with_time_span",

               "achievement_unlock", "achievement_unlock_immediate",
               "achievement_reveal", "achievement_reveal_immediate",
               "achievement_increment", "achievement_increment_immediate",
               "achievement_show_list",
               "get_player_id", "get_player_display_name",
               "get_player_title", "get_player_current_level_number",
               "get_player_icon_image_uri"});
       
       /*             CALLBACK EVENTS
        
        NOTE: All data-changing events are invoked on calling *_immediate methods

        Google Play Games Services:
            _on_gpgs_connected
            _on_gpgs_suspended
            _on_gpgs_connection_failed(error_code, error_message)
        Leaderboards:
            _on_leaderboard_loaded
            _on_all_leaderboards_loaded
            _on_leaderboard_score_submitted(leaderboard_id, status) [*_immediate]

        Achievements:
            _on_achievements_loaded
            _on_achievement_unlocked(achievement_id, status) [*_immediate]
            _on_achievement_revealed(achievement_id, status) [*_immediate]
            _on_achievement_incremented(achievement_id, increment_ammount, status) [*_immediate]

                                                                                                */
    }

    protected void onMainDestroy() {
        disconnect();
    }

    private boolean isConnectedLogged(@Nullable String methodName) {
        if (!client.isConnected()) Log.w(TAG, MODULE + ": not signed in when calling " + methodName);

        return client.isConnected();
    }

    private String getCurrentMethodName() {
        return Thread.currentThread().getStackTrace()[3].getMethodName();
    }

    private void logMethod() {
        String self_name = Thread.currentThread().getStackTrace()[4].getMethodName();
        Log.i(TAG, MODULE + ": " + self_name);
    }


    private String extractStatusNameFromStatus(String status) {
        return status.split(",", 2)[0].split("=", 2)[1];
    }

    /******************************************/
    /********** v EXPORTED METHODS v **********/

    /* GENERAL/CONNECTION */

    /**
     * Initialize GoogleApiClient
     * @param instance_id Godot app reference, can be obtained with GDScript's get_intance_ID()
     *
     * Usage:
     *      gpgs = Globals.get_singleton("GodotPlayGamesService")
     *      gpgs.init(get_instance_ID())
     */
    public void init(int instance_id) {
        this.instance_id = instance_id;
        String self_name = getCurrentMethodName();
        Log.i(TAG, MODULE + ": " + self_name);

        client = new Client(this, activity, TAG, MODULE);
        client.init();
        player = new PlayerInfo(client, activity, TAG, MODULE);
        leaderboards = new org.godotengine.godot.gpgs.Leaderboards(
                client, activity, TAG, MODULE);
        achievements = new org.godotengine.godot.gpgs.Achievements(
                client, activity, TAG, MODULE);
    }

    /**
     * Sign in to Google (Play Games) Services
     *
     * Usage:
     *      gpgs.sign_in()
     */
    public void sign_in() {
        logMethod();

        client.connect();

        Log.i(TAG, MODULE + ": signing in to Google Play Game Services...");
    }

    /**
     * Sign out of Google (Play Games) Services
     * (technically the same as disconnect)
     *
     * Usage:
     *      gpgs.sign_out()
     */
    public void sign_out() {
        logMethod();

        disconnect();

        Log.i(TAG, MODULE + ": signing out of Google Play Game Services...");
    }

    /**
     * Disconnect client of Google (Play Games) Services
     *
     * Usage:
     *      gpgs.disconnect()
     */
    public void disconnect() {
        logMethod();

        client.disconnect();
    }

    /**
     * Try to reconnect if connection is lost to Google (Play Games) Services
     *
     * Usage:
     *      gpgs.reconnect()
     */
    public void reconnect() {
        logMethod();

        client.reconnect();
    }

    /**
     * Get connection status
     *
     * Usage:
     *
     *      if gpgs.is_signed_in():
     *          //Do stuff
     *
     */
    public boolean is_signed_in() {
        logMethod();
        
        return client.isConnected();
    }

    /* LEADERBOARD */

    /**
     * Show the list of leaderboards for your game
     *
     * Usage:
     *      gpgs.leaderboard_show_all_leaderboards()
     */
    public void leaderboard_show_all_leaderboards() {
        String self_name = getCurrentMethodName();
        Log.i(TAG, MODULE + ": " + self_name);

        if (!isConnectedLogged(self_name)) return;

        leaderboards.showAllLeaderboards();
    }


    /**
     * Show leaderboard with specified id
     * @param id leaderboard id obtained from your Developer Console
     *
     * Usage:
     *      var leaderboard_id = "exampleLeaderbord123ID"
     *      gpgs.leaderboard_show(leaderbaord_id)
     */
    public void leaderboard_show(String id) {
        String self_name = getCurrentMethodName();
        Log.i(TAG, MODULE + ": " + self_name);

        if (!isConnectedLogged(self_name)) return;

        leaderboards.show(id);
    }

    /**
     * Show leaderboard with specific id and a custom time span
     * @param id leaderboard id obtained from your Developer Console
     * @param timeSpan 0 - DAILY, 1 - WEEKLY, 2 - ALL TIME
     *                 (com.google.android.gms.games.leaderboard.LeaderboardVariant.TIME_SPAN_*)
     *
     * Usage:
     *      enum {DAILY, WEEKLY, ALL_TIME}
     *      var leaderboard_id = "exampleLeaderbord123ID"
     *
     *      ...
     *
     *      gpgs.leaderboard_show_with_time_span(leaderboard_id, WEEKLY)
     */
    public void leaderboard_show_with_time_span(String id, int timeSpan) {
        String self_name = getCurrentMethodName();
        Log.i(TAG, MODULE + ": " + self_name);

        if (!isConnectedLogged(self_name)) return;

        leaderboards.showWithTimeSpan(id, timeSpan);
    }

    /**
     * Submit a given score to the specified leaderboard
     * @param id leaderboard id obtained from your Developer Console
     * @param score score number the player earned
     *
     * Usage:
     *      var leaderboard_id = "exampleLeaderbord123ID"
     *
     *      gpgs.leaderboard_submit_score(leaderboard_id, 9001)
     */
    public void leaderboard_submit_score(String id, int score) {
        String self_name = getCurrentMethodName();
        Log.i(TAG, MODULE + ": " + self_name);
        
        if (!isConnectedLogged(self_name)) return;

        leaderboards.submitScore(id, score);
    }

    /**
     * Submit a given score to the specified leaderboard immediately
     * with a callback result
     * @param id leaderboard id obtained from your Developer Console
     * @param score score number the player earned
     *
     * Usage:
     *      const ST_OK = "STATUS_OK"
     *      var leaderboard_id = "exampleLeaderbord123ID"
     *
     *      ...
     *
     *      gpgs.leaderboard_submit_score_immediate(leaderboard_id, 9001)
     *
     *      ...
     *
     *      func _on_leaderboard_score_submitted(id, score, status):
     *          if status == ST_OK:
     *              //succeeded action
     *          else:
     *              //failed action
     */
    public void leaderboard_submit_score_immediate(final String id, final int score) {
        String self_name = getCurrentMethodName();
        Log.i(TAG, MODULE + ": " + self_name);

        if (!isConnectedLogged(self_name)) return;

        leaderboards.submitScoreImmediate(
                id, score, new ResultCallback<Leaderboards.SubmitScoreResult>() {
                    @Override
                    public void onResult(@NonNull Leaderboards.SubmitScoreResult submitScoreResult) {
                        String status = extractStatusNameFromStatus(
                                submitScoreResult.getStatus().toString());

                        Log.d(TAG, MODULE
                                + ": > tried to submit score value " + String.valueOf(score)
                                + " with " + status);

                               GodotLib.calldeferred(
                                       instance_id, "_on_leaderboard_score_submitted",
                                       new Object[] { id, score, status });
                    }
                });
    }


    /* ACHIEVEMNTS */

    /**
     * Unlock an achievement with a given id
     *
     * @param achievement_id achievement id obtained from your Developer Console
     *
     * Usage:
     *      var achievement_toasty = "achievemnt123ID"
     *      gpgs.achievement_unlock(achievement_toasty)
     */
    public void achievement_unlock(String achievement_id) {
        String self_name = getCurrentMethodName();
        Log.i(TAG, MODULE + ": " + self_name);

        if(!isConnectedLogged(self_name)) return;

        achievements.unlock(achievement_id);
    }

    /**
     * Unlock an achievement with a given id immediately
     * with a callback result
     *
     * @param achievement_id achievement id obtained from your Developer Console
     *
     * Usage:
     *      const ST_OK = "STATUS_OK"
     *      var achievement_toasty = "achievemnt123ID"
     *
     *      ...
     *
     *      gpgs.achievement_unlock_immediate(achievement_toasty)
     *
     *
     *      ...
     *
     *      func _on_achievement_unlocked(id, status):
     *          if status == ST_OK:
     *              //succeeded action
     *          else:
     *              //failed action
     */
    public void achievement_unlock_immediate(final String achievement_id) {
        String self_name = getCurrentMethodName();
        Log.i(TAG, MODULE + ": " + self_name);

        if(!isConnectedLogged(self_name)) return;

        achievements.unlockImmediate(
                achievement_id,
                new ResultCallback<Achievements.UpdateAchievementResult>() {
                    @Override
                    public void onResult(@NonNull Achievements.UpdateAchievementResult updateAchievementResult) {
                        String status = extractStatusNameFromStatus(
                                updateAchievementResult.getStatus().toString());

                        Log.d(TAG, MODULE
                                + ": > tried to unlock achievement " + achievement_id
                                + " with " + status);

                               GodotLib.calldeferred(
                                       instance_id, "_on_achievement_unlocked",
                                       new Object[] { achievement_id, status });

                    }
                });
    }

    /**
     * Reveal a hidden achievement with a given id
     *
     * @param achievement_id achievement id obtained from your Developer Console
     *
     * Usage:
     *      var achievement_toasty = "achievemnt123ID"
     *
     *      gpgs.achievement_reveal(achievement_toasty)
     */
    public void achievement_reveal(final String achievement_id) {
        String self_name = getCurrentMethodName();
        Log.i(TAG, MODULE + ": " + self_name);

        if (!isConnectedLogged(self_name)) return;

        achievements.reveal(achievement_id);
    }

    /**
     * Reveal a hidden achievement with a given id immediately
     * with a callback result
     *
     * @param achievement_id achievement id obtained from your Developer Console
     *
     * Usage:
     *      const ST_OK = "STATUS_OK"
     *      var achievement_toasty = "achievemnt123ID"
     *
     *      ...
     *
     *      gpgs.achievement_reveal_immediate(achievement_toasty)
     *
     *      ....
     *
     *      func _on_achievement_revealed(id, status):
     *          if status == ST_OK:
     *              //succeeded action
     *          else:
     *              //failed action
     */
    public void achievement_reveal_immediate(final String achievement_id) {
        String self_name = getCurrentMethodName();
        Log.i(TAG, MODULE + ": " + self_name);

        if (!isConnectedLogged(self_name)) return;

        achievements.revealImmediate(
                achievement_id,
                new ResultCallback<Achievements.UpdateAchievementResult>() {
                    @Override
                    public void onResult(@NonNull Achievements.UpdateAchievementResult updateAchievementResult) {
                        String status = extractStatusNameFromStatus(
                                updateAchievementResult.getStatus().toString());

                        Log.d(TAG, MODULE
                                + ": > tried to reveal achievement " + achievement_id
                                + " with " + status);

                               GodotLib.calldeferred(
                                       instance_id, "_on_achievement_revealed",
                                       new Object[] { achievement_id, status });
                    }
                });
    }

    /**
     * Increment an achievement with a given id by a given amount of points
     *
     * @param achievement_id achievement id obtained from your Developer Console
     * @param increment_amount ammount of points to increment the achievement by
     *
     * Usage:
     *      var achievement_toasty = "achievemnt123ID"
     *
     *      gpgs.achievement_increment(achievement_toasty, 5)
     */
    public void achievement_increment(String achievement_id, int increment_amount) {
        String self_name = getCurrentMethodName();
        Log.i(TAG, MODULE + ": " + self_name);

        if(!isConnectedLogged(self_name)) return;

        achievements.increment(achievement_id, increment_amount);
    }

    /**
     * Increment an achievement with a given id by a given amount of points immediately
     * with a callback result
     *
     * @param achievement_id achievement id obtained from your Developer Console
     * @param increment_amount ammount of points to increment the achievement by
     *
     * Usage:
     *      const ST_OK = "STATUS_OK"
     *      var achievement_toasty = "achievemnt123ID"
     *
     *      ...
     *
     *      gpgs.achievement_reveal_immediate(achievement_toasty)
     *
     *      ....
     *
     *      func _on_achievement_revealed(id, increment_amount, status):
     *          if status == ST_OK:
     *              //succeeded action
     *          else:
     *              //failed action
     */
    public void achievement_increment_immediate(final String achievement_id, final int increment_amount) {
        String self_name = getCurrentMethodName();
        Log.i(TAG, MODULE + ": " + self_name);

        if(!isConnectedLogged(self_name)) return;

        achievements.incrementImmediate(
                achievement_id, increment_amount,
                new ResultCallback<Achievements.UpdateAchievementResult>() {
                    @Override
                    public void onResult(@NonNull Achievements.UpdateAchievementResult updateAchievementResult) {
                        String status = extractStatusNameFromStatus(
                                updateAchievementResult.getStatus().toString());

                        Log.d(TAG, MODULE
                                + ": > tried to increment achievement " + achievement_id
                                +" by " + String.valueOf(increment_amount)
                                + " with " + status);

                               GodotLib.calldeferred(
                                       instance_id, "_on_achievement_incremented",
                                       new Object[] { achievement_id, increment_amount, status });

                    }
                });
    }

    /**
     * Show achievements list
     *
     * Usage:
     *
     *      gpgs.achievement_show_list()
     */
    public void achievement_show_list() {
        String self_name = getCurrentMethodName();
        Log.i(TAG, MODULE + ": " + self_name);

        if(!isConnectedLogged(self_name)) return;

        achievements.showList();
    }

    /* PLAYER INFO */

    /**
     * Update player info from Google Play Game Services
     *
     * Usage:
     *
     *      gpgs.update_player_info()
     */
    public void update_player_info() {
        logMethod();

        player.updatePlayerInfo();
    }

    /**
     * Get player ID if player info is loaded
     *
     * @return String user id or an empty string
     *
     * Usage:
     *
     *      var player_id = gpgs.get_player_id()
     */
    public String get_player_id() {
        logMethod();

        return player.getPlayerID();
    }

    /**
     * Get a player Display Name if player info is loaded
     *
     * @return String display name or an empty string
     *
     * Usage:
     *
     *      var player_display_name = gpgs.get_player_display_name()
     */
    public String get_player_display_name() {
        logMethod();

        return player.getDisplayName();
    }

    /**
     * Get player Title if player info is loaded
     *
     * @return String title or an empty string
     *
     * Usage:
     *
     *      var player_title = gpgs.get_player_title()
     */
    public String get_player_title() {
        logMethod();

        return player.getTitle();
    }

    /**
     * Get URI of Player's Icon Image if player info is loaded
     *
     * @return String URI of Player's Icon Image or an empty string
     *
     * Usage:
     *
     *      var player_icon_image_uri = gpgs.get_player_icon_image_uri()
     */
    public String get_player_icon_image_uri() {
        logMethod();

        return player.getIconImageURI();
    }

    /**
     * Get Player's Current Level if player info is loaded
     *
     * @return int current level number or -1
     *
     * Usage:
     *
     *      var player_level_number = gpgs.get_player_cureent_level_number()
     */
    public int get_player_current_level_number() {
        logMethod();

        return player.getPlayerCurrentLevelNumber();
    }

    /********* ^ EXPORTED METHODS ^ **********/
    /*****************************************/

    // public void onMainActivityResult(int requestCode, int resultCode, Intent data) {
   protected void onMainActivityResult(int requestCode, int resultCode, Intent data) {

        switch(requestCode) {
            case RC_SIGN_IN:
                if(!client.getGoogleApiClient().isConnecting()) {
                    client.connect();
                }
            break;

            case REQUEST_ALL_LEADERBOARDS:
                if (resultCode == Activity.RESULT_OK) {
                    GodotLib.calldeferred(instance_id, "_on_all_leaderboards_loaded", new Object[] { });
                }
            break;

            case REQUEST_LEADERBOARD:
                if (resultCode == Activity.RESULT_OK) {
                    GodotLib.calldeferred(instance_id, "_on_leaderboard_loaded", new Object[] { });
                }
            break;

            case REQUEST_ACHIEVEMENTS:
                if (resultCode == Activity.RESULT_OK) {
                    GodotLib.calldeferred(instance_id, "_on_achievements_loaded", new Object[] { });
                }
            break;
            default:
            break;
        }
    }

    /**
     * Called when client is connected
     * By default only updates player info
     */
    public void onConnected() {
        player.updatePlayerInfo();
    }
}
