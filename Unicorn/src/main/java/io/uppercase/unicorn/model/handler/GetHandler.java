package io.uppercase.unicorn.model.handler;

import android.util.Log;

import org.json.JSONObject;

public abstract class GetHandler {

    public void error(String errorMsg) {
        Log.e("UPPERCASE-MODEL", "`get` ERROR: " + errorMsg);
    }

    public void notExists() {
        Log.e("UPPERCASE-MODEL", "`get` NOT EXISTS!");
    }

    public void notAuthed() {
        Log.e("UPPERCASE-MODEL", "`get` NOT AUTHED!");
    }

    public void success(JSONObject savedData) {
    }
}
