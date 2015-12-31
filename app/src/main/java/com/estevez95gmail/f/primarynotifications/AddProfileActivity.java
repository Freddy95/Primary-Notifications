package com.estevez95gmail.f.primarynotifications;


import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;

import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class AddProfileActivity extends AppCompatActivity {
    boolean start, end, selectedContacts, selectedDays;
    int startMin, startHour, endMin, endHour;
    TextView submit;
    ArrayList<Contact> contacts;
    TextView startView;
    TextView endView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_profile);
        init();
        getContacts();
        submit = (TextView)findViewById(R.id.submit_prof);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_profile, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }

    public void submit(View v){
        Profile p = new Profile(contacts, startHour, startMin, endHour, endMin);
        MainActivity.profiles.add(p);
        Intent main = new Intent(AddProfileActivity.this, MainActivity.class);
        startActivity(main);

    }

    /**
     * make user select the start time of this profile
     * @param v - view
     */
    public void showStartTime(View v){
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);

        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(AddProfileActivity.this, new TimePickerDialog.OnTimeSetListener()
        {
            int callCount = 0;   //To track number of calls to onTimeSet()

            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute)
            {

                if(callCount == 0)    // On first call
                {
                    if(selectedHour > endHour){
                        Toast.makeText(getBaseContext(), "Select a time before the End Time", Toast.LENGTH_SHORT).show();
                        return;
                    }else if(selectedMinute >= endMin){
                        if(selectedHour == endHour){
                            Toast.makeText(getBaseContext(), "Select a time before the End Time", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    Toast.makeText(getApplicationContext(), "Start time picked", Toast.LENGTH_SHORT).show();
                    start = true;
                    startMin = selectedMinute;
                    startHour = selectedHour;
                    updateTimeView(true);
                }
                callCount++;
                // Incrementing call count.

            }
        }, hour, minute, false);

        mTimePicker.show();
    }

    /**
     * Make user select the end time of this profile
     * @param v - view
     */
    public void showEndTime(View v){
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);

        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(AddProfileActivity.this, new TimePickerDialog.OnTimeSetListener()
        {
            int callCount = 0;   //To track number of calls to onTimeSet()

            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute)
            {

                if(callCount == 0)    // On first call
                {
                    if(selectedHour < startHour){
                        Toast.makeText(getBaseContext(), "Select a time after the Start Time", Toast.LENGTH_SHORT).show();
                        return;
                    }else if(selectedMinute <= startMin){
                        if(selectedHour == startHour){
                            Toast.makeText(getBaseContext(), "Select a time after the Start Time", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    Toast.makeText(getApplicationContext(), "End time picked", Toast.LENGTH_SHORT).show();
                    end = true;
                    endMin = selectedMinute;
                    endHour = selectedHour;
                    updateTimeView(false);
                }
                callCount++;
                   // Incrementing call count.

            }
        }, hour, minute, false);

        mTimePicker.show();
    }

    /**
     * Returns to main activity
     * @param v - view that initiated function call
     */
    public void returnHome(View v){
          finish();

    }

    /*public void initSubmit(){
        submit.setEnabled(false);
        submit.setTextColor(ColorStateList);
    }*/

    public void selectContacts(View v){
        SelectContactDialog dialog = new SelectContactDialog();
        dialog.add(contacts);
        dialog.show(getFragmentManager(), "Select Contacts");

    }

    public void selectDays(View v){
        SelectDialog selectDays = new SelectDialog();
        selectDays.show(getFragmentManager(), "Select Days");
    }
    /**
     * gets the list of contacts from users phone
     */
    public void getContacts(){

        try {
            Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
            while (phones.moveToNext()){
                String name=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                if(phoneNumber != null && !(phoneNumber.equals( ""))) {
                    Contact newContact = new Contact(name, phoneNumber);

                    contacts.add(newContact);
                }
            }
            phones.close();
            Collections.sort(contacts, Contact.contactNameComp);
        } catch (RuntimeException e) {
            //Toast.makeText(getBaseContext(), "Need Permissions First.", Toast.LENGTH_SHORT).show();
            Intent main = new Intent(AddProfileActivity.this, MainActivity.class);
            startActivity(main);
        }
    }
    /**
     * When user is finished setting up profile add it to list in MainActivity
     */
    public void finished(View v){

    }

    /**
     * initializing values
     */
    public void init(){
        startView = (TextView) findViewById(R.id.setStartTime);
        endView = (TextView) findViewById(R.id.setEndTime);
        contacts = new ArrayList<>();
        start = false;
        end = false;
        selectedDays =false;
        selectedContacts = false;
        startMin = 0;
        startHour = 0;
        endMin = 24;
        endHour = 60;
    }

    /**
     * Changes the text of selecting times to the time the user set.
     * @param start - whether user selected start time or end time.
     */
    public void updateTimeView(boolean start){
        String min;
        if(start){

            if(startMin < 10)
                min = "0" + startMin;
            else
                min = "" + startMin;
            if(startHour > 11)
                startView.setText(((startHour%13)+(startHour/13)) + ":" + min + " Pm");
            else
                startView.setText(startHour + ":" + min + " Am");
        }else{
            if(endMin < 10)
                min = "0" + endMin;
            else
                min = "" + endMin;
            if(endHour > 11)
                endView.setText(((endHour%13)+(endHour/13)) + ":" + min + " Pm");
            else
                endView.setText(endHour + ":" + min + " Am");
        }
    }


}
