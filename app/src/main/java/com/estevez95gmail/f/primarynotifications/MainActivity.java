package com.estevez95gmail.f.primarynotifications;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.*;
import android.provider.ContactsContract;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
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
    static ArrayList<Contact> contacts;
    static Ringtone ringtone;
    static int originalRingerMode;
    static int volume;
    static AudioManager audioManager;
    static Activity fa;
    static boolean playing;
    static Context context;
    static int id;
    static int called;//if 0, ringtone should not be ringing

    final private int REQUEST_PERMISSIONS = 123;

    final String receiveSms = Manifest.permission.READ_SMS;
    final String readPhoneState = Manifest.permission.READ_PHONE_STATE;
    final String readExternalStorage = Manifest.permission.READ_EXTERNAL_STORAGE;
    final String readContacts = Manifest.permission.READ_CONTACTS;
    final String writeExternalStorage = Manifest.permission.WRITE_EXTERNAL_STORAGE;

    static PendingIntent pIntent1;
    static Notification notif;
    static int prevRingerMode;
    static int prevVolume;
    static NotificationManager notificationManager;

    static AlarmManager alarmManager;

    static ProfileDBHelper db;
    static boolean notified;
    static boolean backButton;
    static boolean alwaysSilent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SmsListener.notified = 0;
        Intent intent = new Intent(this, KillNotificationService.class);
        startService(intent);
        backButton = false;

        alwaysSilent = true;

        if (ProfileActivity.fa != null)
            ProfileActivity.fa.finish();

        notified = false;
        Notification.Builder builder = new Notification.Builder(getApplicationContext());
        builder.setSmallIcon(R.mipmap.ic_launcher);

        builder.setContentTitle("Primary Notifications");
        builder.setContentText("Primary Notifications is active");
        Intent i = new Intent(this, MainActivity.class);
        if (Build.VERSION.SDK_INT > 15) {

            PendingIntent pIntent = PendingIntent.getActivity(this, 0, i, 0);
            builder.setContentIntent(pIntent);
        } else {

            PendingIntent pIntent = PendingIntent.getActivity(this, 0, i, 0);
            builder.setContentIntent(pIntent);
        }

        if (alarmManager == null)
            alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);


        if (Build.VERSION.SDK_INT > 15) {
            notif = builder.build();
        } else {
            notif = builder.getNotification();
        }
        notif.flags = Notification.FLAG_ONGOING_EVENT;
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (fa != null)
            fa.finish();
        fa = this;
        context = getApplicationContext();
        db = new ProfileDBHelper(this);
        profiles = db.getAllProfiles();
        setUpAlarms();

        super.onCreate(savedInstanceState);
        // list = getListView();

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        checkNotification();

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
                    if (state == TelephonyManager.CALL_STATE_RINGING) {//phone is ringing
                        if(called == 0) {
                            called = 1;
                            Log.d("Ringing", "RINGING");
                            if (ringtone != null) {
                                if (ringtone.isPlaying())
                                    return;
                            }
                            if (isActive()) {
                                checkToRing(number, true, context);
                            }
                        }


                    }
                    if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
                        called = 0;
                        if (audioManager != null) {
                            if(isActive()) {
                                if (ringtone != null) {
                                    ringtone.stop();
                                    playing = false;

                                }
                                if (!alwaysSilent)
                                    returnPhone();
                                else
                                    audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                            }

                        }

                    }
                    if (state == TelephonyManager.CALL_STATE_IDLE) {
                        called = 0;

                        if (audioManager != null) {
                            if (isActive()) {

                                if(!alwaysSilent)
                                    returnPhone();
                                else
                                    audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);

                                if (ringtone != null)
                                    ringtone.stop();
                                playing = false;
                            }
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
                    checkSelfPermission(Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                //WE HAVE PERMISSIONS ABLE TO ADD PROFILE
                getContacts();
                Intent add = new Intent(this, ProfileActivity.class);
                startActivity(add);
                finish();
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
        Intent add = new Intent(this, ProfileActivity.class);
        startActivity(add);
        finish();
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.d("mainActivity", "onPause");
    }


    protected void onStop() {

        super.onStop();
        Log.d("Stop", "onStop");
        if (isFinishing()) {
            onDestroy();
        }

    }


    @Override
    public void onResume() {

        super.onResume();

    }



    @Override
    protected void onDestroy() {

        alarmManager.cancel(pIntent1);
        notificationManager.cancel(1010101011);
        Log.d("mainActivity", "onDestroy");
        super.onDestroy();


    }


    /**
     * Ask for specified permission.
     *
     * @param permissions - permission to ask for
     */
    public void askForPermissions(ArrayList<String> permissions) {
        if (Build.VERSION.SDK_INT >= 23) {
            // Check for Rationale Option
            for(int i = 0; i < permissions.size(); i++){
                String permission = permissions.get(i);
                if (!shouldShowRequestPermissionRationale(permission)) {
                    if (permission.equals(readContacts))
                        Toast.makeText(this, "Need Access Contacts to get Contact List", Toast.LENGTH_LONG).show();
                    else if (permission.equals(readExternalStorage))
                        Toast.makeText(this, "Need Access to Storage to save and update profiles", Toast.LENGTH_LONG).show();
                    else if (permission.equals(readPhoneState))
                        Toast.makeText(this, "Need Access to Phone to listen for Incoming Calls", Toast.LENGTH_LONG).show();
                    else if (permission.equals(receiveSms))
                        Toast.makeText(this, "Need Access to SMS messages to listen for Incoming Messages", Toast.LENGTH_LONG).show();
                    else if (permission.equals(writeExternalStorage))
                        Toast.makeText(this, "Need Access to Storage to save profiles", Toast.LENGTH_LONG).show();
                }
            }

            //Ask for permission
            String[] arr = new String[permissions.size()];
            arr = permissions.toArray(arr);

            requestPermissions(arr, REQUEST_PERMISSIONS);

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

            ArrayList<String> permissions = new ArrayList<>();
            if (checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.READ_CONTACTS);

            }
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.READ_PHONE_STATE);
            }
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);



            }
            if (checkSelfPermission(Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.RECEIVE_SMS);
            }
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }

            askForPermissions(permissions);
        }


    }


    /**
     * When user clicks on a profile from the homepage allow them to either edit or delete the profile.
     */
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        selectedProfile = profiles.get(position);

        if (Build.VERSION.SDK_INT >= 23) {
            //IF ANDROID 6 OR GREATER MUST CHECK TO SEE IF WE HAVE PERMISSIONS
            if (checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                //WE HAVE PERMISSIONS ABLE TO ADD PROFILE
                getContacts();
                Intent edit = new Intent(this, ProfileActivity.class);
                startActivity(edit);
                finish();
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
        Intent edit = new Intent(this, ProfileActivity.class);
        startActivity(edit);
        finish();
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
                if (phoneNumber != null && !(phoneNumber.equals(""))) {
                    while (phoneNumber.contains("-")) {
                        int ind = phoneNumber.indexOf("-");
                        String s = phoneNumber.substring(0, ind);
                        String t = phoneNumber.substring(ind + 1, phoneNumber.length());
                        phoneNumber = s + t;
                    }
                    while (phoneNumber.contains("(")) {

                        int ind = phoneNumber.indexOf("(");
                        String s = phoneNumber.substring(0, ind);
                        String t = phoneNumber.substring(ind + 1, phoneNumber.length());
                        phoneNumber = s + t;


                    }

                    while (phoneNumber.contains(")")) {

                        int ind = phoneNumber.indexOf(")");
                        String s = phoneNumber.substring(0, ind);
                        String t = phoneNumber.substring(ind + 1, phoneNumber.length());
                        phoneNumber = s + t;

                    }
                    while (phoneNumber.contains(" ")) {
                        int ind = phoneNumber.indexOf(" ");
                        String s = phoneNumber.substring(0, ind);
                        String t = phoneNumber.substring(ind + 1, phoneNumber.length());
                        phoneNumber = s + t;
                    }

                    Contact newContact = new Contact(name, phoneNumber);

                    contacts.add(newContact);
                }
            }
            phones.close();
            Collections.sort(contacts, Contact.contactNameComp);
            ArrayList<Contact> con = contacts;
            int size = contacts.size();
            for (int i = 0; i < size - 1; i++) {
                if (contacts.get(i).getName().equals(contacts.get(i + 1).getName())) {
                    if (contacts.get(i).getPhoneNumber().equals(contacts.get(i + 1).getPhoneNumber())) {
                        con.remove(i + 1); // if contact has same number and name remove from list
                        i--;
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

    /**
     * Check to determine if phone should play ringtone or notification sound.
     *
     * @param phoneNumber - person sending the call or text message.
     * @param ring        - whether the phone is receiving a call or text
     */
    public static void checkToRing(String phoneNumber, boolean ring, Context cont) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        silencePhone();
        Log.d("CheckToRing", "Checking");


        Log.d("Size", "" + profiles.size());

        for (Profile p : profiles) {
            boolean r;
            if (ring)
                r = p.isPhoneCalls();
            else
                r = p.isSms();

            if (p.isEnabled()) {
                Log.d("Enabled", "Is Enabled");
                if (p.getDays()[day - 1]) {
                    Log.d("CurrentDay", "It is current day");

                    if (p.getStartHour() < hour && p.getEndHour() > hour) {
                        if (r) {
                            Log.d("CHECK 1", "p.getStartHour() < hour && p.getEndHour() > hour");
                            for (Contact c : p.getContacts()) {
                                if (c.getPhoneNumber().equals(phoneNumber) || c.getPhoneNumber().equals("1" + phoneNumber)) {
                                    Log.d("Ringing", "Ringing");
                                    // ring
                                    if (ring)
                                        ring();
                                    else
                                        notificationRing();
                                    return;
                                }
                            }
                        }
                    } else if (p.getStartHour() == hour && p.startHour == p.endHour) {

                        if (p.getStartMinute() <= min && p.getEndMinute() >= min) {
                            if (r) {
                                Log.d("CHECK 2", "p.getStartHour() < hour && p.getEndHour() > hour");

                                for (Contact c : p.getContacts()) {
                                    if (c.getPhoneNumber().equals(phoneNumber) || c.getPhoneNumber().equals("1" + phoneNumber)) {

                                        // ring
                                        if (ring)
                                            ring();
                                        else
                                            notificationRing();

                                        return;
                                    }
                                }
                            }
                            //ring
                        }
                    } else if (p.getStartHour() == hour) {

                        if (p.getStartMinute() <= min) {
                            Log.d("CHECK 3", "p.getStartHour() < hour && p.getEndHour() > hour");
                            if (r) {
                                for (Contact c : p.getContacts()) {
                                    if (c.getPhoneNumber().equals(phoneNumber) || c.getPhoneNumber().equals("1" + phoneNumber)) {
                                        // ring
                                        if (ring)
                                            ring();
                                        else
                                            notificationRing();
                                        return;
                                    }
                                }
                            }
                        }
                    } else if (p.getEndHour() == hour) {
                        if (p.getEndMinute() >= min) {
                            Log.d("CHECK 4", "p.getStartHour() < hour && p.getEndHour() > hour");
                            if (r) {
                                for (Contact c : p.getContacts()) {
                                    if (c.getPhoneNumber().equals(phoneNumber) || c.getPhoneNumber().equals("1" + phoneNumber)) {
                                        // ring
                                        if (ring)
                                            ring();
                                        else
                                            notificationRing();
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }

            }
        }

        returnPhone();

    }

    /**
     * Play ringtone on max volume.
     */
    public static void ring() {
        if (ringtone != null)
            if (ringtone.isPlaying())
                return;

        if (!playing) {
            playing = true;

            Log.d("Org", "Original " + audioManager.getRingerMode());

            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            ringtone = RingtoneManager.getRingtone(context, notification);


            int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING);

            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            Log.d("Ringer MODE NORMAL", "NORMAL MODE  " + AudioManager.RINGER_MODE_NORMAL);
            audioManager.getStreamVolume(AudioManager.STREAM_RING);
            audioManager.setStreamVolume(AudioManager.STREAM_RING, maxVolume, AudioManager.FLAG_ALLOW_RINGER_MODES + AudioManager.FLAG_PLAY_SOUND);


            ringtone.play();
        }


    }

    /**
     * Notify the user that they have an incoming text message.
     */
    public static void notificationRing() {
        //Toast.makeText(getApplicationContext(), "Ringing", Toast.LENGTH_SHORT).show();
        if(SmsListener.notified == 0) {//Only ring notification once.

            if (ringtone != null) {
                if (ringtone.isPlaying())
                    return;
            }
            SmsListener.notified = 1;
            Log.d("Org", "Original " + audioManager.getRingerMode());


            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            ringtone = RingtoneManager.getRingtone(context, notification);


            int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING);

            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            Log.d("Ringer MODE NORMAL", "NORMAL MODE  " + AudioManager.RINGER_MODE_NORMAL);
            audioManager.getStreamVolume(AudioManager.STREAM_RING);
            audioManager.setStreamVolume(AudioManager.STREAM_RING, maxVolume, AudioManager.FLAG_ALLOW_RINGER_MODES + AudioManager.FLAG_PLAY_SOUND);
            ringtone.play();


            MediaPlayer player = new MediaPlayer();
            try {
                player.setDataSource(context, notification);
                player.prepare();
                int duration = player.getDuration();
                if (duration > 2000)
                    duration = 2000;
                Log.d("Duration", "" + duration);


                //create timer
                CountDownTimer timer = new CountDownTimer(duration, 1) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        ringtone.stop();
                        if (!alwaysSilent)
                            returnPhone();
                        else
                            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                        SmsListener.notified = 0;
                    }
                };
                timer.start();


            } catch (IOException e) {
                //Error make timer last less

                CountDownTimer timer = new CountDownTimer(1000, 1) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        ringtone.stop();

                        if (!alwaysSilent)
                            returnPhone();
                        else
                            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);

                        SmsListener.notified = 0;
                    }
                };
                timer.start();
            }
        }


    }


    /**
     * Puts the device on silent and gets the original ringer mode and volume.
     */
    public static void mutePhone() {
        if (audioManager != null) {
            if (ringtone != null)
                ringtone.stop();
            originalRingerMode = audioManager.getRingerMode();
            if (originalRingerMode == 2)
                volume = audioManager.getStreamVolume(AudioManager.STREAM_RING);


            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        }
    }

    /**
     * Check to see if at least one profile is active at the current time
     *
     * @return true if a profile is active.
     */
    public static boolean isActive() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        Log.d("Check if enabled ", "Checking");
        for (Profile p : profiles) {


            if (p.isEnabled()) {
                Log.d("Enabled", "Is Enabled");
                if (p.getDays()[day - 1]) {
                    Log.d("CurrentDay", "It is current day");

                    if (p.getStartHour() < hour && p.getEndHour() > hour) {
                        return true;
                    } else if (p.getStartHour() == hour && p.startHour == p.endHour) {

                        if (p.getStartMinute() <= min && p.getEndMinute() >= min) {
                            return true;
                        }

                    } else if (p.getStartHour() == hour) {

                        if (p.getStartMinute() <= min) {
                            return true;
                        }

                    } else if (p.getEndHour() == hour) {

                        if (p.getEndMinute() >= min) {
                            return true;
                        }

                    }
                }

            }
        }


        return false;
    }

    /**
     * Set up the alarm for one minute intervals to check whether or not app is active.
     * If active should mute device and display on-going notification.
     */
    public static void setUpAlarms() {

        cancelAlarms();//end all current running alarms
        for (Profile p : profiles) {
            if (p.isEnabled()) {
                Intent intent = new Intent(context, AlarmReceiver.class);

                if (pIntent1 == null)
                    pIntent1 = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                Log.d("TIME IN MILIS", "" + System.currentTimeMillis() + 60000);
                long x = System.currentTimeMillis() % 60000;
                x = 60000 - x;
                if (Build.VERSION.SDK_INT >= 19)
                    alarmManager.setExact(AlarmManager.RTC, System.currentTimeMillis() + x, pIntent1);
                else
                    alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + x, pIntent1);
                return;
            }
        }
