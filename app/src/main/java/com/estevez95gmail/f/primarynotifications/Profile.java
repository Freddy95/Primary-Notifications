package com.estevez95gmail.f.primarynotifications;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Contains the times and days of the week for this app to be active
 */
public class Profile implements Parcelable {

    ArrayList<Contact> contacts = new ArrayList<>();
    int startHour, endHour, startMinute, endMinute;
    String startTime, endTime;
    boolean monday, tuesday, wednesday, thursday, friday, saturday, sunday;
    boolean enabled;
    boolean sms, phoneCalls;

    int id;

    public boolean isSms() {
        return sms;
    }

    public void setSms(boolean sms) {
        this.sms = sms;
    }

    public boolean isPhoneCalls() {
        return phoneCalls;
    }

    public void setPhoneCalls(boolean phoneCalls) {
        this.phoneCalls = phoneCalls;
    }

    public Profile() {
        monday = false;
        tuesday = false;
        wednesday = false;
        thursday = false;
        friday = false;
        saturday = false;
        sunday = false;
    }


    public int getStartHour() {
        return startHour;
    }

    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    public ArrayList<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(ArrayList<Contact> contacts) {
        this.contacts = contacts;
    }

    public int getEndHour() {
        return endHour;
    }

    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }

    public int getStartMinute() {
        return startMinute;
    }

    public void setStartMinute(int startMinute) {
        this.startMinute = startMinute;
    }

    public int getEndMinute() {
        return endMinute;
    }

    public void setEndMinute(int endMinute) {
        this.endMinute = endMinute;
    }

    public boolean isSunday() {
        return sunday;
    }

    public void setSunday(boolean sunday) {
        this.sunday = sunday;
    }

    public boolean isSaturday() {
        return saturday;
    }

    public void setSaturday(boolean saturday) {
        this.saturday = saturday;
    }

    public boolean isFriday() {
        return friday;
    }

    public void setFriday(boolean friday) {
        this.friday = friday;
    }

    public boolean isThursday() {
        return thursday;
    }

    public void setThursday(boolean thursday) {
        this.thursday = thursday;
    }

    public boolean isWednesday() {
        return wednesday;
    }

    public void setWednesday(boolean wednesday) {
        this.wednesday = wednesday;
    }

    public boolean isTuesday() {
        return tuesday;
    }

    public void setTuesday(boolean tuesday) {
        this.tuesday = tuesday;
    }

    public boolean isMonday() {
        return monday;
    }

    public void setMonday(boolean monday) {
        this.monday = monday;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public boolean hasDay() {
        return (monday || tuesday || wednesday || thursday || friday || saturday || sunday);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void addContact(Contact c){
        contacts.add(c);
    }

    public void setDays(boolean[] days){
        if(days.length != 7)
            return;
        sunday = days[0];
        monday = days[1];
        tuesday = days[2];
        wednesday = days[3];
        thursday = days[4];
        friday = days[5];
        saturday = days[6];

    }

    public boolean[] getDays(){
        boolean[] days = new boolean[7];
        days[0] = sunday;
        days[1] = monday;
        days[2] = tuesday;
        days[3] = wednesday;
        days[4] = thursday;
        days[5] = friday;
        days[6] = saturday;


        return days;
    }


    public ArrayList<String> getAllNames(){
        ArrayList<String> names = new ArrayList<>();

        for(int i = 0; i< contacts.size(); i++){
            names.add(contacts.get(i).getName());
        }

        return names;
    }

    public ArrayList<String> getAllPhoneNumbers(){
        ArrayList<String> phoneNumbers = new ArrayList<>();

        for(int i = 0; i < contacts.size(); i++){
            phoneNumbers.add(contacts.get(i).getPhoneNumber());
        }

        return phoneNumbers;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(contacts);
        dest.writeInt(this.startHour);
        dest.writeInt(this.endHour);
        dest.writeInt(this.startMinute);
        dest.writeInt(this.endMinute);
        dest.writeString(this.startTime);
        dest.writeString(this.endTime);
        dest.writeByte(monday ? (byte) 1 : (byte) 0);
        dest.writeByte(tuesday ? (byte) 1 : (byte) 0);
        dest.writeByte(wednesday ? (byte) 1 : (byte) 0);
        dest.writeByte(thursday ? (byte) 1 : (byte) 0);
        dest.writeByte(friday ? (byte) 1 : (byte) 0);
        dest.writeByte(saturday ? (byte) 1 : (byte) 0);
        dest.writeByte(sunday ? (byte) 1 : (byte) 0);
        dest.writeByte(enabled ? (byte) 1 : (byte) 0);
        dest.writeByte(sms ? (byte) 1 : (byte) 0);
        dest.writeByte(phoneCalls ? (byte) 1 : (byte) 0);
        dest.writeInt(this.id);
    }

    protected Profile(Parcel in) {
        this.contacts = in.createTypedArrayList(Contact.CREATOR);
        this.startHour = in.readInt();
        this.endHour = in.readInt();
        this.startMinute = in.readInt();
        this.endMinute = in.readInt();
        this.startTime = in.readString();
        this.endTime = in.readString();
        this.monday = in.readByte() != 0;
        this.tuesday = in.readByte() != 0;
        this.wednesday = in.readByte() != 0;
        this.thursday = in.readByte() != 0;
        this.friday = in.readByte() != 0;
        this.saturday = in.readByte() != 0;
        this.sunday = in.readByte() != 0;
        this.enabled = in.readByte() != 0;
        this.sms = in.readByte() != 0;
        this.phoneCalls = in.readByte() != 0;
        this.id = in.readInt();
    }

    public static final Creator<Profile> CREATOR = new Creator<Profile>() {
        public Profile createFromParcel(Parcel source) {
            return new Profile(source);
        }

        public Profile[] newArray(int size) {
            return new Profile[size];
        }
    };
}
