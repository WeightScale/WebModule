package com.kostya.myapplication;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.neovisionaries.ws.client.ThreadType;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;
import com.neovisionaries.ws.client.WebSocketState;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.net.ssl.SSLContext;

public class Client{
    ObjectCommand response;
    //List<BasicNameValuePair> extraHeaders = Collections.singletonList(new BasicNameValuePair("Cookie", "session=abcd"));
    Handler handler = new Handler();
    private AtomicBoolean working;
    private InetSocketAddress inetSocketAddress;
    private MessageListener listener;
    private String host;
    private WebSocket ws;
    private static final int TIME_OUT_CONNECT = 5000; /** Время в милисекундах. */
    private static final String TAG = "Websocket";

    //public abstract void killWorkingThread();
    //public abstract void restartWorkingThread();
    //public abstract void write(String data);
    //public abstract ObjectCommand sendCommand(Commands cmd);
    //protected abstract boolean writeByte(byte ch);
    //protected abstract int getByte();

    Client(MessageListener listener, String host){
        this.host = "ws://"+host+"/ws";
        this.listener = listener;
    }

    Client(MessageListener listener, InetSocketAddress address){
        inetSocketAddress = address;
        this.host = "ws://"+inetSocketAddress.getHostString()+"/ws";
    }


    void connect() {
        new Thread(() -> {

            if (ws != null) {
                reconnect();
            } else {
                try {
                    WebSocketFactory factory = new WebSocketFactory().setConnectionTimeout(TIME_OUT_CONNECT);
                    SSLContext context = NaiveSSLContext.getInstance("TLS");
                    factory.setSSLContext(context);
                    ws = factory.createSocket(host);
                    ws.addListener(new SocketListener());
                    ws.setMissingCloseFrameAllowed(true);
                    ws.setPingInterval(5000);
                    ws.setPingSenderName("Scales");
                    ws.connect();
                } catch (WebSocketException | IOException | NoSuchAlgorithmException e) {
                    //EventBus.getDefault().post(new MessageEventSocket(MessageEventSocket.Message.ERROR, e.toString()));
                }
            }
        }).start();
    }

    private void reconnect() {
        try {
            ws = ws.recreate().connect();
        } catch (WebSocketException | IOException e) {
            e.printStackTrace();
        }
    }

    WebSocket getConnection() {
        return ws;
    }

    public void close() {
        ws.disconnect();
    }

    void send(String data) throws Exception {
        ws.sendText(data);
    }

    private Runnable pongTimeOutRunable = new Runnable() {
        @Override
        public void run() {
            close();
            //handler.postDelayed(pongTimeOutRunable, 10000);
        }
    };

    public class SocketListener extends WebSocketAdapter {

        @Override
        public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
            super.onConnected(websocket, headers);
            InetAddress uri = websocket.getSocket().getInetAddress();
            EventBus.getDefault().postSticky(uri);
            EventBus.getDefault().post(new MessageEventSocket(MessageEventSocket.Message.CONNECT, "Connected"));
            //WebScalesClient.Cmd.WT.getParam();
            //Log.i(TAG, "onConnected");
        }

        public void onTextMessage(WebSocket websocket, String message) throws Exception {
            super.onTextMessage(websocket,message);
            listener.onSocketMessage(message);
            //Log.i(TAG, "Message --> " + message);
        }

        @Override
        public void onError(WebSocket websocket, WebSocketException cause) throws Exception {
            super.onError(websocket,cause);
            //Log.i(TAG, "Error -->" + cause.getMessage());
            //EventBus.getDefault().post(new MessageEventSocket(MessageEventSocket.Message.ERROR, cause.toString()));

            //reconnect();
        }

        @Override
        public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {
            super.onDisconnected(websocket,serverCloseFrame,clientCloseFrame,closedByServer);
            //Log.i(TAG, "onDisconnected");
            //EventBus.getDefault().post(new MessageEventSocket(MessageEventSocket.Message.DISCONNECT, "Disconnected"));
            if (closedByServer) {
                reconnect();
            }
        }

        @Override
        public void onUnexpectedError(WebSocket websocket, WebSocketException cause) throws Exception {
            super.onUnexpectedError(websocket,cause);
            //EventBus.getDefault().post(new MessageEventSocket(MessageEventSocket.Message.UNEXPECTED, cause.toString()));
            //Log.i(TAG, "Error -->" + cause.getMessage());
            reconnect();
        }

        @Override
        public void onPongFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
            super.onPongFrame(websocket, frame);
            handler.removeCallbacks(pongTimeOutRunable);
            handler.postDelayed(pongTimeOutRunable, 10000);
            //websocket.sendPing("Are you there?");
        }

        @Override
        public void onPingFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
            super.onPingFrame(websocket, frame);
            //websocket.sendPong("Ok");
        }

        @Override
        public void onStateChanged(WebSocket websocket, WebSocketState newState) throws Exception {
            super.onStateChanged(websocket, newState);
            EventBus.getDefault().post(new MessageEventSocket(MessageEventSocket.Message.STATE, newState.name()));
        }

    }

    static class MessageEventSocket{
        enum Message{
            CONNECT,
            DISCONNECT,
            ERROR,
            STATE,
            UNEXPECTED
        }
        Message message;
        String text;

        MessageEventSocket(Message message, String text){
            this.message = message;
            this.text = text;
        }
    }

    public interface MessageListener {
        void onSocketMessage(String message);
    }

}
