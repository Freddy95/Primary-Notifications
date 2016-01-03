package com.estevez95gmail.f.primarynotifications;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;

import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;


import android.Manifest;


public class MainActivity extends ListActivity {
    //NUMBER OF INCOMING CALL
    String number;
    //HOLDS PROFILES USER CREATES
    static ArrayList<Profile> profiles = new ArrayList<>();
    static Profile selectedProfile;
    ListView list;
    static ArrayList<Contact> contacts;
    Ringtone ringtone;
    static int originalRingerMode;
    static int volume;
    AudioManager audioManager;
    static Activity fa;
    static boolean playing;


    final private int REQUEST_PERMISSIONS = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("CHECK", "Does this work");

        fa = this;



        super.onCreate(savedInstanceState);
        list = getListView();


        setContentView(R.layout.activity_main);
        ProfileAdapter adapter = new ProfileAdapter(list.getContext(), profiles);
        setListAdapter(adapter);


        try {
            TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            PhoneStateListener callStateListener = new PhoneStateListener() {
                public void onCallStateChanged(int state, String incomingNumber) {
                    //  React to incoming call.
                    number = incomingNumber;
                    // If phone ringing
                    if (state == TelephonyManager.CALL_STATE_RINGING) {//phone is ringing
                        if (ringtone != null) {
                            if (ringtone.isPlaying())
                                return;
                        }
                        checkToRing(number);
                    }
                    if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
                        if(audioManager != null) {
                            Toast.makeText(getBaseContext(), "picked up or hung op", Toast.LENGTH_SHORT).show();

                            if (ringtone != null) {
                                ringtone.stop();
                                playing = false;

                            }
                            audioManager.setRingerMode(originalRingerMode);
                            if (originalRingerMode == 2) {
                                audioManager.setStreamVolume(AudioManager.STREAM_RING, volume, AudioManager.FLAG_SHOW_UI);
                            }
                            audioManager = null;
                        }

                    }
                    if (state == TelephonyManager.CALL_STATE_IDLE) {
                        Toast.makeText(getBaseContext(), "maybe doing something", Toast.LENGTH_SHORT).show();

                        if (audioManager != null) {
                            audioManager.setRingerMode(originalRingerMode);
                            if (originalRingerMode == 2) {
                                audioManager.setStreamVolume(AudioManager.STREAM_RING, volume, AudioManager.FLAG_SHOW_UI);
                            }
                            playing = false;
                            audioManager = null;
                        }
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
        selectedProfile = null;
        if (Build.VERSION.SDK_INT >= 23) {
            //IF ANDROID 6 OR GREATER MUST CHECK TO SEE IF WE HAVE PERMISSIONS
            if (checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                //WE HAVE PERMISSIONS ABLE TO ADD PROFILE
                getContacts();
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
        getContacts();
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


    /**
     * Ask for specified permission.
     *
     * @param permission - permission to ask for
     */
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


    /**
     * Check current permissions we already have.
     */
    public void checkCurrentPermissions() {

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                askForPermissions(Manifest.permission.READ_CONTACTS);


            }
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                askForPermissions(Manifest.permission.READ_PHONE_STATE);
            }
        }

    }


    /**
     * When user clicks on a profile from the homepage allow them to either edit or delete the profile.
     */
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        selectedProfile = profiles.get(position);

        Toast.makeText(getApplicationContext(), "Item " + position + " Was Selected", Toast.LENGTH_SHORT).show();
        Intent add = new Intent(this, AddProfileActivity.class);
        startActivity(add);
        super.onListItemClick(l, v, position, id);
    }

    /**
     * gets the list of contacts from users phone
     */
    public void getContacts() {
        if (contacts != null)
            return;
        else
            contacts = new ArrayList<>();
        try {
            Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
            while (phones.moveToNext()) {
                String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                if (phoneNumber != null && !(phoneNumber.equals("")) && !(phoneNumber.contains("-"))) {

                    Contact newContact = new Contact(name, phoneNumber);

                    contacts.add(newContact);
                }
            }
            phones.close();
            Collections.sort(contacts, Contact.contactNameComp);
            ArrayList<Contact> con = contacts;
            int size = contacts.size();
            for(int i = 0; i < size-1; i++){
                if(contacts.get(i).getName().equals(contacts.get(i+1).getName())){
                    if(contacts.get(i).getPhoneNumber().equals(contacts.get(i+1).getPhoneNumber())){
                        con.remove(i); // if contact has same number and name remove from list
                        size--;
                    }
                }
            }
            contacts = con;
        } catch (RuntimeException e) {
            Intent main = new Intent(MainActivity.this, MainActivity.class);
            startActivity(main);
        }
    }

    public void checkToRing(String phoneNumber) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);


