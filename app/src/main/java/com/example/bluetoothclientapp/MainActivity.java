package com.example.bluetoothclientapp;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    Button buttonON, buttonOFF, buttonShow, buttonScan, buttonEnableDevice;
    BluetoothAdapter myBluetoothAdapter;
    Intent btEnabelingIntent;
    int requestCodeForEnable;
    ListView deviceList;
    ArrayList<String> stringArrayList = new ArrayList<String>();
    ArrayAdapter<String> arrayAdapter;
    IntentFilter intentFilter;
    //
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                stringArrayList.add(device.getName());
                //
                arrayAdapter.notifyDataSetChanged();
            }
        }
    };

    //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonON = findViewById(R.id.btON);
        buttonOFF = findViewById(R.id.btOFF);
        buttonShow = findViewById(R.id.showDevices);
        buttonScan = findViewById(R.id.scanDevices);
        buttonEnableDevice = findViewById(R.id.enableDevice);
        deviceList = findViewById(R.id.deviceList);
        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        btEnabelingIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        requestCodeForEnable = 1;
        arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, stringArrayList);
        deviceList.setAdapter(arrayAdapter);
        //
        registerReceiver(broadcastReceiver, intentFilter);

        checkBluetoothOn();
        bluetoothOff();
        showDevices();
        scanForDevices();
        enableDeviceBluetooth();
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == requestCodeForEnable) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(getApplicationContext(), "Bluetooth is enabled", Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "Bluetooth Enabling cancelled", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void showDevices() {
        buttonShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Set<BluetoothDevice> bt = myBluetoothAdapter.getBondedDevices();
                //
                stringArrayList = new ArrayList<String>();
                if (bt.size() > 0) {
                    for (BluetoothDevice device : bt) {
                        stringArrayList.add(device.getName());
                    }
                }
                arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, stringArrayList);
                deviceList.setAdapter(arrayAdapter);
            }
        });
    }

    private void scanForDevices() {
        buttonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myBluetoothAdapter.startDiscovery();
            }
        });
    }

    private void bluetoothOff() {
        buttonOFF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myBluetoothAdapter.isEnabled()) {
                    myBluetoothAdapter.disable();
                }
            }
        });
    }


    private void checkBluetoothOn() {
        buttonON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myBluetoothAdapter == null) {
                    //Bluetooth is not supported
                    Toast.makeText(getApplicationContext(), "Device doesnot support Bluetooth", Toast.LENGTH_LONG).show();
                } else {
                    if (!myBluetoothAdapter.isEnabled()) {
                        startActivityForResult(btEnabelingIntent, requestCodeForEnable);
                    }
                }
            }
        });
    }

    private void enableDeviceBluetooth() {
        buttonEnableDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 10);
                startActivity(intent);
            }
        });
    }
}
