package com.appdev.redhelm321.chat_room;


import android.os.Handler;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.appdev.redhelm321.MainActivity;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class Client extends Thread {

    String serverIP;
    Socket clientSocket;

    DataOutputStream dos;
    DataInputStream dis;

    Handler handler;
    AppCompatActivity sourceActivity;

    ListView lvMessageList;
    ArrayList<String> messages;


    public Client(AppCompatActivity sourceActivity, String serverIP, ListView lvMessageList, ArrayList<String> messages) {
        this.serverIP = serverIP;
        this.handler = new Handler();
        this.lvMessageList = lvMessageList;
        this.messages = messages;
        this.sourceActivity = sourceActivity;
    }

    private void receiveMessage(String message) {
        updateMessageUI(message);
    }

    private void updateMessageUI(String message) {
        messages.add(message);
        lvMessageList.setAdapter(new ArrayAdapter<String>(
                sourceActivity,
                android.R.layout.simple_list_item_1,
                messages));
    }


    @Override
    public void run() {
        try {
            clientSocket = new Socket(serverIP, MainActivity.IP_PORT);
            dos = new DataOutputStream(clientSocket.getOutputStream());
            dis = new DataInputStream(clientSocket.getInputStream());

            String msg;
            while ((msg = dis.readUTF()) != null) {
                String finalMsg = msg;
                handler.post(() -> receiveMessage(finalMsg));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessage(String message) {
        new Thread(() -> {
            try {
                if (dos != null) {
                    dos.writeUTF(message);
                    dos.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
                handler.post(() -> Toast.makeText(sourceActivity, "Error sending message", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }


}