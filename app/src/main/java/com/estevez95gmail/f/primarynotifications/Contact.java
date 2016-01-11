package com.estevez95gmail.f.primarynotifications;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Comparator;

/**
 * Created by Freddy Estevez on 9/13/15.
 */
public class Contact implements Parcelable {

    String name, phoneNumber;

    public Contact(String name, String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getName() {
        return name;
    }

    public static Comparator<Contact> contactNameComp = new Comparator<Contact>() {

        public int compare(Contact s1, Contact s2) {
            String ContactName1 = s1.getName().toUpperCase();
            String ContactName2 = s2.getName().toUpperCase();

            //ascending order
            return ContactName1.compareTo(ContactName2);


        }
    };

    public boolean equals(Contact c) {
        if (c == null)
            return false;
        return (c.getName().equals(name) && c.getPhoneNumber().equals(phoneNumber));
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.phoneNumber);
    }

    protected Contact(Parcel in) {
        this.name = in.readString();
        this.phoneNumber = in.readString();
    }

    public static final Parcelable.Creator<Contact> CREATOR = new Parcelable.Creator<Contact>() {
        public Contact createFromParcel(Parcel source) {
            return new Contact(source);
        }

        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };
}
