package org.godotengine.godot.gpgs;


import android.app.Activity;
import android.app.Dialog;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.games.Games;
import com.google.android.gms.plus.Plus;

import org.godotengine.godot.BaseGameUtils;
import org.godotengine.godot.GodotLib;
import org.godotengine.godot.GodotPlayGameServices;

public class Client extends GPGSEntity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SIGN_IN = 9001;

    private Boolean isIntentInProgress = false;
    private Boolean isResolvingConnectionFailure = false;

    private GoogleApiClient googleApiClient;
    private GodotPlayGameServices parent;

    public GoogleApiClient getGoogleApiClient() {
        return googleApiClient;
    }

    public Client(GodotPlayGameServices parent, Activity activity, String tag, String module) {
        super(activity, tag, module);
        this.parent = parent;
    }

    public void init() {
        final Client thisReference = this;

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, MODULE + ": Client.init : initializing Google Play Game Services...");
                googleApiClient = new GoogleApiClient.Builder(getActivity())
                        .addConnectionCallbacks(thisReference)
                        .addOnConnectionFailedListener(thisReference)
                        .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                        .addApi(Plus.API).addScope(Plus.SCOPE_PLUS_LOGIN)
                        .addApi(Drive.API).addScope(Drive.SCOPE_APPFOLDER)
                        .build();
            }
        });
    }

    public void connect() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, MODULE + ": Client.connect : connecting to Google Play Game Services...");

                googleApiClient.connect();
            }
        });
    }

    public void disconnect() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(!googleApiClient.isConnected()) return;

                Log.i(TAG, MODULE + ": Client.reconnect : disconnecting from Google Play Game Services...");

                Games.signOut(googleApiClient);
                googleApiClient.disconnect();
            }
        });
    }

    public void reconnect() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (googleApiClient.isConnected()) return;

                Log.i(TAG, MODULE + ": Client.reconnect : reconnecting to Google Play Game Services...");

                googleApiClient.connect();
            }
        });

    }

    public boolean isConnected() {
        return googleApiClient.isConnected();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, MODULE + ": onConnected");

        parent.onConnected();

        GodotLib.calldeferred(
            parent.getInstanceID(), "_on_gpgs_connected", new Object[] { });
        isResolvingConnectionFailure = false;
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, MODULE + ": onConnectionSuspended int cause " + String.valueOf(cause));
        GodotLib.calldeferred(parent.getInstanceID(), "_on_gpgs_suspended", new Object[] { cause });
    }

    private void showErrorDialog(@NonNull ConnectionResult connectionResult) {
        Dialog dialog = GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(),
                getActivity(), RC_SIGN_IN);
        if (dialog != null) {
            dialog.show();
        } else {
            // no built-in dialog: show the fallback error message
            BaseGameUtils.showAlert(getActivity(),
                    "Failed to connect to Google Play Game Services! Please check your connection" +
                            "and/or account and try again.");
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (isResolvingConnectionFailure) {
            Log.i(TAG, MODULE + ": onConnectionFailed RESOLVING result code: " + String.valueOf(connectionResult));
            return;
        }

        if (isIntentInProgress) return;

        if (connectionResult.hasResolution()) {
            try {
                isIntentInProgress = true;
                getActivity().startIntentSenderForResult(
                        connectionResult.getResolution().getIntentSender(), RC_SIGN_IN, null, 0, 0, 0);
            } catch (IntentSender.SendIntentException ex) {
                isIntentInProgress = false;
                googleApiClient.connect();
            }
            isResolvingConnectionFailure = true;
            return;
        } else {
            showErrorDialog(connectionResult);
        }

        GodotLib.calldeferred(parent.getInstanceID(), "_on_gpgs_connection_failed", new Object[] { });
        Log.i(TAG, MODULE + ": onConnectionFailed result code: " + String.valueOf(connectionResult));
    }
}
