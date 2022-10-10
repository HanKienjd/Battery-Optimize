package appbattery.cobalt.com.batteryapp;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Debug;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import appbattery.cobalt.com.batteryapp.receiver.batInfoReceiver;

public class MainActivity<startTime> extends AppCompatActivity {
    //TODO find a new way to make these available to Broadcast Reciever as this will cause memory leaks

    batInfoReceiver batInfo = new batInfoReceiver();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Debug.startMethodTracing("awesometrackview");
        setContentView(R.layout.activity_main);
        batInfo.batteryLevel = findViewById(R.id.textView1);
        batInfo.voltLevel = findViewById(R.id.textView2);
        batInfo.tempLevel = findViewById(R.id.textView3);
        batInfo.chargingCurrent = findViewById(R.id.textView5);
        batInfo.chargingState = findViewById(R.id.textView4);
        batInfo.pbar = findViewById(R.id.progressBar1);
        showNetworkInfoToast();
    }

    private void enableBatInfoReceiver(boolean enable) {
        PackageManager pm = getPackageManager();
        ComponentName receiver = new ComponentName(this, batInfoReceiver.class);
        int newState = enable ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED : PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
        pm.setComponentEnabledSetting(receiver, newState, PackageManager.DONT_KILL_APP);
    }

    private void showNetworkInfoToast() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        // to show only the active connection
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if(activeNetwork != null) {
            Toast.makeText(this, activeNetwork.getTypeName(), Toast.LENGTH_SHORT).show();
            if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                Toast.makeText(this, "Wifi", Toast.LENGTH_SHORT).show();
                // connected to wifi
            } else if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                Toast.makeText(this, "Mobile Data", Toast.LENGTH_SHORT).show();
                // connected to the mobile provider's data plan
            }
        } else {
            Toast.makeText(this, "No Network", Toast.LENGTH_SHORT).show();
            // not connected to the internet
        }
        NetworkInfo[] allNetworks = cm.getAllNetworkInfo();
        for(NetworkInfo networkInfo : allNetworks) {
            Toast.makeText(this, networkInfo.getTypeName(), Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(batInfo,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        registerReceiver(batInfo,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(batInfo);
        enableBatInfoReceiver(false);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Debug.stopMethodTracing();
//        unregisterReceiver(batInfo);


    }

 }
