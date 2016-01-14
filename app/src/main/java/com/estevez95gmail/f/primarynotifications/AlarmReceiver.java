package com.estevez95gmail.f.primarynotifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import java.util.jar.Manifest;

/**
 * Created by Freddy Estevez on 1/13/16.
 */
public class AlarmReceiver extends BroadcastReceiver {
    int id = 1010101011;
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("ALARM", "ALARMING");
        if(Build.VERSION.SDK_INT >= 19)
             MainActivity.alarmManager.setExact(AlarmManager.RTC, System.currentTimeMillis()+ 60000, MainActivity.pIntent1);
        else
            MainActivity.alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 60000, MainActivity.pIntent1);
        boolean active = MainActivity.isActive();
        if(active && !MainActivity.notified) {
            MainActivity.notified = true;
            MainActivity.notificationManager.notify(id, MainActivity.notif);
            MainActivity.mutePhone();

        }
        else if(MainActivity.notified && !(active)){
            MainActivity.notificationManager.cancel(id);
            MainActivity.returnPhoneToState();

        }

    }
}
