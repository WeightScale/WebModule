package com.kostya.myapplication;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;



public class WebScalesClient implements Client.MessageListener, InterfaceModule{
    private Client clientWebSocket;
    private Context context;
    private Settings settings;

    private Gson gson;
    private Handler socketConnectionHandler;
    private ResponseCommand response;
    private static final String TAG = "WebScalesClient";


    WebScalesClient(Context context) {
        this.context = context;
        settings = new Settings(context/*, Main.SETTINGS*/);
        socketConnectionHandler = new Handler();
        gson = new GsonBuilder().create();
    }

    private void startCheckConnection() {
        socketConnectionHandler.postDelayed(checkConnectionRunnable, 5000);
    }

    private void stopCheckConnection() {
        socketConnectionHandler.removeCallbacks(checkConnectionRunnable);
    }

    boolean isConnected() {
        return clientWebSocket != null &&
                clientWebSocket.getConnection() != null &&
                clientWebSocket.getConnection().isOpen();
    }

    private Runnable checkConnectionRunnable = () -> {
        try{
            if (!clientWebSocket.getConnection().isOpen()) {
                openConnection();
                return;
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            startCheckConnection();
        }
    };

    void openConnection() {
        if (clientWebSocket != null)
            clientWebSocket.close();
        try {
            //clientWebSocket = new Client(this, BuildConfig.SOCKET_URL + Preferences.getManager().getUserId());
            clientWebSocket = new Client(this, Main.HOST);
            clientWebSocket.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //initScreenStateListener();
        startCheckConnection();
    }

    void closeConnection() {
        if (clientWebSocket != null) {
            clientWebSocket.close();
            clientWebSocket = null;
        }
        //releaseScreenStateListener();
        stopCheckConnection();
    }

    @Override
    public void onSocketMessage(String message) {
        try {
            JSONObject jsonObject = new JSONObject(message);
            if (jsonObject.has("cmd")){
                switch (jsonObject.getString("cmd")){
                    case "swt":
                        EventBus.getDefault().post(gson.fromJson(message,Commands.ClassSWT.class));
                        break;
                    case "wt":
                        EventBus.getDefault().post(gson.fromJson(message, Commands.ClassWT.class));
                        break;
                }
            }

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    @Override
    public ObjectCommand sendCommand(String cmd) {
        try {
            clientWebSocket.send("{'cmd':'"+cmd+"'}");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
