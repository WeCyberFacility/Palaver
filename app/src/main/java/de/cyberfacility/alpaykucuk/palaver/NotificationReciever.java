package de.cyberfacility.alpaykucuk.palaver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class NotificationReciever extends BroadcastReceiver {

    chatActivity currChatActivity;

    @Override
    public void onReceive(Context context, Intent intent) {


        currChatActivity.refreshList();
        Toast.makeText(context, "Notification Clicked!", Toast.LENGTH_SHORT).show();
    }
}


