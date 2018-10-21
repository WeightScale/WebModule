package com.kostya.myapplication;

import android.app.Application;
import android.util.Log;

import java.net.InetSocketAddress;

public class Main extends Application implements WifiBaseManager.OnWifiBaseManagerListener {
    private WebScalesClient webScalesClient;
    private WifiBaseManager wifiBaseManager;

    @Override
    public void onCreate() {
        super.onCreate();
        wifiBaseManager = new WifiBaseManager(this, "scales", this);
        webScalesClient = new WebScalesClient(this);
        Commands.setInterfaceCommand(webScalesClient);
    }


    public void closeSocketConnection() {
        webScalesClient.closeConnection();
    }

    public void openSocketConnection() {
        webScalesClient.openConnection();
    }

    public boolean isSocketConnected() {
        return webScalesClient.isConnected();
    }

    public void reconnect() {
        webScalesClient.openConnection();
    }


    private BackgroundManager.Listener appActivityListener = new BackgroundManager.Listener() {
        public void onBecameForeground() {
            openSocketConnection();
            Log.i("Websocket", "Became Foreground");
        }

        public void onBecameBackground() {
            closeSocketConnection();
            Log.i("Websocket", "Became Background");
        }
    };

    @Override
    public void onConnect(String ssid, InetSocketAddress ipAddress) {
        BackgroundManager.get(this).registerListener(appActivityListener);
        if(!isSocketConnected()){
            reconnect();
        }
    }

    @Override
    public void onDisconnect() {
        BackgroundManager.get(this).unregisterListener(appActivityListener);
    }
}