//
//        for (Profile p : profiles) {
//            if (p.isEnabled()) {
//                boolean[] days = p.getDays();
//                for (int i = 0; i < 7; i++) {
//                    if (days[i]) {
//                        Intent intent = new Intent(context, AlarmReceiver.class);
//
//                        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//                        id++;
//                        Log.d("SETTING UP ALARMS", "Alarm");
//                        Calendar cal = Calendar.getInstance();
//                        Calendar newCal = Calendar.getInstance();
//                        Calendar endCal = Calendar.getInstance();
//
//                        newCal.set(Calendar.DAY_OF_WEEK, (i + 1));
//                        newCal.set(Calendar.HOUR_OF_DAY, p.getStartHour());
//                        newCal.set(Calendar.MINUTE, p.getStartMinute());
//                        Date c = cal.getTime();
//                        Date n = newCal.getTime();
//                        long mil;
//                        if (cal.compareTo(newCal) == 1) {
//                            mil = c.getTime() - n.getTime();
//                            mil = 604800000 - mil;
//                        } else
//                            mil = n.getTime() - c.getTime();
//
//                        endCal.set(Calendar.DAY_OF_WEEK, (i + 1));
//                        endCal.set(Calendar.HOUR_OF_DAY, p.getEndHour());
//                        endCal.set(Calendar.MINUTE, p.getEndMinute() + 1);
//                        Date e = endCal.getTime();
//                        long endMil;
//                        if (cal.compareTo(endCal) == 1) {
//                            endMil = c.getTime() - e.getTime();
//                            endMil = 604800000 - endMil;
//                        } else
//                            endMil = e.getTime() - c.getTime();
//                        Log.d("MILI", "" + mil);
//                        Log.d("ENDMILI", "" + endMil);
//
//                        alarmManager.setRepeating(AlarmManager.RTC, mil, 604800000, pendingIntent);
//
//                        pendingIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//                        id++;
//                        alarmManager.setRepeating(AlarmManager.RTC, endMil, 604800000, pendingIntent);
//
//
//                    }
//
//                }
//            }
//        }
    }

    /**
     * After profile period ends phone should be reset to the original ringer
     * mode it was before app was active.
     */
    public static void returnPhoneToState() {
        audioManager.setRingerMode(originalRingerMode);
        if (originalRingerMode == 2)
            audioManager.setStreamVolume(AudioManager.STREAM_RING, volume, AudioManager.FLAG_ALLOW_RINGER_MODES);
    }

    /**
     * When activity starts check whether there is a profile that is active at the current time.
     */
    public static void checkNotification() {
        if (isActive()) {
            notified = true;
            notificationManager.notify(1010101011, notif);
            mutePhone();
        } else {
            notified = false;
            notificationManager.cancel(1010101011);
            returnPhoneToState();
        }
    }


    /**
     * Cancel  alarms
     */
    public static void cancelAlarms() {
        if (alarmManager != null)
            alarmManager.cancel(pIntent1);


    }

    @Override
    public void onBackPressed() {
        Intent setIntent = new Intent(Intent.ACTION_MAIN);
        setIntent.addCategory(Intent.CATEGORY_HOME);
        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(setIntent);
        backButton = true;
    }

    /**
     * This function checks to see if phone is not on silent while app is active.
     * This may mean user wants to recieve notifications from other apps such as
     * email, fb message, etc. Subject to change. SHOULD ONLY BE CALLED IF APP IS ACTIVE.
     */
    public static void silencePhone(){
        if(audioManager != null){
            if(audioManager.getRingerMode() != 0) {
                alwaysSilent = false;
                prevRingerMode = audioManager.getRingerMode();
                if(prevRingerMode == 2)
                    prevVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING);
                audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            }else
                alwaysSilent = true;
        }
    }

    /**
     *  Return phone to state before phone call or text.
     *  Should only be called if always silent is false.
     */
    public static void returnPhone(){
        audioManager.setRingerMode(prevRingerMode);
        if(prevRingerMode == 2)
            audioManager.setStreamVolume(AudioManager.STREAM_RING, prevVolume, AudioManager.FLAG_ALLOW_RINGER_MODES);

    }


}
