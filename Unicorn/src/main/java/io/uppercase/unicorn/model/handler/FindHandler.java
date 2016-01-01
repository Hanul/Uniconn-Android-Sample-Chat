package io.uppercase.unicorn.model.handler;

import android.util.Log;

import org.json.JSONObject;

import java.util.List;

public abstract class FindHandler {

    public void error(String errorMsg) {
        Log.e("UPPERCASE-MODEL", "`find` ERROR: " + errorMsg);
    }

    public void notAuthed() {
        Log.e("UPPERCASE-MODEL", "`find` NOT AUTHED!");
    }

    public void success(List<JSONObject> savedDataSet) {
    }
}
