package de.cyberfacility.alpaykucuk.palaver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationReciever extends BroadcastReceiver {

    chatActivity currChatActivity;

    @Override
    public void onReceive(Context context, Intent intent) {
        currChatActivity.refreshList();
    }
}


