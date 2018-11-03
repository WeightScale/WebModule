package com.kostya.myapplication;

import android.app.Application;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.net.InetSocketAddress;

public class Main extends Application implements WifiBaseManager.OnWifiBaseManagerListener {
    private WebScalesClient webScalesClient;
    private WifiBaseManager wifiBaseManager;
    private Settings settings;
    public static String SSID;
    public static String HOST;
    public static final String SETTINGS = Main.class.getName() + ".SETTINGS"; //

    @Override
    public void onCreate() {
        super.onCreate();
        settings = new Settings(this/*, SETTINGS*/);
        SSID = settings.read( getString(R.string.KEY_SSID) , "KONST");
        wifiBaseManager = new WifiBaseManager(this,this);
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
        }

        public void onBecameBackground() {
            closeSocketConnection();
        }

        @Override
        public void onBecameDestroy() {
            if(isSocketConnected()){
                closeSocketConnection();
            }
            wifiBaseManager.terminate();
            System.runFinalizersOnExit(true);
            System.exit(0);
        }
    };

    @Override
    public void onWiFiConnect(String ssid, InetSocketAddress ipAddress) {
        BackgroundManager.get(this).registerListener(appActivityListener);
        HOST = settings.read( getString(R.string.KEY_HOST) , "scales");
        if(!isSocketConnected()){
            reconnect();
        }
    }

    @Override
    public void onWiFiDisconnect() {
        BackgroundManager.get(this).unregisterListener(appActivityListener);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
