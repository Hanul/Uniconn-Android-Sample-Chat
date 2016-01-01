package io.uppercase.unicorn.room;

import java.util.ArrayList;
import java.util.List;

import io.uppercase.unicorn.SocketServerConnector;
import io.uppercase.unicorn.handler.ConnectedHandler;
import io.uppercase.unicorn.handler.ConnectionFailedHandler;
import io.uppercase.unicorn.handler.DisconnectedHandler;
import io.uppercase.unicorn.handler.MethodHandler;

/**
 * UPPERCASE-ROOM 모듈의 룸 서버와의 접속 및 통신 작업을 처리하는 클래스
 */
public class RoomConnector {

    private SocketServerConnector connector;

    private List<SendInfo> waitingSendInfos = new ArrayList<SendInfo>();
    private List<String> enterRoomNames = new ArrayList<String>();

    public void disconnect() {
        connector.disconnect();
    }

    public void reconnect() {
        connector.reconnect();
    }

    public void connect(String host) {
        connector.connect(host);
    }

    /**
     * @param port
     * @param connectedHandler
     * @param connectionFailedHandler
     * @param disconnectedHandler
     */
    public RoomConnector(int port, final ConnectedHandler connectedHandler, ConnectionFailedHandler connectionFailedHandler, DisconnectedHandler disconnectedHandler) {

        connector = new SocketServerConnector(port, new ConnectedHandler() {

            @Override
            public void handle() {

                for (String roomName : enterRoomNames) {
                    send("__ENTER_ROOM", roomName);
                }

                for (SendInfo sendInfo : waitingSendInfos) {
                    send(sendInfo.getMethodName(), sendInfo.getData(), sendInfo.getMethodHandler());
                }

                waitingSendInfos = new ArrayList<SendInfo>();

                connectedHandler.handle();

            }

        }, connectionFailedHandler, disconnectedHandler);
    }

    /**
     * @param host
     * @param port
     * @param connectedHandler
     * @param connectionFailedHandler
     * @param disconnectedHandler
     */
    public RoomConnector(String host, int port, final ConnectedHandler connectedHandler, ConnectionFailedHandler connectionFailedHandler, DisconnectedHandler disconnectedHandler) {
        this(port, connectedHandler, connectionFailedHandler, disconnectedHandler);
        connect(host);
    }

    public boolean isConnected() {
        return connector.isConnected();
    }

    /**
     * @param methodName
     * @param methodHandler
     */
    public void on(String methodName, MethodHandler methodHandler) {
        connector.on(methodName, methodHandler);
    }

    /**
     * @param methodName
     * @param methodHandler
     */
    public void off(String methodName, MethodHandler methodHandler) {
        connector.off(methodName, methodHandler);
    }

    /**
     * @param methodName
     */
    public void off(String methodName) {
        connector.off(methodName);
    }

    /**
     * @param methodName
     * @param data
     * @param methodHandler
     */
    public void send(String methodName, Object data, final MethodHandler methodHandler) {
        if (isConnected() != true) {
            waitingSendInfos.add(new SendInfo(methodName, data, methodHandler));
        } else {
            connector.send(methodName, data, methodHandler);
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
     * @param roomName
     */
    public void enterRoom(String roomName) {
        enterRoomNames.add(roomName);
        if (isConnected() != true) {
            send("__ENTER_ROOM", roomName);
        }
    }

    /**
     * @param roomName
     */
    public void exitRoom(String roomName) {
        if (isConnected() != true) {
            send("__EXIT_ROOM", roomName);
        }
        enterRoomNames.remove(roomName);
    }
}
