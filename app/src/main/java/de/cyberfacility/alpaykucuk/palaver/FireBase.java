package de.cyberfacility.alpaykucuk.palaver;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FireBase extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
      Log.d("Push",remoteMessage.getData().toString());
      createNotificationChannel();

        NotificationCompat.Builder notificationBuilder=
                new NotificationCompat.Builder(this,"id")
                .setContentTitle(remoteMessage.getData().get("sender")+" "+remoteMessage.getData().get("preview"))
                .setContentText(remoteMessage.getData().get("text"))
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());

        versendeBroadcast();

    }


    public void versendeBroadcast() {
        Intent intent = new Intent(); // Neuen Intent anlegen

        intent.setAction("de.cyberfacility.alpaykucuk.palaver");
        //intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        System.out.println("Broadscast versendet!");
        sendBroadcast(intent); // Intent mit Broadcast-Nachricht an alle Receiver verschicken
    }


    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            String name="Channel";
            String description="Description";
            int importance=NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("id",name,importance);
            channel.setDescription(description);

            NotificationManager notificationManager=getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }







    }







}
