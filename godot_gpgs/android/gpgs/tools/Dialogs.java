package org.godotengine.godot.gpgs.tools;

import android.content.Context;
import android.support.v7.app.AlertDialog;


public class Dialogs extends Object {
    public static void showSimpleAlert(Context context, String message) {
        (new AlertDialog.Builder(context)
                .setMessage(message)
                .setNeutralButton(android.R.string.ok, null)).create().show();
    }
}
