package com.estevez95gmail.f.primarynotifications;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
    static boolean start, end;
    int startMin, startHour, endMin, endHour;
    TextView submit;
    ArrayList<Contact> contacts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        contacts = new ArrayList<>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_profile);
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
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
                    Toast.makeText(getApplicationContext(), "Start time picked", Toast.LENGTH_SHORT).show();
                    start = true;
                    startMin = selectedMinute;
                    startHour = selectedHour;
                }
                callCount++;
                // Incrementing call count.

            }
        }, hour, minute, false);

        mTimePicker.show();
    }
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
                    Toast.makeText(getApplicationContext(), "End time picked", Toast.LENGTH_SHORT).show();
                    end = true;
                    endMin = selectedMinute;
                    endHour = selectedHour;
                }
                callCount++;
                   // Incrementing call count.

            }
        }, hour, minute, false);

        mTimePicker.show();
    }

    public void returnHome(View v){
        Intent main = new Intent(AddProfileActivity.this, MainActivity.class);
        startActivity(main);
    }

    /*public void initSubmit(){
        submit.setEnabled(false);
        submit.setTextColor(ColorStateList);
    }*/

    public void selectContacts(View v){
        SelectContactDialog dialog = new SelectContactDialog();
        dialog.add(contacts);
        dialog.show(getFragmentManager(), "Selecting Contact");
    }
    public void getContacts(){
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        while (phones.moveToNext()){
            String name=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            if(phoneNumber != null && phoneNumber != "") {
                Contact newContact = new Contact(name, phoneNumber);
                contacts.add(newContact);
            }
        }
        phones.close();
        Collections.sort(contacts, Contact.contactNameComp);
    }
}