        for (Profile p : profiles) {
            if (p.isEnabled()) {
                Toast.makeText(getApplicationContext(), "Is Enabled", Toast.LENGTH_SHORT).show();

                if (p.getStartHour() < hour && p.getEndHour() > hour) {
                    Toast.makeText(getApplicationContext(), "Test 1", Toast.LENGTH_SHORT).show();

                    for (Contact c : p.getSelected()) {
                        if (c.getPhoneNumber().equals(phoneNumber) || c.getPhoneNumber().equals("1" + phoneNumber)) {
                            Toast.makeText(getApplicationContext(), "TEST 1A", Toast.LENGTH_SHORT).show();

                            // ring
                            ring();
                            return;
                        }
                    }
                } else if (p.getStartHour() == hour && p.startHour == p.endHour) {
                    Toast.makeText(getApplicationContext(), "Test 2", Toast.LENGTH_SHORT).show();

                    if (p.getStartMinute() <= min && p.getEndMinute() >= min) {
                        Toast.makeText(getApplicationContext(), "Test 21", Toast.LENGTH_SHORT).show();

                        for (Contact c : p.getSelected()) {
                            if (c.getPhoneNumber().equals(phoneNumber) || c.getPhoneNumber().equals("1" + phoneNumber)) {
                                Toast.makeText(getApplicationContext(), "Test 21A", Toast.LENGTH_SHORT).show();

                                // ring
                                ring();
                                return;
                            }
                        }
                        //ring
                    }
                } else if (p.getStartHour() == hour) {
                    Toast.makeText(getApplicationContext(), "Test 3", Toast.LENGTH_SHORT).show();

                    if (p.getStartMinute() <= min) {
                        Toast.makeText(getApplicationContext(), "Test 3b", Toast.LENGTH_SHORT).show();

                        for (Contact c : p.getSelected()) {
                            if (c.getPhoneNumber().equals(phoneNumber) || c.getPhoneNumber().equals("1" + phoneNumber)) {
                                // ring
                                ring();
                                return;
                            }
                        }

                        //ring
                    }
                } else if (p.getEndHour() == hour) {
                    if (p.getEndMinute() >= min) {

                        for (Contact c : p.getSelected()) {
                            if (c.getPhoneNumber().equals(phoneNumber) || c.getPhoneNumber().equals("1" + phoneNumber)) {
                                // ring
                                ring();
                                return;
                            }
                        }

                    }
                }
            }
        }
    }

    public void ring() {
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            if(!playing) {
                Toast.makeText(getApplicationContext(), "Ringing", Toast.LENGTH_SHORT).show();

                Log.d("Org", "Original " + audioManager.getRingerMode());
                originalRingerMode = audioManager.getRingerMode();
                if(originalRingerMode == 2){
                    volume = audioManager.getStreamVolume(AudioManager.STREAM_RING);
                }
                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), notification);



                int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING);

                audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                Log.d("Ringer MODE NORMAL", "NORMAL MODE  " + AudioManager.RINGER_MODE_NORMAL);
                audioManager.getStreamVolume(AudioManager.STREAM_RING);
                audioManager.setStreamVolume(AudioManager.STREAM_RING, maxVolume, AudioManager.FLAG_SHOW_UI + AudioManager.FLAG_PLAY_SOUND);
                ringtone.play();
                playing = true;
            }


    }


}
