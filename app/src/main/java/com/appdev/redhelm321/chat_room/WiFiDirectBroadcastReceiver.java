package com.appdev.redhelm321.chat_room;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.widget.Toast;

import com.appdev.redhelm321.MainActivity;

public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {
    WifiP2pManager wifiP2pManager;
    WifiP2pManager.Channel wifiP2pChannel;
    MainActivity context;

    public WiFiDirectBroadcastReceiver(WifiP2pManager wifiP2pManager, WifiP2pManager.Channel wifiP2pChannel, MainActivity context) {
        this.wifiP2pManager = wifiP2pManager;
        this.wifiP2pChannel = wifiP2pChannel;
        this.context = context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            wifiP2pStateChangeEvent(intent);
        }
        else if(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            wifiP2pPeersChangedEvent(intent);
        }
        else if(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            wifiP2pConnectionChangedEvent(intent);
        }
        else if(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            wifiP2pThisDeviceChangedEvent(intent);
        }
    }

    private void wifiP2pStateChangeEvent(Intent intent) {
        int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);

        if(state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
            Toast.makeText(context, "WiFi is ON", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(context, "WiFi is OFF", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("MissingPermission") // Ensured that the permission was already granted
    private void wifiP2pPeersChangedEvent(Intent intent) {
        if (wifiP2pManager != null) {
            wifiP2pManager.requestPeers(wifiP2pChannel, WIFI_P2P_SharedData.getPeerListListener());
        }
        else {
            Toast.makeText(context, "ERROR: wifiP2pManager IS NULL", Toast.LENGTH_SHORT).show();
        }
    }

    private void wifiP2pConnectionChangedEvent(Intent intent) {
        if (wifiP2pManager == null) return;

        NetworkInfo netInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

        if (netInfo.isConnected()) {
            wifiP2pManager.requestConnectionInfo(
                    wifiP2pChannel,
                    WIFI_P2P_SharedData.getConnectionInfoListener()
            );
        }
        else {
            Toast.makeText(context, "Disconnected", Toast.LENGTH_SHORT).show();
        }

    }


    private void wifiP2pThisDeviceChangedEvent(Intent intent) {
    }
}
