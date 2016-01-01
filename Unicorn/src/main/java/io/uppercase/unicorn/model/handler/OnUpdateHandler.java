package io.uppercase.unicorn.model.handler;

import org.json.JSONObject;

public interface OnUpdateHandler {

    /**
     * @param savedData
     */
    public void handle(JSONObject savedData);
}
