package io.uppercase.unicorn.model.handler;

import org.json.JSONObject;

public interface OnRemoveHandler {

    /**
     * @param originData
     */
    public void handle(JSONObject originData);
}
