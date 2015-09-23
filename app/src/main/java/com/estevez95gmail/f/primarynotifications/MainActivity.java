package com.estevez95gmail.f.primarynotifications;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    //NUMBER OF INCOMING CALL
    String number;
    //HOLDS PROFILES USER CREATES
    ArrayList<Profile> profiles;
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
                    Toast.makeText(getApplicationContext(), "Phone Is Ringing " + number, Toast.LENGTH_LONG).show();
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
        //add profile
        if(id == R.id.action_addProf){
            addProfile();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    /*
     * ALLOWS USER TO CREATE NEW PROFILE
     */
    public void addProfile(){
        Intent add = new Intent(this, AddProfileActivity.class);
        startActivity(add);
    }

    @Override
    public void onPause(){
        super.onPause();
        Log.d("mainActivity", "onPause");
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.d("mainActivity", "onDestroy");
    }



}
