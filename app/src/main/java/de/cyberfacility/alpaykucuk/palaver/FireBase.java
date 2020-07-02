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
import com.preference.PowerPreference;

public class FireBase extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {


        //DEBUGGING:
        /*System.out.println("Sender: " + remoteMessage.getData().get("sender"));
        System.out.println("Preview: " + remoteMessage.getData().get("preview"));
        System.out.println("Text: " + remoteMessage.getData().get("text"));
        */

      Log.d("Push",remoteMessage.getData().toString());
      notificationChannelErstellen();

        int requestID = (int) System.currentTimeMillis();
        chatActivity.currentEmpfänger = findEmpfängerInList(remoteMessage.getData().get("sender"));

        Intent notificationIntentChat = new Intent(getApplicationContext(), chatActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, requestID,notificationIntentChat, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder newNotificationBuilder=
                new NotificationCompat.Builder(this,"id")
                        .setContentText(remoteMessage.getData().get("preview").substring(1))
                        .setContentTitle(remoteMessage.getData().get("sender"))
                .setSmallIcon(R.mipmap.launcherlogo)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(contentIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(67, newNotificationBuilder.build());

        versendeBroadcast();

    }


    public void versendeBroadcast() {
        Intent intent = new Intent(); // Neuen Intent anlegen
        intent.setAction("de.cyberfacility.alpaykucuk.palaver");
        System.out.println("Broadscast versendet!");
        sendBroadcast(intent); // Intent mit Broadcast-Nachricht an alle Receiver verschicken
    }


    private void notificationChannelErstellen(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            String name="Channel";
            String beschreibung="Description";
            int prio =NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel newNotChannel = new NotificationChannel("id",name,prio);
            newNotChannel.setDescription(beschreibung);

            NotificationManager notManager =getSystemService(NotificationManager.class);
            notManager.createNotificationChannel(newNotChannel);

        }







    }


    public Nutzer findEmpfängerInList(String sender) {

        getLoggedNutzer();
        Nutzer senderNutzer = MainScreenActivity.currentNutzer.searchFreundInListe(sender);

        return senderNutzer;

    }



    public void getLoggedNutzer() {
        Nutzer offlineNutzer = PowerPreference.getFileByName("Offline").getObject("OfflineNutzer", Nutzer.class);
        MainScreenActivity.currentNutzer = offlineNutzer;
        System.out.println("Beweis: " + offlineNutzer.getNutzername());

    }




}
