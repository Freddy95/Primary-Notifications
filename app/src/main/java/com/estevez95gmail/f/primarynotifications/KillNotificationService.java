package com.estevez95gmail.f.primarynotifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by Freddy Estevez on 1/14/16.
 */
public class KillNotificationService extends Service {
    IBinder mBinder;
    NotificationManager nm;
    int NOTIFICATION_ID = AlarmReceiver.id;
    public class KillBinder extends Binder {
        public final Service service;

        public KillBinder(Service service) {
            this.service = service;
        }

    }

    public IBinder onBind(Intent intent){
        return mBinder = new KillBinder(this);
    }

    public void onTaskRemoved(Intent rootIntent){
        Log.d("task", "Task removed");
        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.cancel(NOTIFICATION_ID);
    }
}
