package com.dev.goldilocksdatacollection;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.util.Log;

import androidx.annotation.RequiresPermission;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.UUID;

public class Bluetooth {

    private Context context;
    private Uploader uploader;

    private String TAG = "DEBUG";
    private int count = 0;
    private int reconnectCounter = 0;
    public boolean hasConnected = false;
    public boolean deviceConnectedToSuit = false;

    public String activity = "unknown";

    private BluetoothGatt gatt;
    private String deviceMACAdd = "";

    private final static UUID BATTERY_UUID = UUID.fromString("0000180f-0000-1000-8000-00805f9b34fb");
    private final static UUID BATTERY_LEVEL = UUID.fromString("00002a19-0000-1000-8000-00805f9b34fb");


    @SuppressLint("MissingPermission")
    public Bluetooth(Context context, BluetoothDevice device){
        this.context = context;
        this.uploader = new Uploader();
        this.deviceMACAdd = device.getAddress();

        gatt = device.connectGatt(context, true, gattCallback);
    }


    private final BluetoothGattCallback gattCallback =
            new BluetoothGattCallback() {
                @Override
                @RequiresPermission(value = "android.permission.BLUETOOTH_CONNECT")
                public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                    count = 0;
                    Log.e(TAG, String.format("Connection State Changed - New State: %d", newState) + "; " + Boolean.toString(BluetoothProfile.STATE_CONNECTED == newState));
                    if (newState == BluetoothProfile.STATE_CONNECTED) {
                        hasConnected = true;
                        gatt.discoverServices();
                    }
                    else if (newState == BluetoothProfile.STATE_DISCONNECTED && hasConnected) {
                        Log.e(TAG, "Disconnected from GATT server.");
                        hasConnected = false;
                        BluetoothDevice device = gatt.getDevice();
                        reconnectCounter = 0;
                    }
                }

                @Override
                @RequiresPermission(value = "android.permission.BLUETOOTH_CONNECT")
                // New services discovered
                public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                    Log.e(TAG, "Services Discovered");

                    String logString = "";

                    for(BluetoothGattService services : gatt.getServices()){

                        // Battery level hopefully
                        if (services.getUuid().equals(BATTERY_UUID)) {
                            Log.d(TAG, String.valueOf(services.getUuid()));

                            BluetoothGattCharacteristic characteristic = services.getCharacteristic(BATTERY_LEVEL);

                            if (characteristic != null) {
                                gatt.readCharacteristic(characteristic);
                            }
                        }

                        // Everything else
                        logString += "\n" + services.getUuid().toString() + ":";
                        for(BluetoothGattCharacteristic characteristics : services.getCharacteristics()){
                            logString += characteristics.getUuid().toString() + ",";
                        }
                        logString += "\n";
                    }
                    Log.e(TAG, logString);

                    // Services - Thermistor and Accelerometer
                    UUID thermService = UUID.fromString("9ba97d45-f647-4571-94da-defc060d8728");

