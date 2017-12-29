package org.godotengine.godot.gpgs;


import android.app.Activity;
import android.util.Log;

public class GPGSEntity {

    public String TAG, MODULE;
    public boolean debug;
    private Activity activity;

    public void setDebug(boolean state) {
        debug = state;
    }

    public Activity getActivity() {
        return activity;
    }

    public void debugLog(String message) {
        if (debug) Log.d(TAG, MODULE + ": " + message);
    }

    public GPGSEntity(
        Activity activity, String tag, String module, boolean debug) {
        TAG = tag;
        MODULE = module;
        this.activity = (Activity) activity;
        this.debug = debug;
    }
}
