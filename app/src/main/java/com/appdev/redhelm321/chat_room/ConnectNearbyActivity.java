package com.appdev.redhelm321.chat_room;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.appdev.redhelm321.MainActivity;
import com.appdev.redhelm321.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConnectNearbyActivity extends AppCompatActivity {

    ArrayList<String> discoveredDevicesNames;
    ArrayList<WifiP2pDevice> discoveredDevices;

    ListView lv_discoveredDeviceList;
    Button btn_scanDevices;
    TextView tv_connectionStatus;
    Button btn_sendMessage;

    BroadcastReceiver broadcastReceiver;
    IntentFilter intentFilter;
    WifiManager wifiManager;
    WifiP2pManager wifiP2pManager;
    WifiP2pManager.Channel wifiP2pChannel;
    WifiP2pManager.PeerListListener peerListListener;
    WifiP2pManager.ConnectionInfoListener connectionInfoListener;


    ServerClass serverClass;
    ClientClass clientClass;
    Socket socket;

    boolean isHost;

    static final int MESSAGE_READ = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_connect_nearby);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        InitializeComponents();
    }

    private void InitializeComponents() {

        wifiP2pManager = WIFI_P2P_SharedData.getWifiP2pManager();
        wifiP2pChannel = WIFI_P2P_SharedData.getWifiP2pChannel();
        broadcastReceiver = WIFI_P2P_SharedData.getBroadcastReceiver();
        intentFilter = WIFI_P2P_SharedData.getIntentFilter();

        lv_discoveredDeviceList = findViewById(R.id.lv_discoveredDeviceList);
        btn_sendMessage = findViewById(R.id.btn_sendMessage);
        btn_scanDevices = findViewById(R.id.btn_scanDevices);
        tv_connectionStatus = findViewById(R.id.tv_connectionStatus);
    }

    private void btn_scanDevices_OnClick() {
    }


    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    //SERVER CLASS
    public class ServerClass extends Thread {
        ServerSocket serverSocket;
        InputStream inputStream;
        OutputStream outputStream;

        public void write(byte[] bytes) {
            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(MainActivity.IP_PORT);
                socket = serverSocket.accept();
                inputStream = socket.getInputStream();
                outputStream = socket.getOutputStream();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            ExecutorService executorService = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());

            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    byte[] buffer = new byte[1024];
                    int bytes;

                    while (socket != null) {
                        try {
                            bytes = inputStream.read(buffer);
                            if(bytes > 0 ) {
                                int finalBytes = bytes;
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        String tempMsg = new String(buffer, 0, finalBytes);

//                                        final String newMessage = tv_messages.getText().toString() + "\n" + tempMsg;
//
//                                        tv_messages.setText(newMessage);

                                    }
                                });
                            }

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }

                }
            });

        }
    }

    // CLIENT CLASS
    public class ClientClass extends Thread {

        String hostAddress;
        InputStream inputStream;
        OutputStream outputStream;

        public ClientClass(String hostAddress) {
            this.hostAddress = hostAddress;
            socket = new Socket();
        }

        public void write(byte[] bytes) {
            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void run() {
            try {

                socket.connect(new InetSocketAddress(hostAddress, MainActivity.IP_PORT), 500);
                inputStream = socket.getInputStream();
                outputStream = socket.getOutputStream();

            } catch (IOException e) {

                throw new RuntimeException(e);
            }

            ExecutorService executorService = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());

            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    byte[] buffer = new byte[1024];
                    int bytes;

                    while (socket != null) {
                        try {
                            bytes = inputStream.read(buffer);
                            if(bytes > 0 ) {
                                int finalBytes = bytes;
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        String tempMsg = new String(buffer, 0, finalBytes);

//                                        final String newMessage = tv_messages.getText().toString() + "\n" + tempMsg;
//
//                                        tv_messages.setText(newMessage);

                                    }
                                });
                            }

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }

                }
            });
        }
    }
}