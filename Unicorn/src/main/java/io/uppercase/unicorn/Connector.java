package io.uppercase.unicorn;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import io.uppercase.unicorn.handler.ConnectedHandler;
import io.uppercase.unicorn.handler.ConnectionFailedHandler;
import io.uppercase.unicorn.handler.DisconnectedHandler;
import io.uppercase.unicorn.room.RoomServerConnector;

/**
 * UPPERCASE 서버와의 접속 및 통신 작업을 처리하는 클래스
 */
public class Connector extends RoomServerConnector {

    private String doorHost;
    private boolean isSecure;
    private int webServerPort;
    private ConnectionFailedHandler connectionFailedHandler;

    /**
     * @param doorHost
     * @param isSecure
     * @param webServerPort
     * @param socketServerPort
     * @param connectedHandler
     * @param connectionFailedHandler
     * @param disconnectedHandler
     */
    public Connector(String doorHost, boolean isSecure, int webServerPort, int socketServerPort, ConnectedHandler connectedHandler, ConnectionFailedHandler connectionFailedHandler, DisconnectedHandler disconnectedHandler) {
        super(socketServerPort, connectedHandler, connectionFailedHandler, disconnectedHandler);

        this.doorHost = doorHost;
        this.isSecure = isSecure;
        this.webServerPort = webServerPort;
        this.connectionFailedHandler = connectionFailedHandler;

        new ConnectTask().execute();
    }

    public class ConnectTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            String host = null;

            try {

                URL url = new URL((isSecure ? "https://" : "http://") + doorHost + ":" + webServerPort + "/__SOCKET_SERVER_HOST?defaultHost=" + doorHost);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("GET");
                BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                host = "";
                String line;

                while ((line = rd.readLine()) != null) {
                    host += line;
                }

                rd.close();

            } catch (IOException e) {
                e.printStackTrace();

                connectionFailedHandler.handle();
            }

            if (host != null) {
                connect(host);
            }

            return null;
        }
    }
}
