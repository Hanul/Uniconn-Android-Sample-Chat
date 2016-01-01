package io.uppercase.unicorn.model.handler;

import android.util.Log;

public abstract class ExistsHandler {

    public void error(String errorMsg) {
        Log.e("UPPERCASE-MODEL", "`checkIsExists` ERROR: " + errorMsg);
    }

    public void notAuthed() {
        Log.e("UPPERCASE-MODEL", "`checkIsExists` NOT AUTHED!");
    }

    public void success(Boolean isExists) {
    }
}
