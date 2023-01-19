package com.dev.goldilocksdatacollection;

import android.bluetooth.BluetoothDevice;

import java.util.ArrayList;
import java.util.List;

public class Mediator {

    private List<BluetoothDevice> devices = new ArrayList<BluetoothDevice>();

    public Mediator(){}

    public int addDevice(BluetoothDevice device){
        devices.add(device);
        return devices.size() - 1;
    }

    public BluetoothDevice getDevice(int index){
        return devices.get(index);
    }
}
