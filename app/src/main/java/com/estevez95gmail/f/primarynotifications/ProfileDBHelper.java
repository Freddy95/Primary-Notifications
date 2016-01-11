package com.estevez95gmail.f.primarynotifications;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

public class ProfileDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Profiles.db";
    public static final String PROFILES_TABLE_NAME = "profiles";
    public static final String PROFILES_COLUMN_ID = "id";
    public static final String PROFILES_COLUMN_NAME = "name";
    public static final String PROFILES_COLUMN_PHONE_NUMBER = "phone_number";
    public static final String PROFILES_COLUMN_START_TIME = "start_time";
    public static final String PROFILES_COLUMN_END_TIME = "end_time";
    public static final String PROFILES_COLUMN_START_TIME_VIEW = "start_time_view";
    public static final String PROFILES_COLUMN_END_TIME_VIEW = "end_time_view";
    public static final String PROFILES_COLUMN_ENABLED = "enabled";
    public static final String PROFILES_COLUMN_DAYS = "days";
    public static final String PROFILES_COLUMN_PHONECALLS = "phone_calls";
    public static final String PROFILES_COLUMN_SMS = "sms";
    public static final String strSeparator = "__,__";
    public static final String PROFILES_TRUE = "True";
    public static final String PROFILES_FALSE = "False";



    public ProfileDBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(
                "create table profiles " +
                        "(id integer primary key, name text, phone_number text, start_time text" +
                        ", end_time text, days text, start_time_view text, end_time_view text, enabled text," +
                        " phone_calls text, sms text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS profiles");
        onCreate(db);
    }

    public boolean insertProfile(Profile profile) {

        String n = convertArrayListToString(profile.getAllNames());
        String p = convertArrayListToString(profile.getAllPhoneNumbers());
        int startHour = profile.getStartHour();
        int startMin = profile.getStartMinute();
        int endHour = profile.getEndHour();
        int endMin = profile.getEndMinute();

        ArrayList<String> startTime = new ArrayList<>();
        startTime.add(Integer.toString(startHour));
        startTime.add(Integer.toString(startMin));

        String s = convertArrayListToString(startTime);

        ArrayList<String> endTime = new ArrayList<>();
        endTime.add(Integer.toString(endHour));
        endTime.add(Integer.toString(endMin));

        String e = convertArrayListToString(endTime);

        String d = convertBooleanToDays(profile.getDays());

        String startTimeView = profile.getStartTime();
        String endTimeView = profile.getEndTime();
        String enabled = PROFILES_FALSE;
        String sms = PROFILES_FALSE;
        String phoneCalls = PROFILES_FALSE;
        if(profile.isEnabled())
            enabled = PROFILES_TRUE;
        if(profile.isPhoneCalls())
            phoneCalls = PROFILES_TRUE;
        if(profile.isSms())
            sms = PROFILES_TRUE;


        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PROFILES_COLUMN_NAME, n);
        contentValues.put(PROFILES_COLUMN_PHONE_NUMBER, p);
        contentValues.put(PROFILES_COLUMN_START_TIME, s);
        contentValues.put(PROFILES_COLUMN_END_TIME, e);
        contentValues.put(PROFILES_COLUMN_DAYS, d);
        contentValues.put(PROFILES_COLUMN_START_TIME_VIEW, startTimeView);
        contentValues.put(PROFILES_COLUMN_END_TIME_VIEW, endTimeView);
        contentValues.put(PROFILES_COLUMN_ENABLED, enabled);
        contentValues.put(PROFILES_COLUMN_PHONECALLS, phoneCalls);
        contentValues.put(PROFILES_COLUMN_SMS, sms);

        db.insert("profiles", null, contentValues);
        db.close();
        return true;
    }





    public boolean updateProfile(Profile profile, int id) {


        String n = convertArrayListToString(profile.getAllNames());
        String p = convertArrayListToString(profile.getAllPhoneNumbers());
        int startHour = profile.getStartHour();
        int startMin = profile.getStartMinute();
        int endHour = profile.getEndHour();
        int endMin = profile.getEndMinute();

        ArrayList<String> startTime = new ArrayList<>();
        startTime.add(Integer.toString(startHour));
        startTime.add(Integer.toString(startMin));

        String s = convertArrayListToString(startTime);

        ArrayList<String> endTime = new ArrayList<>();
        endTime.add(Integer.toString(endHour));
        endTime.add(Integer.toString(endMin));

        String e = convertArrayListToString(endTime);

        String d = convertBooleanToDays(profile.getDays());

        String startTimeView = profile.getStartTime();
        String endTimeView = profile.getEndTime();
        String enabled = PROFILES_FALSE;
        String sms = PROFILES_FALSE;
        String phoneCalls = PROFILES_FALSE;
        if(profile.isEnabled())
            enabled = PROFILES_TRUE;
        if(profile.isPhoneCalls())
            phoneCalls = PROFILES_TRUE;
        if(profile.isSms())
            sms = PROFILES_TRUE;


        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PROFILES_COLUMN_NAME, n);
        contentValues.put(PROFILES_COLUMN_PHONE_NUMBER, p);
        contentValues.put(PROFILES_COLUMN_START_TIME, s);
        contentValues.put(PROFILES_COLUMN_END_TIME, e);
        contentValues.put(PROFILES_COLUMN_DAYS, d);
        contentValues.put(PROFILES_COLUMN_START_TIME_VIEW, startTimeView);
        contentValues.put(PROFILES_COLUMN_END_TIME_VIEW, endTimeView);
        contentValues.put(PROFILES_COLUMN_ENABLED, enabled);
        contentValues.put(PROFILES_COLUMN_PHONECALLS, phoneCalls);
        contentValues.put(PROFILES_COLUMN_SMS, sms);


        db.update("profiles", contentValues, "id = ? ", new String[]{Integer.toString(id)});
        db.close();
        return true;
    }

    public Integer deleteProfile(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int i =  db.delete("profiles",
                "id = ? ",
                new String[]{Integer.toString(id)});
        db.close();
        return i;
    }

    public ArrayList<Profile> getAllProfiles() {
        ArrayList<Profile> array_list = new ArrayList<>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from profiles", null);
        res.moveToFirst();

        while (res.isAfterLast() == false) {
            ArrayList<String> names = convertStringToArrayList((res.getString(res.getColumnIndex(PROFILES_COLUMN_NAME))));
            ArrayList<String> phoneNumbers = convertStringToArrayList((res.getString(res.getColumnIndex(PROFILES_COLUMN_PHONE_NUMBER))));
            ArrayList<String> startTimes = convertStringToArrayList((res.getString(res.getColumnIndex(PROFILES_COLUMN_START_TIME))));
            ArrayList<String> endTimes = convertStringToArrayList((res.getString(res.getColumnIndex(PROFILES_COLUMN_END_TIME))));
            String startTimeView = res.getString(res.getColumnIndex(PROFILES_COLUMN_START_TIME_VIEW));
            String endTimeView = res.getString(res.getColumnIndex(PROFILES_COLUMN_END_TIME_VIEW));
            ArrayList<String> days = convertStringToArrayList(res.getString(res.getColumnIndex(PROFILES_COLUMN_DAYS)));
            String enabled = res.getString(res.getColumnIndex(PROFILES_COLUMN_ENABLED));
            String phoneCalls = res.getString(res.getColumnIndex(PROFILES_COLUMN_PHONECALLS));
            String sms = res.getString(res.getColumnIndex(PROFILES_COLUMN_SMS));

            Profile profile = new Profile();

            profile.setDays(convertDaysToBoolean(days));
            profile.setEnabled(enabled.equals(PROFILES_TRUE));
            profile.setPhoneCalls(phoneCalls.equals(PROFILES_TRUE));
            profile.setSms(sms.equals(PROFILES_TRUE));

            int startHour = Integer.valueOf(startTimes.get(0));
            int startMin = Integer.valueOf(startTimes.get(1));
            int endHour = Integer.valueOf(endTimes.get(0));
            int endMin = Integer.valueOf(endTimes.get(1));

            profile.setStartHour(startHour);
            profile.setStartMinute(startMin);
            profile.setEndHour(endHour);
            profile.setEndMinute(endMin);


            profile.setEndTime(endTimeView);
            profile.setStartTime(startTimeView);
            for(int i = 0; i < phoneNumbers.size(); i++){
                Contact c = new Contact(names.get(i), phoneNumbers.get(i));
                profile.addContact(c);
            }
            array_list.add(profile);
            res.moveToNext();
        }
        res.close();
        db.close();
        return array_list;



    }


    public static String convertArrayListToString(ArrayList<String> array){
        String str = "";
        for (int i = 0;i < array.size(); i++) {
            str = str + array.get(i);
            // Do not append comma at the end of last element
            if(i < array.size()-1){
                str = str + strSeparator;
            }
        }
        return str;
    }
    public static ArrayList<String> convertStringToArrayList(String str){
        String[] arr = str.split(strSeparator);
        ArrayList<String> a = new ArrayList<>();
        for(int i = 0; i < arr.length; i++){
            a.add(arr[i]);
        }
        return a;
    }


    public static boolean[] convertDaysToBoolean(ArrayList<String> days){
        boolean[] ret = {false, false, false, false, false, false, false};

        for(int i = 0; i < days.size(); i++){
            String s = days.get(i);

            switch (s){
                case "Sunday":
                    ret[0] = true;
                    break;
                case "Monday":
                    ret[1] = true;
                    break;
                case "Tuesday":
                    ret[2] = true;
                    break;
                case "Wednesday":
                    ret[3] = true;
                    break;
                case "Thursday":
                    ret[4] = true;
                    break;
                case "Friday":
                    ret[5] = true;
                    break;
                case "Saturday":
                    ret[6] = true;
                    break;

                default:
                    break;
            }

        }

        return ret;
    }

    public String convertBooleanToDays(boolean[] days){
        ArrayList<String> d = new ArrayList<>();

        if(days.length != 7)
            return null;

        if(days[0])
            d.add("Sunday");
        if(days[1])
            d.add("Monday");
        if(days[2])
            d.add("Tuesday");
        if(days[3])
            d.add("Wednesday");
        if(days[4])
            d.add("Thursday");
        if(days[5])
            d.add("Friday");
        if(days[6])
            d.add("Saturday");

        String da = convertArrayListToString(d);
        return da;
    }
}
