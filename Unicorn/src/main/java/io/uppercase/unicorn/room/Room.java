package io.uppercase.unicorn.room;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.uppercase.unicorn.handler.MethodHandler;

public class Room {

    private RoomConnector connector;

    private String roomName;
    private Map<String, List<MethodHandler>> methodHandlerMap = new HashMap<String, List<MethodHandler>>();
    private boolean exited;

    /**
     * @param connector
     * @param boxName
     * @param name
     */
    public Room(RoomConnector connector, String boxName, String name) {
        this.connector = connector;
        connector.enterRoom(roomName = boxName + "/" + name);
    }

    /**
     * @param methodName
     * @param methodHandler
     */
    public void on(String methodName, MethodHandler methodHandler) {

        List<MethodHandler> methodHandlers = methodHandlerMap.get(roomName + "/" + methodName);

        connector.on(roomName + "/" + methodName, methodHandler);

        if (methodHandlerMap.get(roomName + "/" + methodName) == null) {
            methodHandlerMap.put(roomName + "/" + methodName, methodHandlers = new ArrayList<MethodHandler>());
        }

        methodHandlers.add(methodHandler);
    }

    /**
     * @param methodName
     * @param methodHandler
     */
    public void off(String methodName, MethodHandler methodHandler) {

        List<MethodHandler> methodHandlers = methodHandlerMap.get(roomName + "/" + methodName);

        connector.off(roomName + "/" + methodName, methodHandler);

        methodHandlers.remove(methodHandler);

        if (methodHandlers.size() == 0) {
            off(methodName);
        }
    }

    /**
     * @param methodName
     */
    public void off(String methodName) {

        List<MethodHandler> methodHandlers = methodHandlerMap.get(roomName + "/" + methodName);

        for (MethodHandler methodHandler : methodHandlers) {
            connector.off(roomName + "/" + methodName, methodHandler);
        }

        methodHandlerMap.remove(roomName + "/" + methodName);
    }

    /**
     * @param methodName
     * @param data
     * @param methodHandler
     */
    public void send(String methodName, Object data, MethodHandler methodHandler) {
        if (exited != true) {
            connector.send(roomName + "/" + methodName, data, methodHandler);
        } else {
            Log.e("UPPERCASE-ROOM", "`ROOM.send` ERROR! ROOM EXITED!");
        }
    }

    /**
     * @param methodName
     * @param data
     */
    public void send(String methodName, Object data) {
        send(methodName, data, null);
    }

    /**
     * exit.
     */
    public void exit() {

        if (exited != true) {

            connector.exitRoom(roomName);

            for (String fullMethodName : methodHandlerMap.keySet()) {

                List<MethodHandler> methodHandlers = methodHandlerMap.get(fullMethodName);

                for (MethodHandler methodHandler : methodHandlers) {
                    connector.off(fullMethodName, methodHandler);
                }

                methodHandlerMap.remove(fullMethodName);
            }

            // free method handler map.
            methodHandlerMap = null;

            exited = true;
        }
    }
}
