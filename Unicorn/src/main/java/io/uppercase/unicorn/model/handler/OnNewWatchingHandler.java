package io.uppercase.unicorn.model.handler;

import org.json.JSONObject;

public abstract class OnNewWatchingHandler {

    protected void addUpdateHandler(OnUpdateHandler handler) {
        //TODO: 구현해야함
    }

    /**
     * @param savedData
     */
    abstract public void handle(JSONObject savedData);
}
