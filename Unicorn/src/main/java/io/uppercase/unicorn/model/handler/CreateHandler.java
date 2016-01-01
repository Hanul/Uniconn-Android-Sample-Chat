package io.uppercase.unicorn.model.handler;

import android.util.Log;

import org.json.JSONObject;

public abstract class CreateHandler {

    public void error(String errorMsg) {
        Log.e("UPPERCASE-MODEL", "`create` ERROR: " + errorMsg);
    }

    public void notValid(JSONObject validErrors) {
        Log.e("UPPERCASE-MODEL", "`create` NOT VALID!: " + validErrors);
    }

    public void notAuthed() {
        Log.e("UPPERCASE-MODEL", "`create` NOT AUTHED!");
    }

    public void success(JSONObject savedData) {
    }
}
