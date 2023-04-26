package com.example.proyecto2.Services;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ServicioFirebase extends FirebaseMessagingService {

    private String tokenUsuario;

    public ServicioFirebase() {
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);

        try {
            File file = new File(getFilesDir(), "token.txt");
            FileWriter writer = new FileWriter(file);
            writer.write(s);
            writer.flush();
            writer.close();
        } catch (IOException e) {
        }
    }

    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
        }
        if (remoteMessage.getNotification() != null) {
        }
    }

}
