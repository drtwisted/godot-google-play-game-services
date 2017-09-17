package org.godotengine.godot.gpgs;


import android.app.Activity;
import android.util.Log;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.leaderboard.Leaderboards.SubmitScoreResult;

public class Leaderboards extends GPGSComponent {

    public static final int REQUEST_ALL_LEADERBOARDS = 1002;
    public static final int REQUEST_LEADERBOARD = 1002;

    public static final int TIME_SPAN_DAILY = 0;
    public static final int TIME_SPAN_WEEKLY = 1;
    public static final int TIME_SPAN_ALL_TIME = 2;

    public Leaderboards(Client client, Activity activity, String tag, String module) {
        super(client, activity, tag, module);
    }

    public void showAllLeaderboards() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getActivity().startActivityForResult(
                        Games.Leaderboards.getAllLeaderboardsIntent(
                                getClient().getGoogleApiClient()),
                        REQUEST_ALL_LEADERBOARDS);
                Log.d(TAG, MODULE + ": requesting all leaderboards");
            }
        });
    }

    public void show(final String leaderboard_id) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getActivity().startActivityForResult(
                        Games.Leaderboards.getLeaderboardIntent(
                                getClient().getGoogleApiClient(), leaderboard_id),
                        REQUEST_LEADERBOARD);
                Log.d(TAG, MODULE + ": requesting leaderboard ID " + leaderboard_id);
            }
        });
    }

    public void showWithTimeSpan(final String leaderboard_id, final int timeSpan) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getActivity().startActivityForResult(
                        Games.Leaderboards.getLeaderboardIntent(
                                getClient().getGoogleApiClient(), leaderboard_id, timeSpan),
                        REQUEST_LEADERBOARD);
                Log.d(TAG, MODULE + ": requesting leaderboard ID " + leaderboard_id + " with timeSpan=" + timeSpan);
            }
        });
    }

    public void submitScore(final String leaderboard_id, final int score) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Games.Leaderboards.submitScore(
                        getClient().getGoogleApiClient(), leaderboard_id, score);
                Log.d(TAG, MODULE + ": submitting score value " + String.valueOf(score) + " for leaderboard ID " + leaderboard_id);
            }
        });
    }

    public void submitScoreImmediate(final String leaderboard_id, final int score,
                                     final ResultCallback<SubmitScoreResult> resultCallback) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Games.Leaderboards.submitScoreImmediate(
                        getClient().getGoogleApiClient(),
                        leaderboard_id, score).setResultCallback(resultCallback);
                Log.d(TAG, MODULE + ": submitting score value " + String.valueOf(score) + " for leaderboard ID " + leaderboard_id);
            }
        });
    }
}
