package com.kostya.myapplication;

import com.google.gson.annotations.SerializedName;

abstract class Commands {
    private static InterfaceModule interfaceModule;
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
        @SerializedName("cmd")
        String command;
        @SerializedName("w")
        double weight;
        @SerializedName("c")
        int charge;
        @SerializedName("s")
        boolean stable;

        @Override
        void getParam() {

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
