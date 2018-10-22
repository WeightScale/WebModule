package com.kostya.myapplication;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetAddress;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

abstract class Commands {
    private static InterfaceModule interfaceModule;
    OkHttpClient client;
    public Gson gson = new Gson();
    InetAddress hostAddress;
    static ClassWT WT = new ClassWT();
    static ClassSWT SWT;
    static ClassTP TP = new ClassTP();
    abstract void getParam();

    /** Класс команды получить данные вес заряд стабильный вес */
    static class ClassWT extends Commands{
        @SerializedName("cmd")
        String command;
        @SerializedName("w")
        double weight;
        @SerializedName("c")
        int charge;
        @SerializedName("s")
        boolean stable;

        @Override
        void getParam(){
            ObjectCommand obj = interfaceModule.sendCommand("wt");
        }
    }

    /** Класс комманды отправленн стабильный вес */
    class ClassSWT{
        @SerializedName("cmd")
        String command;
        @SerializedName("d")
        String time;
        @SerializedName("v")
        double weight;
    }

    /** Класс комманда сбросить в ноль */
    static class ClassTP extends  Commands{
        @SerializedName("cmd")
        String command;

        @Override
        void getParam(){
            ObjectCommand obj = interfaceModule.sendCommand("tp");
        }
    }

    static class ClassCDATE extends Commands{
        @SerializedName("st_id")
        int command;
        @SerializedName("av_id")
        int average;
        @SerializedName("mw_id")
        int maxWeight;
        @SerializedName("ofs")
        int offset;
        @SerializedName("ac_id")
        boolean stable;
        @SerializedName("scale")
        int scale;
        @SerializedName("fl_id")
        int filter;
        @SerializedName("sl_id")
        int seal;
        @SerializedName("us_id")
        String user;
        @SerializedName("ps_id")
        String password;

        @Override
        void getParam() {
            Request request = new Request.Builder()
                    .url("http://" + hostAddress.getHostAddress() + "/" + "cdate.json")
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
    }

    static class ClassSettingsScale{
        @SerializedName("id_auto")
        boolean auto;
        @SerializedName("bat_max")
        int batteryMax;
        @SerializedName("id_pe")
        boolean powerEnable;
        @SerializedName("id_pt")
        long powerTime;
        @SerializedName("id_n_admin")
        String nameAdmin;
        @SerializedName("id_p_admin")
        String keyAdmin;
        @SerializedName("id_lan_ip")
        String lanIp;
        @SerializedName("id_gateway")
        String gateway;
        @SerializedName("id_subnet")
        String subnet;
        @SerializedName("id_ssid")
        String ssid;
        @SerializedName("id_key")
        String key;
    }

    static  class ClassSettingsServer{
        @SerializedName("id_host")
        String host;
        @SerializedName("id_pin")
        long pin;
    }
    static void setInterfaceCommand(InterfaceModule i){
        interfaceModule = i;
    }
}
