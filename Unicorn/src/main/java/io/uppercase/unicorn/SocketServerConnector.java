package io.uppercase.unicorn;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.uppercase.unicorn.handler.ConnectedHandler;
import io.uppercase.unicorn.handler.ConnectionFailedHandler;
import io.uppercase.unicorn.handler.DisconnectedHandler;
import io.uppercase.unicorn.handler.MethodHandler;

/**
 * UJS의 SOCKET_SERVER로 만들어진 서버와의 접속 및 통신 작업을 처리하는 클래스
 */
public class SocketServerConnector {

    private static final String TAG = "SocketServerConnector";

    private String host;
    private int port;
    private ConnectedHandler connectedHandler;
    private ConnectionFailedHandler connectionFailedHandler;
    private DisconnectedHandler disconnectedHandler;

    private PrintWriter out;

    private Map<String, List<MethodHandler>> methodHandlerMap = new HashMap<String, List<MethodHandler>>();
    private boolean connected = false;

    private int sendKey = 0;

    public void disconnect() {
        connected = false;
    }

    public void reconnect() {
        disconnect();
        new ConnectTask().execute();
    }

    public void connect(String host) {
        this.host = host;
        reconnect();
    }

    /**
     * @param port
     * @param connectedHandler
     * @param connectionFailedHandler
     * @param disconnectedHandler
     */
    public SocketServerConnector(int port, ConnectedHandler connectedHandler, ConnectionFailedHandler connectionFailedHandler, DisconnectedHandler disconnectedHandler) {
        this.port = port;
        this.connectedHandler = connectedHandler;
        this.connectionFailedHandler = connectionFailedHandler;
        this.disconnectedHandler = disconnectedHandler;
    }

    /**
     * @param host
     * @param port
     * @param connectedHandler
     * @param connectionFailedHandler
     * @param disconnectedHandler
     */
    public SocketServerConnector(String host, int port, ConnectedHandler connectedHandler, ConnectionFailedHandler connectionFailedHandler, DisconnectedHandler disconnectedHandler) {
        this(port, connectedHandler, connectionFailedHandler, disconnectedHandler);
        connect(host);
    }

    /**
     * @return connected
     */
    public boolean isConnected() {
        return connected;
    }

    /**
     * @param methodName
     * @param methodHandler
     */
    public void on(String methodName, MethodHandler methodHandler) {

        List<MethodHandler> methodHandlers = methodHandlerMap.get(methodName);

        if (methodHandlers == null) {
            methodHandlerMap.put(methodName, methodHandlers = new ArrayList<MethodHandler>());
        }

        methodHandlers.add(methodHandler);
    }

    /**
     * @param methodName
     * @param methodHandler
     */
    public void off(String methodName, MethodHandler methodHandler) {

        List<MethodHandler> methodHandlers = methodHandlerMap.get(methodName);

        methodHandlers.remove(methodHandler);

        if (methodHandlers.size() == 0) {
            off(methodName);
        }
    }

    /**
     * @param methodName
     */
    public void off(String methodName) {
        methodHandlerMap.remove(methodName);
    }

    /**
     * @param methodName
     * @param data
     * @param methodHandler
     */
    public void send(String methodName, Object data, final MethodHandler methodHandler) {

        if (isConnected() == true) {

            final JSONObject sendData = new JSONObject();
            try {
                sendData.put("methodName", methodName);
                sendData.put("data", data instanceof JSONObject ? JSONUtil.packData((JSONObject) data) : data);
                sendData.put("sendKey", sendKey);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            final String callbackName = "__CALLBACK_" + sendKey;

            if (methodHandler != null) {

                // on callback.
                on(callbackName, new MethodHandler() {

                    @Override
                    public void handle(Object data) {

                        // run callback.
                        methodHandler.handle(data);

                        // off callback.
                        off(callbackName);
                    }
                });
            }

            sendKey += 1;

            new Thread() {

                public void run() {
                    out.write(sendData + "\r\n");
                    out.flush();
                }

            }.start();
        }

        else {
            Log.e(TAG, "IS NOT CONNECTED!");
        }
    }

    /**
     * @param methodName
     * @param data
     */
    public void send(String methodName, Object data) {
        send(methodName, data, null);
    }

    public class ConnectTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {

                Socket socket = new Socket(host, port);

                final BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

                connected = true;

                connectedHandler.handle();

                new Thread() {

                    public void run() {
                        try {

                            while (connected == true) {

                                String str = reader.readLine();

                                if (str != null) {

                                    JSONObject json = new JSONObject(str);

                                    String methodName = json.getString("methodName");
                                    Object data = null;

                                    if (json.isNull("data") != true) {
                                        data = json.get("data");
                                    }

                                    List<MethodHandler> methodHandlers = methodHandlerMap.get(methodName);

                                    if (methodHandlers != null) {
                                        for (MethodHandler methodHandler : methodHandlers) {
                                            methodHandler.handle(data instanceof JSONObject ? JSONUtil.unpackData((JSONObject) data) : data);
                                        }
                                    }
                                }

                                // disconnected
                                else {
                                    connected = false;
                                    disconnectedHandler.handle();
                                }
                            }

                        } catch (SocketException e) {
                            e.printStackTrace();

                            connected = false;
                            disconnectedHandler.handle();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }.start();

            } catch (ConnectException e) {
                e.printStackTrace();

                connectionFailedHandler.handle();

            } catch (UnknownHostException e) {
                e.printStackTrace();

                connectionFailedHandler.handle();

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
