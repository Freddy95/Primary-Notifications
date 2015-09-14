package com.estevez95gmail.f.primarynotifications;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    String number;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TelephonyManager manager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        PhoneStateListener callStateListener = new PhoneStateListener() {
            public void onCallStateChanged(int state, String incomingNumber) {
                //  React to incoming call.
                number = incomingNumber;
                // If phone ringing
                if (state == TelephonyManager.CALL_STATE_RINGING) {
                    Toast.makeText(getApplicationContext(), "Phone Is Ringing", Toast.LENGTH_LONG).show();
                }
            }
        };
        manager.listen(callStateListener,PhoneStateListener.LISTEN_CALL_STATE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void addProfile(View v){
        Toast.makeText(getBaseContext(), "Hello :) "+ Toast.LENGTH_LONG, Toast.LENGTH_LONG).show();

    }

    @Override
    public void onPause(){
        super.onPause();
        Log.d("CLASS_NAME", "onPause");
    }


}
