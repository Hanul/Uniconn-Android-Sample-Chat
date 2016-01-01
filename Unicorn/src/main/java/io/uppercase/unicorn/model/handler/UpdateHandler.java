package io.uppercase.unicorn.model.handler;

import android.util.Log;

import org.json.JSONObject;

public abstract class UpdateHandler {

    public void error(String errorMsg) {
        Log.e("UPPERCASE-MODEL", "`update` ERROR: " + errorMsg);
    }

    public void notExists() {
        Log.e("UPPERCASE-MODEL", "`update` NOT EXISTS!");
    }

    public void notValid(JSONObject validErrors) {
        Log.e("UPPERCASE-MODEL", "`update` NOT VALID!: " + validErrors);
    }

    public void notAuthed() {
        Log.e("UPPERCASE-MODEL", "`update` NOT AUTHED!");
    }

    public void success(JSONObject savedData, JSONObject originData) {
    }
}
