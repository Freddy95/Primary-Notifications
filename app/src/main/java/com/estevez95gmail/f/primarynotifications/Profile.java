package com.estevez95gmail.f.primarynotifications;

import java.util.ArrayList;

/**
 * Contains the times and days of the week for this app to be active
 */
public class Profile {

    ArrayList<Contact> selected = new ArrayList<>();
    int startHour, endHour, startMinute, endMinute;


    public Profile(ArrayList<Contact> selected, int startHour, int endHour, int startMinute, int endMinute) {
        this.selected = selected;
        this.startHour = startHour;
        this.endHour = endHour;
        this.startMinute = startMinute;
        this.endMinute = endMinute;
    }
}
