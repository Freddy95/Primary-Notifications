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
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class ProfileActivity extends AppCompatActivity implements MultiChoiceDialog.OnDialogDismissListener{
    boolean start, end, selectedContacts, selectedDays;
    int startMin, startHour, endMin, endHour;
    ImageView submit;
    ImageView cancel;
    ArrayList<Contact> contacts;
    TextView startView;
    TextView endView;
    Profile profile;
    CheckBox sms;
    CheckBox phoneCalls;
    boolean editing;
    int id;
    static ProfileActivity fa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        fa = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_profile);
        submit = (ImageView)findViewById(R.id.submit_prof);
        if(MainActivity.selectedProfile == null) {//If there is no selected profile we are creating a new profile
            profile = new Profile();
            editing = false;
            submit.setEnabled(false);
            init();
            initHandlers();
            submit.setVisibility(View.INVISIBLE);

        }else{//Editing selected profile
            profile = MainActivity.selectedProfile;
            id = MainActivity.profiles.indexOf(profile);
            editing = true;
            initEdit();
            initEditHandlers();
            submit.setVisibility(View.VISIBLE);
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
        profile.setId(id);
        MainActivity.db.insertProfile(profile);
        profile.setId(MainActivity.db.getLast());

        MainActivity.fa.finish();
        Intent main = new Intent(ProfileActivity.this, MainActivity.class);
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
        mTimePicker = new TimePickerDialog(ProfileActivity.this, new TimePickerDialog.OnTimeSetListener()
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
        mTimePicker = new TimePickerDialog(ProfileActivity.this, new TimePickerDialog.OnTimeSetListener()
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
        MultiChoiceDialog dialog = new MultiChoiceDialog();
        Bundle b = new Bundle();
        b.putBoolean("Contact", true);
        b.putParcelable("Profile", profile);
        b.putParcelableArrayList("ContactList", contacts);
        dialog.setArguments(b);
        dialog.show(getFragmentManager(), "Select Contacts");


    }

    public void selectDays(View v){
        MultiChoiceDialog dialog = new MultiChoiceDialog();
        Bundle b = new Bundle();
        b.putBoolean("Contact", false);
        b.putParcelable("Profile", profile);
        dialog.setArguments(b);
        dialog.show(getFragmentManager(), "Select Days");
        Log.d("tag", "Selecting days");
    }
    /**
     * gets the list of contacts from users phone
     */
    public void getContacts(){

        contacts = MainActivity.contacts;
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
        cancel = (ImageView) findViewById(R.id.cancel_prof);

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
        cancel = (ImageView) findViewById(R.id.cancel_prof);

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

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnHome();
            }
        });
        sms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                profile.setSms(isChecked);
            }
        });

        phoneCalls.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                profile.setPhoneCalls(isChecked);
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

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteProfile();
            }
        });

        sms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                profile.setSms(isChecked);
            }
        });

        phoneCalls.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                profile.setPhoneCalls(isChecked);
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
            else if(startHour > 0)
                startView.setText(startHour + ":" + min + " AM");
            else
                startView.setText("12:" + min + " AM");
        }else{
            if(endMin < 10)
                min = "0" + endMin;
            else
                min = "" + endMin;
            if(endHour > 11)
                endView.setText(((endHour%13)+(endHour/13)) + ":" + min + " PM");
            else if(endHour > 0)
                endView.setText(endHour + ":" + min + " AM");
            else
                endView.setText("12:" + min + " AM");
        }
    }

    /**
     * Checks to see if user can add profile to their list of profiles.
     * If so, submit button enable.
     */
    public void checkSubmit(){
        if(profile.getContacts().isEmpty()){
            submit.setEnabled(false);
            submit.setVisibility(View.INVISIBLE);
            return;
        }if(!end || !start){
            submit.setEnabled(false);
            submit.setVisibility(View.INVISIBLE);

            return;
        }if(!(profile.hasDay())){
            submit.setEnabled(false);
            submit.setVisibility(View.INVISIBLE);

            return;
        }
        if(!(profile.isSms() || profile.isPhoneCalls())){
            submit.setEnabled(false);
            submit.setVisibility(View.INVISIBLE);

            return;
        }
        submit.setVisibility(View.VISIBLE);
        submit.setEnabled(true);

    }


    public void smsToggle(View v){
        profile.setSms(sms.isChecked());
        checkSubmit();

    }

    public void phoneCallsToggle(View v){
        profile.setPhoneCalls(phoneCalls.isChecked());
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
        profile.setEnabled(true);
        profile.setSms(sms.isChecked());
        profile.setPhoneCalls(phoneCalls.isChecked());
        id = profile.getId();
        MainActivity.db.updateProfile(profile, id);
        MainActivity.fa.finish();
        Intent main = new Intent(ProfileActivity.this, MainActivity.class);
        startActivity(main);
        finish();
    }

    public void deleteProfile(){
        id = profile.getId();
        MainActivity.db.deleteProfile(id);
        MainActivity.fa.finish();
        Intent main = new Intent(ProfileActivity.this, MainActivity.class);
        startActivity(main);
        finish();
    }





}
