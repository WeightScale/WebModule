package com.kostya.myapplication.preferences;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.preference.PreferenceActivity;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.widget.EditText;
import android.widget.Toast;

import com.kostya.myapplication.ActivityCalibration;
import com.kostya.myapplication.R;

import java.util.List;

/**
 * @author Kostya 29.10.2016.
 */
public class ActivityProperties extends PreferenceActivity {
    private EditText input;
    private static boolean active = false;

    @Override
    protected void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        active = false;
    }

    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.head, target);
    }

    @Override
    public void onHeaderClick(Header header, int position) {
        if (header.id == R.id.settingsGeneral) {
            startDialog(header);
        }else if (header.id == R.id.settingsNet){
            startPreferencePanel(FragmentSettingsNet.class.getName(), header.fragmentArguments, header.titleRes, header.title, null, 0);
        }else if (header.id == R.id.calibrator){
            startActivity(new Intent(getApplicationContext(), ActivityCalibration.class));
            //startDialog(header);
        }else if (header.id == R.id.closedHeader){
            finish();
        }
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        //return true;
        return FragmentSettingsNet.class.getName().equals(fragmentName) || FragmentSettingsGeneral.class.getName().equals(fragmentName) || FragmentCalibrator.class.getName().equals(fragmentName);
    }

    public static boolean isActive(){
        return active;
    }

    void startDialog(final Header header){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("ВВОД КОДА");
        input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        input.setTransformationMethod(PasswordTransformationMethod.getInstance());
        dialog.setView(input);
        dialog.setCancelable(false);
        dialog.setPositiveButton(getString(R.string.OK), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (input.getText() != null) {
                    boolean key = false;
                    String string = input.getText().toString();
                    if (!string.isEmpty()){
                        try{
                            if ("343434".equals(string))
                                key = true;
                            //else if (string.equals(ScalesView.getInstance().getScaleModule().getModuleServiceCod()))
                                //key = true;
                            if (key){
                                if (header.id == R.id.settingsGeneral)
                                    startPreferencePanel(FragmentSettingsGeneral.class.getName(), header.fragmentArguments, header.titleRes, header.title, null, 0);
                                else if(header.id == R.id.calibrator)
                                    startPreferencePanel(FragmentCalibrator.class.getName(), header.fragmentArguments, header.titleRes, header.title, null, 0);
                                finish();
                            }else {
                                Toast.makeText(getApplicationContext(), R.string.error_code, Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception e){}
                    }
                }
                dialogInterface.dismiss();
            }
        });
        dialog.setNegativeButton(getString(R.string.Close), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.setMessage("Введи код доступа к административным настройкам");
        dialog.show();
    }
}
