package com.estevez95gmail.f.primarynotifications;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.provider.ContactsContract;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.Manifest;
import android.widget.Toolbar;


public class MainActivity extends ListActivity {
    //NUMBER OF INCOMING CALL
    String number;
    //HOLDS PROFILES USER CREATES
    static ArrayList<Profile> profiles = new ArrayList<>();


    final private int REQUEST_PERMISSIONS = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("CHECK", "Does this work");


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ProfileAdapter adapter = new ProfileAdapter(getListView().getContext(), profiles);
        setListAdapter(adapter);



        try {
            TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
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
            manager.listen(callStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        } catch (RuntimeException e) {
            Toast.makeText(getBaseContext(), "Primary Notifications requires permissions", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        //add profile
        if (id == R.id.action_addProf) {
            addProfile();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    /**
     * ALLOWS USER TO CREATE NEW PROFILE
     */
    public void addProfile() {
        //CHECK WHICH VERSION OF ANDROID
        if (Build.VERSION.SDK_INT >= 23) {
            //IF ANDROID 6 OR GREATER MUST CHECK TO SEE IF WE HAVE PERMISSIONS
            if (checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                //WE HAVE PERMISSIONS ABLE TO ADD PROFILE
                Intent add = new Intent(this, AddProfileActivity.class);
                startActivity(add);
                return;
            } else {


                //DONT HAVE PERMISSIONS MUST REQUEST FROM USER
                //CHECK PERMISSIONS WE DO HAVE THEN ASK FOR ALL NEEDED PERMISSIONS
                checkCurrentPermissions();
                return;
            }

        }
        //NOT RUNNING ANDROID 6 OR GREATER
        //Permissions allowed at install time no need to check

        Intent add = new Intent(this, AddProfileActivity.class);
        startActivity(add);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("mainActivity", "onPause");
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("mainActivity", "onDestroy");
    }
/*
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSIONS:
            {
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_CONTACTS, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_CONTACTS, PackageManager.PERMISSION_GRANTED);
                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);

                if (perms.get(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                    // All Permissions Granted
                        allPerms = true;
                } else {
                    // Permission Denied
                    Toast.makeText(MainActivity.this, "Some Permission is Denied", Toast.LENGTH_SHORT)
                            .show();
                    allPerms = false;
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }*/

    /*private void checkPermissions() {





        allPerms = false;
        if (permissionsNeeded.size() > 0) {
                // Need Rationale
                String message = "You need to grant access to " + permissionsNeeded.get(0);
                for (int i = 1; i < permissionsNeeded.size(); i++)
                    message = message + ", " + permissionsNeeded.get(i);
                showMessageOKCancel(message,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestPermissions(permissionsNeeded.toArray(new String[permissionsNeeded.size()]),
                                        REQUEST_PERMISSIONS);
                            }
                        });
                return;
            }
            requestPermissions(permissionsNeeded.toArray(new String[permissionsNeeded.size()]),
                    REQUEST_PERMISSIONS);




    }*/

    public void askForPermissions(String permission) {
        if (Build.VERSION.SDK_INT >= 23) {
            // Check for Rationale Option
            if (!shouldShowRequestPermissionRationale(permission))
                Toast.makeText(this, permission + " is required.", Toast.LENGTH_SHORT).show();
            //Ask for permission
            requestPermissions(new String[]{permission}, REQUEST_PERMISSIONS);

        }


    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    public void checkCurrentPermissions() {

        if(Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                askForPermissions(Manifest.permission.READ_CONTACTS);


            }
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                askForPermissions(Manifest.permission.READ_PHONE_STATE);
            }
        }

    }


    public void addProfileFinish(Profile profile){
        profiles.add(profile);
    }

}