                    // Thermistor1Notification
                    enableNotification(thermService, UUID.fromString("1400c9df-b670-473e-b38d-3c07fd22eec2"), gatt);
                }

                @Override
                @RequiresPermission(value = "android.permission.BLUETOOTH_CONNECT")
                public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status){
                    count++;
                    Log.e(TAG, "Writing Start Streaming Data Command");

                    UUID thermService = UUID.fromString("9ba97d45-f647-4571-94da-defc060d8728");
                    UUID accelService = UUID.fromString("0c28c057-2580-42a0-b61b-267458ae0d0b");

                    // Thermistor2Notification
                    if(count == 1) enableNotification(thermService, UUID.fromString("43530b19-ef9f-4ed6-bf44-474a92b1de32"), gatt);

                    // AccelXNotification
                    if(count == 2) enableNotification(accelService, UUID.fromString("c6f4a776-636f-48d6-97bd-8fc5c06a2e5a"), gatt);
                    // AccelYNotification
                    if(count == 3) enableNotification(accelService, UUID.fromString("854ed786-e054-410f-aea2-610c948f85c0"), gatt);
                    // AccelZNotification
                    if(count == 4) enableNotification(accelService, UUID.fromString("d9e6a330-1c73-4fdc-a884-c150fc218bb1"), gatt);

                    // Battery voltage
                    if(count == 5) {
                        enableNotification(UUID.fromString("4d7d4ea7-4f5d-4584-a256-e9b0549f3d38"), UUID.fromString("43b59fd1-91d5-4b0e-932b-72dfb13ee1e7"), gatt);
                    }
                }

                @Override
                public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                    Log.e(TAG, "Data Received From: " + characteristic.getUuid().toString());

                    StringBuilder stringBuilder = new StringBuilder();
                    byte[] data =  characteristic.getValue();
                    for(byte byteChar : data){
                        stringBuilder.append(String.format("%02X ", byteChar));
                    }
                    String dataStream = stringBuilder.toString().trim();

                    Log.e(TAG, "We can now process data");
                    processData(dataStream, characteristic.getUuid());
                }

                @Override
                public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                    Log.e(TAG, "Read - Data Received!" + characteristic.getStringValue(0));
                    super.onCharacteristicRead(gatt, characteristic, status);

                    final Integer batteryLevel = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);

                    if (batteryLevel != null) {
                        Log.d(TAG, "battery level: " + batteryLevel);
                    }
                }
            };

    @RequiresPermission(value = "android.permission.BLUETOOTH_CONNECT")
    private void enableNotification(UUID servUUID, UUID charUUID, BluetoothGatt gatt){
        try {
            BluetoothGattCharacteristic characteristic = gatt.getService(servUUID).getCharacteristic(charUUID);
            Log.e("Smart Vest", "-----\n" + charUUID.toString() + ":\nCreated Characteristic");
            Log.e(TAG, Boolean.toString(gatt.setCharacteristicNotification(characteristic, true)));

            int i = 0;
            for (BluetoothGattDescriptor descriptor : characteristic.getDescriptors()) {
                Log.e(TAG, "Descriptor for " + characteristic.getUuid().toString() + ":" + descriptor.getUuid().toString());
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                gatt.writeDescriptor(descriptor);
                i++;
            }
        }
        catch(Exception e){
            Log.e(TAG, e.getMessage());
            Log.e("Smart Vest", "Failed to enable notifications for " + servUUID.toString() + "," + charUUID.toString());
        }
    }

    private String temp1 = "";
    private String temp2 = "";
    private String x = "";
    private String y = "";
    private String z = "";
    private String gyrox = "";
    private String gyroy = "";
    private String gyroz = "";
    private int zCounter = 0;


    private void processData(String dataStream, UUID id) {
        Integer[] parsedData = new Integer[0];
        String dataType = "";
        if (id.toString().compareTo("1400c9df-b670-473e-b38d-3c07fd22eec2") == 0) {
            temp1 += dataStream + " ";
            if(!temp1.contains("FF FF FF FF")){
                if(dataStream.contains("1F 62 1F 62 1F 62")){
                    deviceConnectedToSuit = false;
                }
                else
                    deviceConnectedToSuit = true;
                Log.e("DEBUG", "Device connected to suit: " + Boolean.toString(deviceConnectedToSuit));
            }
        } else if (id.toString().compareTo("43530b19-ef9f-4ed6-bf44-474a92b1de32") == 0) {
            temp2 += dataStream + " ";
        } else if (id.toString().compareTo("9a261321-9087-49b8-8218-76fd407859a0") == 0) {
            gyrox += dataStream + " ";
        } else if (id.toString().compareTo("b59b673d-190b-4955-ba41-b9194728dd25") == 0) {
            gyroy += dataStream + " ";
        } else if (id.toString().compareTo("4afbc1ce-93d6-4fc5-b812-99ff0187ce48") == 0) {
            gyroz += dataStream + " ";
        } else if (id.toString().compareTo("c6f4a776-636f-48d6-97bd-8fc5c06a2e5a") == 0) {
            x += dataStream + " ";
        } else if (id.toString().compareTo("854ed786-e054-410f-aea2-610c948f85c0") == 0) {
            y += dataStream + " ";
        } else if (id.toString().compareTo("d9e6a330-1c73-4fdc-a884-c150fc218bb1") == 0) {
            z += dataStream + " ";
            zCounter++;
            if(zCounter == 3){
                try{
                    zCounter = 0;
//                    if(deviceConnectedToSuit){
                    uploadData(activity);
//                    }
                } catch(JSONException e){
                    Log.e(TAG, e.getMessage());
                } catch(IOException e){
                    Log.e(TAG, e.getMessage());
                }
            }
        }
    }


    private void uploadData(String tag) throws JSONException, IOException {
        if(temp2.contains("FF FF FF FF FF") || x.contains("FF FF FF FF FF")){
            x = "";
            y = "";
            z = "";
            gyrox = "";
            gyroy = "";
            gyroz = "";
            temp1 = "";
            temp2 = "";
            return;
        }

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        JSONObject obj = new JSONObject();
        obj.put("id", android.os.Build.MODEL + "-" + deviceMACAdd);
        obj.put("time", timestamp.toString());
        obj.put("tags", tag);
        obj.put("x", x);
        obj.put("y", y);
        obj.put("z", z);
        obj.put("gyro_x", gyrox);
        obj.put("gyro_y", gyroy);
        obj.put("gyro_z", gyroz);
        obj.put("temp1", temp1);
        obj.put("temp2", temp2);

        uploader.postData(obj, "forward/");
        x = "";
        y = "";
        z = "";
        gyrox = "";
        gyroy = "";
        gyroz = "";
        temp1 = "";
        temp2 = "";
    }

    @SuppressLint("MissingPermission")
    public void endConnection(){
        gatt.close();
    }
}
