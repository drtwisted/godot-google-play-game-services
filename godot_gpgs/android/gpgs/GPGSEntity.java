package org.godotengine.godot.gpgs;


import android.app.Activity;

public class GPGSEntity {

    public String TAG, MODULE;
    private Activity activity;

    public Activity getActivity() {
        return activity;
    }

    public GPGSEntity(Activity activity, String tag, String module) {
        TAG = tag;
        MODULE = module;
        this.activity = (Activity) activity;
    }
}
