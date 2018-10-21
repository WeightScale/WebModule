package com.kostya.myapplication.preferences;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.preference.ListPreference;
import android.provider.Settings;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListAdapter;


import com.kostya.myapplication.R;

import java.util.List;

/**
 * Created by Kostya on 26.06.2016.
 */
public class ListPreferenceWifi extends ListPreference {
    WifiManager wifiManager;
    private int mClickedDialogEntryIndex;
    List<WifiConfiguration> list;
    List<ScanResult> scanResultList;


    public ListPreferenceWifi(Context context, AttributeSet attrs) {
        super(context, attrs);
        //WifiConfiguration wifiConfiguration = new WifiConfiguration();
        wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        list = wifiManager.getConfiguredNetworks();

        /*entries = new CharSequence[list.size()];
        entryValues = new CharSequence[list.size()];
        int i = 0;
        for (WifiConfiguration wifiConfiguration : list){
            entries[i] = wifiConfiguration.SSID;
            entryValues[i] = String.valueOf(wifiConfiguration.networkId);
            i++;
        }
        setEntries(entries);
        setEntryValues(entryValues);*/
        setPersistent(true);

        mClickedDialogEntryIndex = getPersistedInt(0);
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        int value = restoreValue? getPersistedInt(mClickedDialogEntryIndex) : (Integer) defaultValue;
        setValue(value);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
       if (positiveResult && mClickedDialogEntryIndex >= 0 /*&& entryValues != null*/) {
           ScanResult value = scanResultList.get(mClickedDialogEntryIndex);
            if (callChangeListener(value)) {
                setValue(mClickedDialogEntryIndex);
            }
        }
    }

    public void setValue(int value) {
        if (shouldPersist()) {
            persistInt(value);
        }
        if (value != mClickedDialogEntryIndex) {
            mClickedDialogEntryIndex = value;
            notifyChanged();
        }
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
    }

    @Override
    protected void onPrepareDialogBuilder( AlertDialog.Builder builder ){
        scanResultList = wifiManager.getScanResults();
        final ListAdapter adapter = new ConfigurationAdapter(getContext(), R.layout.item_list_sender, scanResultList);

        builder.setSingleChoiceItems(adapter, mClickedDialogEntryIndex, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which ){
                long l = adapter.getItemId( which );
                setValue(which);

                    /*if (mClickedDialogEntryIndex != which) {
                        mClickedDialogEntryIndex = which;
                        if (shouldPersist()) {
                            persistInt(mClickedDialogEntryIndex);
                        }
                        ListPreferenceWifi.this.notifyChanged();
                    }*/
                ListPreferenceWifi.this.onClick(dialog, DialogInterface.BUTTON_POSITIVE);

                dialog.dismiss();
            }
        } );

        builder.setPositiveButton("Настроить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                getContext().startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                dialogInterface.dismiss();
            }
        });

        //setDefaultValue(mClickedDialogEntryIndex);
    }

    class ConfigurationAdapter extends ArrayAdapter<ScanResult> {

        public ConfigurationAdapter(Context context, int resource, List<ScanResult> list) {
            super(context, resource, list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;

            if (view == null) {
                LayoutInflater layoutInflater = LayoutInflater.from(getContext());
                view = layoutInflater.inflate(R.layout.item_list_sender, parent, false);
            }

            ScanResult p = getItem(position);
            CheckedTextView textView = (CheckedTextView) view.findViewById(R.id.text1);
            textView.setText(p.SSID.replace("\"",""));
            //WifiConfiguration wc = list.get(mClickedDialogEntryIndex);
            //ScanResult wc = scanResultList.get(mClickedDialogEntryIndex);
            /*if (wc.networkId == p.networkId){
                textView.setTextColor(Color.BLUE);
            }else {
                textView.setTextColor(Color.BLACK);
            }*/

            /*for (ScanResult scanResult : scanResultList){
                if (scanResult.SSID.equals(wc.SSID.replace("\"",""))){
                    textView.setBackgroundColor(Color.GRAY);
                }else {
                    textView.setBackgroundColor(Color.WHITE);
                }
            }*/

            return view;
        }
    }
}
