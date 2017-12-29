package org.godotengine.godot.gpgs;

import android.app.Activity;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.achievement.Achievements.UpdateAchievementResult;

public class Achievements extends GPGSComponent {
    public static final int REQUEST_ACHIEVEMENTS = 2002;


    public Achievements(Client client, Activity activity, String tag, String module, boolean debug) {
        super(client, activity, tag, module, debug);
    }

     public void showList() {
         getActivity().runOnUiThread(new Runnable() {
             @Override
             public void run() {
                 getActivity().startActivityForResult(
                         Games.Achievements.getAchievementsIntent(getClient().getGoogleApiClient()),
                         REQUEST_ACHIEVEMENTS);
                 debugLog("show achievements list");
             }
         });
     }

    public void unlock(final String achievement_id) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Games.Achievements.unlock(getClient().getGoogleApiClient(), achievement_id);
                debugLog("unlock achievement ID " + String.valueOf(achievement_id));
            }
        });
    }

    public void unlockImmediate(final String achievement_id,
                                final ResultCallback<UpdateAchievementResult> resultCallback) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Games.Achievements.unlockImmediate(
                        getClient().getGoogleApiClient(),
                        achievement_id).setResultCallback(resultCallback);
                debugLog("unlock achievement ID " + String.valueOf(achievement_id));
            }
        });
    }

    public void reveal(final String achievement_id) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Games.Achievements.reveal(getClient().getGoogleApiClient(), achievement_id);
                debugLog("reveal achievement ID " + String.valueOf(achievement_id));
            }
        });
    }

    public void revealImmediate(final String achievement_id,
                                final ResultCallback<UpdateAchievementResult> resultCallback) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Games.Achievements.revealImmediate(
                        getClient().getGoogleApiClient(),
                        achievement_id).setResultCallback(resultCallback);
                debugLog("reveal achievement ID " + String.valueOf(achievement_id));
            }
        });
    }

    public void increment(final String achievement_id, final int increment_amount) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Games.Achievements.increment(
                        getClient().getGoogleApiClient(), achievement_id, increment_amount);
                debugLog("increment achievement ID " + String.valueOf(achievement_id)
                                + " by " + String.valueOf(increment_amount));
            }
        });
    }

    public void incrementImmediate(final String achievement_id, final int increment_amount,
                                   final ResultCallback<UpdateAchievementResult> resultCallback) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Games.Achievements.incrementImmediate(
                        getClient().getGoogleApiClient(),
                        achievement_id, increment_amount).setResultCallback(resultCallback);
                debugLog("increment achievement ID " + String.valueOf(achievement_id)
                                + " by " + String.valueOf(increment_amount));
            }
        });
    }

}
