package com.estevez95gmail.f.primarynotifications;


import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class AddProfileActivity extends AppCompatActivity implements MultiChoiceDialog.OnDialogDismissListener{
    boolean start, end, selectedContacts, selectedDays;
    int startMin, startHour, endMin, endHour;
    TextView submit;
    TextView textView1;
    ArrayList<Contact> contacts;
    TextView startView;
    TextView endView;
    Profile profile;
    CheckBox sms;
    CheckBox phoneCalls;
    boolean editing;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_profile);
        submit = (TextView)findViewById(R.id.submit_prof);
        if(MainActivity.selectedProfile == null) {//If there is no selected profile we are creating a new profile
            profile = new Profile();
            editing = false;
            submit.setEnabled(false);
            init();
            initHandlers();
        }else{//Editing selected profile
            profile = MainActivity.selectedProfile;
            editing = true;
            initEdit();
            initEditHandlers();
        }

        getContacts();



        checkSubmit();
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

    public void submit(){

        profile.setStartTime(startView.getText().toString());
        profile.setEndTime(endView.getText().toString());
        profile.setEnabled(true);
        MainActivity.profiles.add(profile);
        MainActivity.fa.finish();
        Intent main = new Intent(AddProfileActivity.this, MainActivity.class);
        startActivity(main);
        finish();

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
                    profile.setStartHour(startHour);
                    profile.setStartMinute(startMin);
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

                    profile.setEndHour(endHour);
                    profile.setEndMinute(endMin);

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
     */
    public void returnHome(){

          finish();

    }

    /*public void initSubmit(){
        submit.setEnabled(false);
        submit.setTextColor(ColorStateList);
    }*/

    public void selectContacts(View v){
        MultiChoiceDialog dialog = new MultiChoiceDialog(profile, true);
        dialog.addContacts(contacts);
        dialog.show(getFragmentManager(), "Select Contacts");


    }

    public void selectDays(View v){
        MultiChoiceDialog dialog = new MultiChoiceDialog(profile, false);
        dialog.show(getFragmentManager(), "Select Days");
        Log.d("tag", "Selecting days");
    }
    /**
     * gets the list of contacts from users phone
     */
    public void getContacts(){

        /*try {
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
            Intent main = new Intent(AddProfileActivity.this, MainActivity.class);
            startActivity(main);
        }*/
        contacts = MainActivity.contacts;
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
        sms = (CheckBox) findViewById(R.id.SMS);
        phoneCalls = (CheckBox) findViewById(R.id.phoneCalls);
        textView1 = (TextView) findViewById(R.id.cancel_prof);

    }

    /**
     * Initialize values when editing a profile.
     */
    public void initEdit(){
        startView = (TextView) findViewById(R.id.setStartTime);
        endView = (TextView) findViewById(R.id.setEndTime);
        contacts = new ArrayList<>();
        start = true;
        end = true;
        selectedDays = true;
        selectedContacts = true;
        textView1 = (TextView) findViewById(R.id.cancel_prof);
        textView1.setText("DELETE");

        startMin = profile.getStartMinute();
        startHour = profile.getStartHour();
        endMin = profile.getEndMinute();
        endHour = profile.getEndHour();

        sms = (CheckBox) findViewById(R.id.SMS);
        phoneCalls = (CheckBox) findViewById(R.id.phoneCalls);
        sms.setChecked(profile.isSms());
        phoneCalls.setChecked(profile.isPhoneCalls());

        updateTimeView(false);//update the end time view
        updateTimeView(true);//update the start time view
    }

    /**
     * Initialize handlers of controls.
     */
    private void initHandlers(){

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });

        textView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnHome();
            }
        });

    }


    private void initEditHandlers(){
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishEdit();
            }
        });

        textView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteProfile();
            }
        });
    }

    /**
     * Changes the text of selecting times to the time the user set.
     * Also checks to see if we should enable the submit button.
     * @param start - whether user selected start time or end time.
     */
    public void updateTimeView(boolean start){
        //Check if submit should be enabled.
        checkSubmit();

        String min;
        if(start){

            if(startMin < 10)
                min = "0" + startMin;
            else
                min = "" + startMin;
            if(startHour > 11)
                startView.setText(((startHour%13)+(startHour/13)) + ":" + min + " PM");
            else
                startView.setText(startHour + ":" + min + " AM");
        }else{
            if(endMin < 10)
                min = "0" + endMin;
            else
                min = "" + endMin;
            if(endHour > 11)
                endView.setText(((endHour%13)+(endHour/13)) + ":" + min + " PM");
            else
                endView.setText(endHour + ":" + min + " AM");
        }
    }

    /**
     * Checks to see if user can add profile to their list of profiles.
     * If so, submit button enable.
     */
    public void checkSubmit(){
        if(profile.getSelected().isEmpty()){
            submit.setEnabled(false);
            return;
        }if(!end || !start){
            submit.setEnabled(false);
            return;
        }if(!(profile.hasDay())){
            submit.setEnabled(false);
            return;
        }
        if(!(profile.isSms() || profile.isPhoneCalls())){
            submit.setEnabled(false);
            return;
        }
        submit.setEnabled(true);

    }


    public void smsToggle(View v){
        if(sms.isChecked()){
            profile.setSms(true);
        }else{
            profile.setSms(false);
        }
        checkSubmit();

    }

    public void phoneCallsToggle(View v){
        if(phoneCalls.isChecked()){
            profile.setPhoneCalls(true);
        }else{
            profile.setPhoneCalls(false);
        }
        checkSubmit();
    }

    public void onDialogDismissListener(int position) {
        // Do something here to display that article
        Log.d("welp", "DOES THIS WORK NOW");
        checkSubmit();
    }


    public void finishEdit(){
        profile.setStartTime(startView.getText().toString());
        profile.setEndTime(endView.getText().toString());
        MainActivity.fa.finish();
        Intent main = new Intent(AddProfileActivity.this, MainActivity.class);
        startActivity(main);
        finish();
    }

    public void deleteProfile(){
        MainActivity.profiles.remove(profile);
        MainActivity.fa.finish();
        Intent main = new Intent(AddProfileActivity.this, MainActivity.class);
        startActivity(main);
        finish();
    }

}
