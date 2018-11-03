package com.kostya.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetAddress;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;



public class MainActivity extends AppCompatActivity {
    TextView textView;
    public Gson gson = new Gson();
    Handler handler = new Handler();
    InetAddress hostAdsdress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        textView = findViewById(R.id.text);
        textView.setText("");
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Commands.GET_WT.getParam();
                //Commands.WT.getParam();
                //webScalesClient.sendCommand(Commands.GET_WT.getParam());
                startActivity(new Intent(getApplicationContext(), com.kostya.myapplication.preferences.ActivityProperties.class));
                /*try {
                   run("settings.json");
                } catch (Exception e) {
                    e.printStackTrace();
                }*/

            }
        });
    }

    public class BasicAuthInterceptor implements Interceptor {

        private String credentials;

        public BasicAuthInterceptor(String user, String password) {
            this.credentials = Credentials.basic(user, password);
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Request authenticatedRequest = request.newBuilder()
                    .header("Authorization", credentials).build();
            return chain.proceed(authenticatedRequest);
        }

    }

    private final OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(new BasicAuthInterceptor("sa", "343434"))
            .build();

    public void run(String v) throws Exception {
        Request request = new Request.Builder()
                .url("http://" + Main.HOST + "/" + v)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful())
                    throw new IOException("Unexpected code " + response);
                String s = response.body().string();
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    Commands.ClassSettingsScale settingsScale = gson.fromJson(jsonObject.getString("scale"), Commands.ClassSettingsScale.class);
                    Commands.ClassSettingsServer settingsServer = gson.fromJson(jsonObject.getString("server"),Commands.ClassSettingsServer.class);
                    double scale = jsonObject.getDouble("scale");
                    String us = jsonObject.getString("us_id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        //WebScalesClient.Cmd.WT.getParam();
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    /*@Subscribe(sticky = true)
    public void onEvent(InetAddress event) {
        hostAdsdress = event;
    }*/

    @Subscribe
    public void onEvent(Commands.ClassSWT event) {
        Log.i("Event", event.time);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventSocket(Client.MessageEventSocket eventSocket){
        textView.setText(String.valueOf(eventSocket.text));
        if(eventSocket.message == Client.MessageEventSocket.Message.CONNECT){
            Commands.WT.getParam();
            //handler.postAtTime(checkGetWeightRunable, 5000);
        }
        //Log.i("Event", eventSocket.text);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Commands.ClassWT event) {
        handler.removeCallbacks(checkGetWeightRunable);
        textView.setText(String.valueOf(event.weight));
        //EventBus.getDefault().cancelEventDelivery(WebScalesClient.Cmd.WT);
        if(EventBus.getDefault().hasSubscriberForEvent(Commands.ClassWT.class)){
            Log.i("Event", event.command);
        }
        Commands.WT.getParam();
        handler.postDelayed(checkGetWeightRunable, 5000);
        //Log.i("Event", event.command);
    }

    private Runnable checkGetWeightRunable = new Runnable() {
        @Override
        public void run() {
            textView.setText(String.valueOf("--"));
            Commands.WT.getParam();
            handler.postDelayed(checkGetWeightRunable, 5000);
        }
    };
}
