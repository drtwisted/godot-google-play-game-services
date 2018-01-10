package org.godotengine.godot.gpgs;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.common.images.ImageManager;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.Player;


public class PlayerInfo extends GPGSComponent {

    private Player player;

    public PlayerInfo(
        Client client, Activity activity, String tag, String module, boolean debug) {
        super(client, activity, tag, module, debug);
    }

    public void updatePlayerInfo() {
        debugLog("Updating Player info...");
        final PlayerInfo thisReference = this;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                thisReference.setPlayer(
                        Games.Players.getCurrentPlayer(
                                thisReference.getClient().getGoogleApiClient()));
                debugLog("> Updated Player info successfully!");
            }
        });
    }

    private void setPlayer(Player player) {
        this.player = player;
    }

    public boolean isPlayerInfoAvailable() {
        if (player == null) {
            if (debug) Log.w(TAG, MODULE + ": "
                    + Thread.currentThread().getStackTrace()[4].getMethodName()
                    + " player info is not retrieved yet. Try again!");
            return false;
        }

        return true;
    }

    public String getPlayerID() {
        debugLog("getPlayerID");
        
        if (isPlayerInfoAvailable()) {
            return player.getPlayerId();
        }

        return "";
    }

    public String getDisplayName() {
        debugLog("getDisplayName");

        if (isPlayerInfoAvailable()) {
            return player.getDisplayName();
        }

        return "";
    }

    public String getTitle() {
        debugLog("getTitle");

        if (isPlayerInfoAvailable()) {
            return player.getTitle();
        }

        return "";
    }

    public String getIconImageURI() {
        debugLog("getIconImageURI");

        if (isPlayerInfoAvailable() && player.hasIconImage()) {

            return player.getIconImageUri().toString();
        }

        return "";
    }

    public void loadIconImage() {
        debugLog("loadIconImage");
        if (isPlayerInfoAvailable() && player.hasIconImage()) {
            final Uri requested_uri = player.getIconImageUri();

            ImageManager.create(getActivity()).loadImage(new ImageManager.OnImageLoadedListener() {
                @Override
                public void onImageLoaded(Uri uri, Drawable drawable, boolean b) {
                    if (uri == requested_uri) {
//                        GodotLib.calldeferred(parent.getInstanceID(), "_on_icon_image_loaded", new Object[] {
//                                requested_uri, ((BitmapDrawable) drawable).getBitmap()});
                        Log.d(TAG, "");
                    }
                }
            }, requested_uri);
        }
    }

    public int getPlayerCurrentLevelNumber() {
        debugLog("getPlayerCurrentLevelNumber");

        if (isPlayerInfoAvailable()) {
            return player.getLevelInfo().getCurrentLevel().getLevelNumber();
        }

        return -1;
    }

}
