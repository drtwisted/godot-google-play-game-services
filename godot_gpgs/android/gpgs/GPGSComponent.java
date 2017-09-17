package org.godotengine.godot.gpgs;


import android.app.Activity;

import com.google.android.gms.common.api.GoogleApiClient;


public class GPGSComponent extends GPGSEntity {

    private Client client;

    public Client getClient() {
        return client;
    }

    GPGSComponent(Client client, Activity activity, String tag, String module) {
        super(activity, tag, module);
        this.client = client;
    }
}
