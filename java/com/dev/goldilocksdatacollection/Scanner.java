package com.dev.goldilocksdatacollection;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.util.Log;
import android.webkit.WebView;

import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

public class Scanner {

    private Context c;
    private WebView mWebView;
    private List<String> devices = new ArrayList<String>();
    private Mediator mediator;

    public Scanner(Context c, WebView mWebView, Mediator mediator){
        this.c = c;
        this.mWebView = mWebView;
        this.mediator = mediator;



        final BroadcastReceiver receiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                Log.e("DEBUG", "hello?");
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    // Discovery has found a device. Get the BluetoothDevice
                    // object and its info from the Intent.
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    Log.e("DEBUG", "Device has been found!");
                    Log.e("DEBUG", device.getName() + ", " + device.getBluetoothClass().getDeviceClass());
                    String deviceName = device.getName();
                    String deviceHardwareAddress = device.getAddress(); // MAC address



                    if(!devices.contains(deviceHardwareAddress) && deviceName != null) {
                        devices.add(deviceHardwareAddress);
                        int index = mediator.addDevice(intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE));
                        mWebView.loadUrl("javascript:addCard('" + Integer.toString(device.getBluetoothClass().getDeviceClass()) + "','" +
                                deviceName + "','" + deviceHardwareAddress + "','" + Integer.toString(index) + "')");
                        Log.e("DEBUG", deviceName + ", " + deviceHardwareAddress);
                    }
                }
            }
        };

        scanForDevices(receiver);
    };

    private BluetoothAdapter BTAdapter;

    public void scanForDevices(BroadcastReceiver receiver){
        Log.e("DEBUG", "test");
        if (ActivityCompat.checkSelfPermission(c, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            Log.e("DEBUG", "scanForDevices did not have permission");
//            return;
        }

        BTAdapter = BluetoothAdapter.getDefaultAdapter();
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        c.registerReceiver(receiver, filter);
        Log.e("DEBUG", "Starting to Scan");
        Log.e("DEBUG", Boolean.toString(BTAdapter.startDiscovery()));
    }

    @SuppressLint("MissingPermission")
    public void close(){
        BTAdapter.cancelDiscovery();
    }
}
