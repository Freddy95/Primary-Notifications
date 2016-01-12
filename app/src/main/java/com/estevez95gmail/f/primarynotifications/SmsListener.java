package com.estevez95gmail.f.primarynotifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

/**
 * Created by Freddy Estevez on 1/3/16.
 */
public class SmsListener extends BroadcastReceiver {
   static  SmsListener  listener;


    final SmsManager sms = SmsManager.getDefault();


    public void onReceive(Context context, Intent intent) {

        // Retrieves a map of extended data from the intent.
        final Bundle bundle = intent.getExtras();

        try {

            if (bundle != null) {

                final Object[] pdusObj = (Object[]) bundle.get("pdus");

                for (int i = 0; i < pdusObj.length; i++) {
                    SmsMessage currentMessage;
                    if(Build.VERSION.SDK_INT < 23)
                        currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    else
                        currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i], intent.getStringExtra("format"));

                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                    Log.d("RECIEVED SMS", "Recieved sms");

                    MainActivity.checkToRing(phoneNumber, false);

                } // end for loop
            } // bundle is null

        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver " + e);

        }
    }
}
