package io.uppercase.unicorn.model.handler;

import android.util.Log;

public abstract class CountHandler {

    public void error(String errorMsg) {
        Log.e("UPPERCASE-MODEL", "`count` ERROR: " + errorMsg);
    }

    public void notAuthed() {
        Log.e("UPPERCASE-MODEL", "`count` NOT AUTHED!");
    }

    public void success(Long count) {
    }
}
