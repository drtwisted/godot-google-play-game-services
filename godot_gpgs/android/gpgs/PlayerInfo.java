package org.godotengine.godot.gpgs;

import android.app.Activity;
import android.util.Log;

import com.google.android.gms.games.Games;
import com.google.android.gms.games.Player;


public class PlayerInfo extends GPGSComponent {

    private Player player;

    public PlayerInfo(Client client, Activity activity, String tag, String module) {
        super(client, activity, tag, module);
    }

    public void updatePlayerInfo() {
        Log.d(TAG, MODULE + ": Updating Player info...");
        final PlayerInfo thisReference = this;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                thisReference.setPlayer(
                        Games.Players.getCurrentPlayer(
                                thisReference.getClient().getGoogleApiClient()));
                Log.d(TAG, MODULE + ": > Updated Player info successfully!");
            }
        });
    }

    private void setPlayer(Player player) {
        this.player = player;
    }

    public boolean isPlayerInfoAvailable() {
        if (player == null) {
            Log.w(TAG, MODULE + ": "
                    + Thread.currentThread().getStackTrace()[4].getMethodName()
                    + " player info is not retrieved yet. Try again!");
            return false;
        }

        return true;
    }

    public String getPlayerID() {
        Log.d(TAG, MODULE + ": getPlayerID");
        
        if (isPlayerInfoAvailable()) {
            return player.getPlayerId();
        }

        return "";
    }

    public String getDisplayName() {
        Log.d(TAG, MODULE + ": getDisplayName");

        if (isPlayerInfoAvailable()) {
            return player.getDisplayName();
        }

        return "";
    }

    public String getTitle() {
        Log.d(TAG, MODULE + ": getTitle");

        if (isPlayerInfoAvailable()) {
            return player.getTitle();
        }

        return "";
    }

    public String getIconImageURI() {
        Log.d(TAG, MODULE + ": getIconImageURI");

        if (isPlayerInfoAvailable() && player.hasIconImage()) {
            return player.getIconImageUri().toString();
        }

        return "";
    }

    public int getPlayerCurrentLevelNumber() {
        Log.d(TAG, MODULE + ": getPlayerCurrentLevelNumber");

        if (isPlayerInfoAvailable()) {
            return player.getLevelInfo().getCurrentLevel().getLevelNumber();
        }

        return -1;
    }

}
