package io.uppercase.unicorn.model.handler;

import android.util.Log;

import org.json.JSONObject;

public abstract class RemoveHandler {

    public void error(String errorMsg) {
        Log.e("UPPERCASE-MODEL", "`remove` ERROR: " + errorMsg);
    }

    public void notExists() {
        Log.e("UPPERCASE-MODEL", "`remove` NOT EXISTS!");
    }

    public void notAuthed() {
        Log.e("UPPERCASE-MODEL", "`remove` NOT AUTHED!");
    }

    public void success(JSONObject originData) {
    }
}
