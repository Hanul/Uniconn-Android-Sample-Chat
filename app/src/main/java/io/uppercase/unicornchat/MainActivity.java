package io.uppercase.unicornchat;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import io.uppercase.unicorn.Connector;
import io.uppercase.unicorn.handler.ConnectedHandler;
import io.uppercase.unicorn.handler.ConnectionFailedHandler;
import io.uppercase.unicorn.handler.DisconnectedHandler;
import io.uppercase.unicorn.model.Model;
import io.uppercase.unicorn.model.handler.FindHandler;
import io.uppercase.unicorn.model.handler.OnNewHandler;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private Connector connector;
    private Model chatModel;

    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView contentInputView = (TextView) findViewById(R.id.content_input);
        contentInputView.requestFocus();

        ListView list = (ListView) findViewById(R.id.list);
        adapter = new ArrayAdapter<String>(this, R.layout.item, R.id.item_text);
        list.setAdapter(adapter);

        Log.i(TAG, "접속을 시도합니다.");

        connector = new Connector("192.168.0.6", false, 8101, 8102, new ConnectedHandler() {

            @Override
            public void handle() {
                Log.i(TAG, "접속되었습니다.");
            }

        }, new ConnectionFailedHandler() {

            @Override
            public void handle() {
                Log.i(TAG, "접속에 실패하였습니다.");
                connector.reconnect();
            }

        }, new DisconnectedHandler() {

            @Override
            public void handle() {
                Log.i(TAG, "접속이 끊어졌습니다.");
                connector.reconnect();
            }
        });

        chatModel = new Model(connector, "SampleChat", "Message");

        JSONObject sort = new JSONObject();
        try {
            sort.put("createTime", 1);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        chatModel.find(null, sort, 100, new FindHandler() {

            @Override
            public void success(final List<JSONObject> savedDataSet) {

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        for (JSONObject savedData : savedDataSet) {
                            appendItem(savedData);
                        }
                    }
                });
            }
        });

        chatModel.onNew(new OnNewHandler() {

            @Override
            public void handle(JSONObject savedData) {
                appendItem(savedData);
            }
        });
    }

    public void appendItem(final JSONObject savedData) {

        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                try {
                    adapter.add(savedData.getString("writer") + ": " + savedData.getString("content"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void sendMessage(View view) {
        TextView writerInputView = (TextView) findViewById(R.id.writer_input);
        TextView contentInputView = (TextView) findViewById(R.id.content_input);

        JSONObject data = new JSONObject();
        try {
            data.put("writer", writerInputView.getText());
            data.put("content", contentInputView.getText());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        chatModel.create(data);

        contentInputView.setText("");
    }
}
