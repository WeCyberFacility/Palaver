package de.cyberfacility.alpaykucuk.palaver;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
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


        System.out.println("Sender: " + remoteMessage.getData().get("sender"));
        System.out.println("Preview: " + remoteMessage.getData().get("preview"));
        System.out.println("Text: " + remoteMessage.getData().get("text"));

      Log.d("Push",remoteMessage.getData().toString());
      createNotificationChannel();

        int requestID = (int) System.currentTimeMillis();
        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
        chatActivity.currentEmpfänger = findEmpfängerInList(remoteMessage.getData().get("sender"));

        Intent notificationIntentChat = new Intent(getApplicationContext(), chatActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, requestID,notificationIntentChat, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Action action = new NotificationCompat.Action.Builder(R.drawable.common_google_signin_btn_icon_dark, "Previous", contentIntent).build();
        NotificationCompat.Builder notificationBuilder=
                new NotificationCompat.Builder(this,"id")
                        //TODO: preview um eine nach rechts verschieben!!!
                //.setContentTitle(remoteMessage.getData().get("sender")+" "+remoteMessage.getData().get("preview"))
                //.setContentText(remoteMessage.getData().get("text"))
                        .setContentText(remoteMessage.getData().get("preview").substring(1))
                        .setContentTitle(remoteMessage.getData().get("sender"))
                .setSmallIcon(R.mipmap.launcherlogo)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(contentIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(67, notificationBuilder.build());

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


    public Nutzer findEmpfängerInList(String sender) {

        Nutzer senderNutzer = MainScreenActivity.currentNutzer.searchFreundInListe(sender);

        return senderNutzer;

    }






}
