//Простой класс настроек
package com.kostya.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Settings {
    final Context mContext;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    public static final String SETTINGS = Settings.class.getName() + ".SETTINGS"; //Имя настроек

    public Settings(Context context, String name) {
        mContext = context;
        load(mContext.getSharedPreferences(name, Context.MODE_PRIVATE)); //загрузить настройки
    }

    public Settings(Context context) {
        mContext = context;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPreferences.edit();
        editor.apply();
    }

    public void load(SharedPreferences sp) {
        sharedPreferences = sp;
        editor = sp.edit();
        editor.apply();
    }

    public void write(String key, String value) {
        editor.putString(key, value).apply();
    }

    public void write(int key, String value) {
        editor.putString(mContext.getString(key), value).apply();
    }

    public void write(int key, boolean value) {
        editor.putBoolean(mContext.getString(key), value).apply();
    }

    public void write(int key, int value) {
        editor.putInt(mContext.getString(key), value).apply();
    }

    public void write(String key, int value) {
        editor.putInt(key, value).apply();
    }

    public void write(String key, float value) {
        editor.putFloat(key, value).apply();
    }

    public void write(String key, boolean value) {
        editor.putBoolean(key, value).apply();
    }

    public String read(int key, String def) {
        return sharedPreferences.getString(mContext.getString(key), def);
    }

    /*public boolean read(KEY key, boolean def) {

        return sharedPreferences.getBoolean(mContext.getString(key.getResId()), def);
    }*/

    public boolean read(int key, boolean def) {

        return sharedPreferences.getBoolean(mContext.getString(key), def);
    }

    public String read(String key, String def) {
        return sharedPreferences.getString(key, def);
    }

    boolean read(String key, boolean def) {
        return sharedPreferences.getBoolean(key, def);
    }

    public int read(String key, int in) {
        return sharedPreferences.getInt(key, in);
    }

    public int read(int key, int in) {
        return sharedPreferences.getInt(mContext.getString(key), in);
    }

    /*public int read(KEY key, int in) {
        return sharedPreferences.getInt(mContext.getString(key.getResId()), in);
    }*/

    public float read(String key, float in) { return sharedPreferences.getFloat(key, in); }

    /*boolean contains(KEY key) {
        return sharedPreferences.contains(mContext.getString(key.getResId()));
    }*/

    public boolean contains(String key) {
        return sharedPreferences.contains(key);
    }

    public void remove(String key) {
        editor.remove(key);
        editor.commit();
    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }
}