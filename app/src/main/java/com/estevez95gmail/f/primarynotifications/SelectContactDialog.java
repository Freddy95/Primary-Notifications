package com.estevez95gmail.f.primarynotifications;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Freddy Estevez on 9/22/15.
 * This class allows user to select contacts for this profile.
 */
public class SelectContactDialog extends DialogFragment{
    CharSequence[] cons;
    // The contacts user has selected
    ArrayList<Contact>mSelectedItems;
    // all contacts
    static ArrayList<Contact> contacts;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mSelectedItems = new ArrayList<>();
          // Where we track the selected items

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Set the dialog title
        builder.setTitle("Select Contacts")
                // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected
                .setMultiChoiceItems(cons, null,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which,
                                                boolean isChecked) {
                                if (isChecked) {
                                    // If the user checked the item, add it to the selected items
                                        mSelectedItems.add(contacts.get(which));
                                } else if (mSelectedItems.contains(which)) {
                                    // Else, if the item is already in the array, remove it
                                    mSelectedItems.remove(mSelectedItems.indexOf(which));
                                }
                            }
                        })
                        // Set the action buttons
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK, so save the mSelectedItems results somewhere
                        // or return them to the component that opened the dialog

                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dismiss();
                    }
                });

        return builder.create();
    }
    /*
     * Adds Contacts to the Array cons to be displayed
     */
    public void add(ArrayList<Contact> contactList){
        contacts = new ArrayList<>();
        contacts = contactList;
        cons = new CharSequence[contacts.size()];
        for(int i = 0; i < cons.length; i++){
            cons[i] = contacts.get(i).getName();
        }
    }

    public void addDays(ArrayList<String> days){
        cons = new CharSequence[days.size()];
        for(int i = 0; i < cons.length; i++){
            cons[i] = days.get(i);
        }
    }


}
