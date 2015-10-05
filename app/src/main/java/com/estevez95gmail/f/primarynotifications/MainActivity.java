package com.estevez95gmail.f.primarynotifications;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
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


public class MainActivity extends AppCompatActivity {
    //NUMBER OF INCOMING CALL
    String number;
    //HOLDS PROFILES USER CREATES
    ArrayList<Profile> profiles;
    boolean allPerms = false;

    final private int REQUEST_PERMISSIONS = 123;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(savedInstanceState == null)
            profiles = new ArrayList<>();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
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
            manager.listen(callStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        } catch (RuntimeException e) {
                Toast.makeText(getBaseContext(), "Primary Notifications requires permissions", Toast.LENGTH_SHORT).show();
        }
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
        if(id == R.id.action_request_permission){
            checkPermissions();
        }

        return super.onOptionsItemSelected(item);
    }
    /**
     * ALLOWS USER TO CREATE NEW PROFILE
     */
    public void addProfile(){
        if(Build.VERSION.SDK_INT >= 23) {
            //checkPermissions();
            if(!checkPerms()){// we didnt get all permissions needed
                Toast.makeText(getBaseContext(), "Need All Permissions", Toast.LENGTH_SHORT).show();
                checkPermissions();
                return;
            }
        }
        Intent add = new Intent(this, AddProfileActivity.class);
        startActivity(add);
    }

    @Override
    public void onPause(){
        super.onPause();
        Log.d("mainActivity", "onPause");
    }
    @Override
    public void onResume(){
        super.onResume();

    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.d("mainActivity", "onDestroy");
    }

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
    }

    private void checkPermissions() {
        List<String> permissionsNeeded = new ArrayList<String>();

        final List<String> permissionsList = new ArrayList<String>();
        if (!addPermission(permissionsList, Manifest.permission.READ_PHONE_STATE))
            permissionsNeeded.add("Read Phone Calls");
        if (!addPermission(permissionsList, Manifest.permission.READ_CONTACTS))
            permissionsNeeded.add("Read Contacts");

        if(permissionsNeeded.size() == 0){
            Toast.makeText(getBaseContext(), "Permissions Already Granted", Toast.LENGTH_SHORT).show();
            allPerms = true;
            return;
        }
        allPerms = false;
        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {
                // Need Rationale
                String message = "You need to grant access to " + permissionsNeeded.get(0);
                for (int i = 1; i < permissionsNeeded.size(); i++)
                    message = message + ", " + permissionsNeeded.get(i);
                showMessageOKCancel(message,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                                        REQUEST_PERMISSIONS);
                            }
                        });
                return;
            }
            requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                    REQUEST_PERMISSIONS);

        }


    }

    private boolean addPermission(List<String> permissionsList, String permission) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            // Check for Rationale Option
            if (!shouldShowRequestPermissionRationale(permission))
                return false;
        }
        return true;
    }
    private void showMessageOKCancel(String message,DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    public boolean checkPerms(){
        if(checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED){
            return false;
        }
        if(checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){
            return false;
        }
        return true;
    }



}
