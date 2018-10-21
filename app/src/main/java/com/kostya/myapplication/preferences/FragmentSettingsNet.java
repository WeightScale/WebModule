package com.kostya.myapplication.preferences;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.kostya.myapplication.R;

import java.util.List;

public class FragmentSettingsNet extends PreferenceFragment {

    public enum KEY{
        STATIC_IP(R.string.KEY_STATIC_IP){
            @Override
            void setup(Preference name) throws Exception {
                final Context mContext = name.getContext();
                name.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object o) {
                        return true;
                    }
                });
            }
        },
        IP(R.string.KEY_IP){
            @Override
            void setup(Preference name) throws Exception {
                final CharSequence title = name.getTitle();
                name.setTitle(title + " " + name.getSharedPreferences().getString(name.getKey(), "192.168.1.100") );
                name.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object o) {
                        preference.setTitle(title + " " + o);
                        return true;
                    }
                });
            }
        },
        MASK(R.string.KEY_MASK){
            @Override
            void setup(Preference name) throws Exception {
                final CharSequence title = name.getTitle();
                name.setTitle(title + " " + name.getSharedPreferences().getString(name.getKey(), "255.255.1.1") );
                name.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object o) {
                        preference.setTitle(title + " " + o);
                        return true;
                    }
                });
            }
        },
        GATEWAY(R.string.KEY_GATEWAY){
            @Override
            void setup(Preference name) throws Exception {
                final CharSequence title = name.getTitle();
                name.setTitle(title + " " + name.getSharedPreferences().getString(name.getKey(), "192.168.1.1") );
                name.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object o) {
                        preference.setTitle(title + " " + o);
                        return true;
                    }
                });
            }
        },
        NET(R.string.KEY_NET){
            @Override
            void setup(Preference name) throws Exception {
                final Context mContext = name.getContext();
                try {
                    name.setTitle("ИМЯ СЕТИ: " + name.getSharedPreferences().getString(name.getKey(), "scales") );
                }catch (Exception e){}
                //name.setSummary("Сеть по умолчанию. Для выбора конкретной сети из списка кофигураций если есть.");
                name.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object o) {
                        if (o.toString().isEmpty()) {
                            Toast.makeText(mContext, "Ошибка", Toast.LENGTH_SHORT).show();
                            name.setTitle("ИМЯ СЕТИ: " + "???");
                            return false;
                        }
                        String netName = ((ScanResult)o).SSID.replace("\"","");
                        //String netId = String.valueOf(((WifiConfiguration)o).networkId);
                        name.setTitle("ИМЯ СЕТИ: " + netName);
                        return true;
                    }
                });
            }

            String getNameOfId(Context context, int id){
                List<WifiConfiguration> list = ((WifiManager)context.getApplicationContext().getSystemService(Context.WIFI_SERVICE)).getConfiguredNetworks();
                for (WifiConfiguration wifiConfiguration : list){
                    if (wifiConfiguration.networkId == id){
                        return  wifiConfiguration.SSID.replace("\"", "");
                    }
                }
                return "";
            }
        },
        HOST(R.string.KEY_HOST){
            @Override
            void setup(Preference name) throws Exception {
                final Context context = name.getContext();
                final CharSequence title = name.getTitle();
                name.setTitle(title + " " + name.getSharedPreferences().getString(name.getKey(), "scales"));
                name.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object o) {
                        preference.setTitle(title + " " + o);
                        return true;
                    }
                });
            }
        };
        private final int resId;
        abstract void setup(Preference name)throws Exception;

        KEY(int key){
            resId = key;
        }
        public int getResId() { return resId; }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings_net);
        PreferenceManager.setDefaultValues(getActivity(), R.xml.settings_net, false);
        //settings = new Settings(getActivity(), ScalesView.SETTINGS);
        //scalesView = ScalesView.getInstance();
        initPreferences();
    }

    public void initPreferences(){
        for (KEY enumPreference : KEY.values()){
            Preference preference = findPreference(getString(enumPreference.getResId()));
            if(preference != null){
                try {
                    enumPreference.setup(preference);
                } catch (Exception e) {
                    preference.setEnabled(false);
                }
            }
        }
    }
}
