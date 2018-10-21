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

    public Gson gson;
    private Handler socketConnectionHandler;
    private ResponseCommand response;
    protected static final String TAG = "WebScalesClient";


    public WebScalesClient(Context context) {
        this.context = context;
        socketConnectionHandler = new Handler();
        gson = new GsonBuilder()
                .create();
    }

    private void startCheckConnection() {
        socketConnectionHandler.postDelayed(checkConnectionRunnable, 5000);
    }

    private void stopCheckConnection() {
        socketConnectionHandler.removeCallbacks(checkConnectionRunnable);
    }

    public boolean isConnected() {
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

    public void openConnection() {
        if (clientWebSocket != null)
            clientWebSocket.close();
        try {
            //clientWebSocket = new Client(this, BuildConfig.SOCKET_URL + Preferences.getManager().getUserId());
            clientWebSocket = new Client(this, "scales");
            clientWebSocket.connect();
            //Log.i("Websocket", "Socket connected by user " + Preferences.getManager().getUserId());
            Log.i("Websocket", "Socket connected by user " + "192.168.1.8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        //initScreenStateListener();
        startCheckConnection();
    }

    public void closeConnection() {
        if (clientWebSocket != null) {
            clientWebSocket.close();
            clientWebSocket = null;
        }
        //releaseScreenStateListener();
        stopCheckConnection();
    }

    @Override
    public void onSocketMessage(String message) {
        //EventBus.getDefault().post(gson.fromJson(message, RealTimeEvent.class));
        try {
            JSONObject jsonObject = new JSONObject(message);
            if (jsonObject.has("cmd")){
                switch (jsonObject.getString("cmd")){
                    case "swt":
                        //Log.i(TAG, jsonObject.toString());
                        EventBus.getDefault().post(gson.fromJson(message,Commands.ClassSWT.class));
                        break;
                    case "wt":
                        //Log.i(TAG, jsonObject.toString());

                        EventBus.getDefault().post(gson.fromJson(message, Commands.ClassWT.class));

                        break;
                }
            }

        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }

    /*@Override
    public void write(String data) throws Exception {
        //s.append('\r').append('\n');
        clientWebSocket.send(data);
    }*/

    @Override
    public ObjectCommand sendCommand(String cmd) {
        try {
            clientWebSocket.send("{'cmd':'"+cmd+"'}");
        } catch (Exception e) {
            e.printStackTrace();
        }
        //write("{'cmd':'"+cmd+"'}");
        /*response = new ResponseCommand(cmd);
        for (int i = 0; i < 500; i++) {
            try { TimeUnit.MILLISECONDS.sleep(1);} catch (InterruptedException e) {e.printStackTrace();}
            try {
                if (response.isResponse()) {
                    return new ObjectCommand(Commands.GET_WT,"");
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }*/
        return null;
    }
}
