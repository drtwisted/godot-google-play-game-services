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
import org.godotengine.godot.gpgs.tools.PlayerInfoUpdateCallback;

import java.util.ArrayList;

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

    private boolean clientInitialized = false;

    private static GodotPlayGameServices singletonReference;

    private boolean debug;
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

    private PlayerInfoUpdateCallback getPlayerInfoUpdateCallback() {
        return new PlayerInfoUpdateCallback() {
            @Override
            public void run() {
                GodotLib.calldeferred(
                    instance_id, "_on_player_info_updated", new Object[] { });
            }
        };
    }
    /* - GETTERS */

    static public Godot.SingletonBase initialize(Activity p_activity) {
        return (Godot.SingletonBase) new GodotPlayGameServices(p_activity);
    }


    public GodotPlayGameServices(Activity activity) {
       this.activity = activity;

       registerClass(MODULE, new String[] {
                "set_debug",
                "init", "init_with_debug",
                "sign_in", "sign_out", "reconnect", "disconnect",
                "is_signing_in", "is_signed_in", "is_connection_suspended",

                "leaderboard_submit_score", "leaderboard_submit_score_immediate",
                "leaderboard_show_all_leaderboards",
                "leaderboard_show", "leaderboard_show_with_time_span",

                "achievement_unlock", "achievement_unlock_immediate",
                "achievement_reveal", "achievement_reveal_immediate",
                "achievement_increment", "achievement_increment_immediate",
                "achievement_show_list",
           
                "is_player_info_available",
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
            _on_leaderboard_closed
            _on_all_leaderboards_loaded
            _on_all_leaderboards_closed
            _on_leaderboard_score_submitted(leaderboard_id, status) [*_immediate]

        Achievements:
            _on_achievements_loaded
            _on_achievements_closed
            _on_achievement_unlocked(achievement_id, status) [*_immediate]
            _on_achievement_revealed(achievement_id, status) [*_immediate]
            _on_achievement_incremented(achievement_id, increment_ammount, status) [*_immediate]

        Player Info:
            _on_player_info_updated
                                                                                                */
    }

    protected void onMainDestroy() {
        disconnect();
    }

    private boolean isConnectedLogged() {
        if (!client.isConnected()) {
            GodotLib.calldeferred(instance_id, "_on_gpgs_not_connected", new Object[] { });

            if (debug) {
                Log.w(TAG,
                        MODULE + ": not signed in when calling " + Thread.currentThread().getStackTrace()[4]);
            }

        }

        return client.isConnected();

    }

    private String getCurrentMethodName() {
        return Thread.currentThread().getStackTrace()[3].getMethodName();
    }

    private boolean isClientInitializedLogged() {
        if (!clientInitialized) {
            GodotLib.calldeferred(instance_id, "_on_gpgs_not_signed_in", new Object[] { });

            if (debug) Log.d(TAG, MODULE + ": client is not initialized!");
        }

        return clientInitialized;
    }

    private void logMethod() {
        if (!debug) return;
        
        String self_name = Thread.currentThread().getStackTrace()[4].getMethodName();
        Log.i(TAG, MODULE + ": " + self_name);
    }

    private void debugLog(String message) {
        if (debug) Log.d(TAG, MODULE + ": " + message);
    }

    private String extractStatusNameFromStatus(String status) {
        return status.split(",", 2)[0].split("=", 2)[1];
    }

    private void initClient() {
        if (client != null) return;
        
        client = new Client(this, activity, getOnClientConnectedCallbackList(), TAG, MODULE, debug);
        client.init();
        player = new PlayerInfo(client, activity, TAG, MODULE, debug);
        leaderboards = new org.godotengine.godot.gpgs.Leaderboards(
                client, activity, TAG, MODULE, debug);
        achievements = new org.godotengine.godot.gpgs.Achievements(
                client, activity, TAG, MODULE, debug);

        clientInitialized = true;
    }

    private void onGpgsResultCallback(
        int result, String callbackOkName, String callbackCanceledName) {
        switch (result) {
            case Activity.RESULT_OK:
                GodotLib.calldeferred(
                    instance_id, callbackOkName, new Object[] { });
                break;

            case Activity.RESULT_CANCELED:
                GodotLib.calldeferred(
                    instance_id, callbackCanceledName, new Object[] { });
                break;
            default:
            break;
        }        
    }

    private void _init(int instance_id, boolean debug) {
        this.instance_id = instance_id;
        this.debug = debug;
        
        logMethod();
    }
    
    /******************************************/
    /********** v EXPORTED METHODS v **********/

    /* GENERAL */

    /**
     * Change debug logging state during runtime
     * @param state new debug state
     *
     * Usage:
     *      gpgs.set_debug(true) # Enable
     *
     *      # Do stuff with gpgs
     * 
     *      gpgs.set_debug(false)
     */
    public void set_debug(boolean state) {
        logMethod();

        if (debug == state) return;

        debug = state;

        client.setDebug(debug);
        player.setDebug(debug);
        leaderboards.setDebug(debug);
        achievements.setDebug(debug);
    }

    /*CONNECTION */

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
        this.debug = false;
        
        logMethod();
    }

    /**
     * Initialize GoogleApiClient with debug logging enabled
     * This includes method calls and some sensitive data, such as items IDs
     * 
     * @param instance_id Godot app reference, can be obtained with GDScript's get_intance_ID()
     *
     * Usage:
     *      gpgs = Globals.get_singleton("GodotPlayGamesService")
     *      gpgs.init_with_debug(get_instance_ID())
     */
    public void init_with_debug(int instance_id) {
        this.instance_id = instance_id;
        this.debug = true;
        
        logMethod();
    }

    /**
     * Sign in to Google (Play Games) Services
     *
     * Usage:
     *      gpgs.sign_in()
     */
    public void sign_in() {
        logMethod();

        initClient();

        if (!isClientInitializedLogged()) return;

        client.connect();

        //Log.i(TAG, MODULE + ": signing in to Google Play Game Services...");
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

        if (!isClientInitializedLogged() || !isConnectedLogged()) return;

        disconnect();

        //Log.i(TAG, MODULE + ": signing out of Google Play Game Services...");
    }

    /**
     * Disconnect client of Google (Play Games) Services
     *
     * Usage:
     *      gpgs.disconnect()
     */
    public void disconnect() {
        logMethod();

        if (!isClientInitializedLogged() || !isConnectedLogged()) return;

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

        if (!isClientInitializedLogged()) return;

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
        
        return clientInitialized && client.isConnected();
    }

    /**
     * Get connection status
     *
     * Usage:
     *
     *      if gpgs.is_signing_in():
     *          //Do stuff
     *
     */
    public boolean is_signing_in() {
        logMethod();
        
        return clientInitialized && client.isConnecting();
    }

    /**
     * Get connection status
     *
     * Usage:
     *
     *      if gpgs.is_connection_suspended():
     *          //Do stuff
     *
     */
    public boolean is_connection_suspended() {
        logMethod();

        return clientInitialized && client.isConnectionSuspeneded();
    }

    /* LEADERBOARD */

    /**
     * Show the list of leaderboards for your game
     *
     * Usage:
     *      gpgs.leaderboard_show_all_leaderboards()
     */
    public void leaderboard_show_all_leaderboards() {
        logMethod();

        if (!isClientInitializedLogged() || !isConnectedLogged()) return;

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
        logMethod();

        if (!isClientInitializedLogged() || !isConnectedLogged()) return;

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
        logMethod();

        if (!isClientInitializedLogged() || !isConnectedLogged()) return;

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
        logMethod();
        
        if (!isClientInitializedLogged() || !isConnectedLogged()) return;

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
        logMethod();

        if (!isClientInitializedLogged() || !isConnectedLogged()) return;

        final boolean debug = this.debug;
        
        leaderboards.submitScoreImmediate(
                id, score, new ResultCallback<Leaderboards.SubmitScoreResult>() {
                    @Override
                    public void onResult(@NonNull Leaderboards.SubmitScoreResult submitScoreResult) {
                        String status = extractStatusNameFromStatus(
                                submitScoreResult.getStatus().toString());

                        if (debug) Log.d(TAG, MODULE
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
        logMethod();

        if (!isClientInitializedLogged() || !isConnectedLogged()) return;

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
        logMethod();

        if (!isClientInitializedLogged() || !isConnectedLogged()) return;

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
        logMethod();

        if (!isClientInitializedLogged() || !isConnectedLogged()) return;

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
        logMethod();

        if (!isClientInitializedLogged() || !isConnectedLogged()) return;

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
        logMethod();

        if (!isClientInitializedLogged() || !isConnectedLogged()) return;

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
        logMethod();

        if (!isClientInitializedLogged() || !isConnectedLogged()) return;

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
        logMethod();

        if (!isClientInitializedLogged() || !isConnectedLogged()) return;

        achievements.showList();
    }

    /* PLAYER INFO */

    /**
     * Check if player info from Google Play Game Services is available
     *
     * Usage:
     *
     *      gpgs.is_player_info_available()
     */
    public boolean is_player_info_available() {
        logMethod();

        if (!isClientInitializedLogged() || !isConnectedLogged()) return false;

        return player.isPlayerInfoAvailable();       
    }
    /**
     * Update player info from Google Play Game Services
     *
     * Usage:
     *
     *      gpgs.update_player_info()
     */
    public void update_player_info() {
        logMethod();

        if (!isClientInitializedLogged() || !isConnectedLogged()) return;

        player.updatePlayerInfo(getPlayerInfoUpdateCallback());
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

        if (!isClientInitializedLogged() || !isConnectedLogged()) return "";

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

        if (!isClientInitializedLogged() || !isConnectedLogged()) return "";

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

        if (!isClientInitializedLogged() || !isConnectedLogged()) return "";

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

        if (!isClientInitializedLogged() || !isConnectedLogged()) return "";

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

        if (!isClientInitializedLogged() || !isConnectedLogged()) return -1;

        return player.getPlayerCurrentLevelNumber();
    }

    /********* ^ EXPORTED METHODS ^ **********/
    /*****************************************/


    /*********************************************/
    /*** v MAIN ACTIVITY LIFECYCLE CALLBACKS v ***/

    public void onMainActivityResult(int requestCode, int resultCode, Intent data) {
//   protected void onMainActivityResult(int requestCode, int resultCode, Intent data) {

        switch(requestCode) {
            case RC_SIGN_IN:
                if(!client.getGoogleApiClient().isConnecting()) {
                    client.connect();
                }
            break;

            case REQUEST_ALL_LEADERBOARDS:
                onGpgsResultCallback(
                    resultCode,
                    "_on_all_leaderboards_loaded",
                    "_on_all_leaderboards_closed");
            break;

            case REQUEST_LEADERBOARD:
                onGpgsResultCallback(
                    resultCode,
                    "_on_leaderboard_loaded",
                    "_on_leaderboard_closed");
            break;

            case REQUEST_ACHIEVEMENTS:
                onGpgsResultCallback(
                    resultCode,
                    "_on_achievements_loaded",
                    "_on_achievements_closed");
            break;
            default:
            break;
        }
    }

    protected void onMainActivityResume() {
        // debugLog("ON RESUME");

        if (is_signing_in()) return;

        reconnect();
    }

    protected void onMainActivityPause() {
        // debugLog("ON PAUSE");

        if (is_signing_in()) return;

        disconnect();
    }
    /*** ^ MAIN ACTIVITY LIFECYCLE CALLBACKS ^ ***/
    /*********************************************/

    /**
     * Called when client is connected
     * By default only updates player info
     */
    private ArrayList<Client.OnConnectedCallback> getOnClientConnectedCallbackList() {
        ArrayList<Client.OnConnectedCallback> list = new ArrayList<>();
        
        list.add(new Client.OnConnectedCallback() {
            boolean runOnce = true;
            @Override
            public void run() {
                player.updatePlayerInfo(getPlayerInfoUpdateCallback());
            }
        });
        
        /***
        
            Add any custom callbacks as follows
        
        list.add(new Client.OnConnectedCallback() {
            @Override
            public void run() {
                // Your code goes here
            }
        });
        
        ***/
        
        return list;
    }
}
