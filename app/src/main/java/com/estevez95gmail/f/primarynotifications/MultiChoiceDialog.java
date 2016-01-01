package com.estevez95gmail.f.primarynotifications;

import android.app.Activity;
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
public class MultiChoiceDialog extends DialogFragment{
    CharSequence[] cons;
    // The contacts user has selected
    ArrayList<Contact> mSelectedItems;
    // all contacts
    static ArrayList<Contact> contacts;

    //The days user has selected
    ArrayList<String> dSelectedItems;
    //All days
    static String[] days = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

    boolean  selectedItems[];


    Profile profile;
   OnDialogDismissListener mCallBack;
    boolean contact;

    public interface OnDialogDismissListener {
        public void onDialogDismissListener(int position);
    }

    public MultiChoiceDialog(Profile p, boolean contact) {

        //Profile to edit.
        profile = p;
        //Check to see if this dialog should be used for selected days
        // or contacts.
        this.contact = contact;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallBack = (OnDialogDismissListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnDialogDismissListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Where we track the selected items

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        if(contact) {//We are selecting between contacts and not days

            builder.setTitle("Select Contacts")


                    // Specify the list array, the items to be selected by default (null for none),
                    // and the listener through which to receive callbacks when items are selected
                    .setMultiChoiceItems(cons, selectedItems,
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

                            profile.setSelected(mSelectedItems);
                            mCallBack.onDialogDismissListener(2);
                            dismiss();

                        }
                    })
                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            dismiss();
                        }
                    });
        }else{//Selecting days
            selectedItems = new boolean[7];

            cons = days;

            dSelectedItems = new ArrayList<>();
            getSelected();
            builder.setTitle("Select Days")
                    // Specify the list array, the items to be selected by default (null for none),
                    // and the listener through which to receive callbacks when items are selected


                    .setMultiChoiceItems(cons, selectedItems,
                            new DialogInterface.OnMultiChoiceClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which,
                                                    boolean isChecked) {
                                    if (isChecked) {
                                        // If the user checked the item, add it to the selected items
                                        dSelectedItems.add(days[which]);
                                    } else if (dSelectedItems.contains(days[which])) {
                                        // Else, if the item is already in the array, remove it
                                        String day = days[which];
                                        switch (day) {
                                            case "Monday":
                                                profile.setMonday(false);
                                                break;
                                            case "Tuesday":
                                                profile.setTuesday(false);
                                                break;
                                            case "Wednesday":
                                                profile.setWednesday(false);
                                                break;
                                            case "Thursday":
                                                profile.setThursday(false);
                                                break;
                                            case "Friday":
                                                profile.setFriday(false);
                                                break;
                                            case "Saturday":
                                                profile.setSaturday(false);
                                                break;
                                            case "Sunday":
                                                profile.setSunday(false);
                                                break;
                                            default:
                                                break;

                                        }
                                        dSelectedItems.remove(dSelectedItems.indexOf(days[which]));
                                    }


                                }

                            })
                            // Set the action buttons
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            // User clicked OK, so save the mSelectedItems results somewhere
                            // or return them to the component that opened the dialog

                            for (int i = 0; i < dSelectedItems.size(); i++) {
                                String day = dSelectedItems.get(i);
                                switch (day) {
                                    case "Monday":
                                        profile.setMonday(true);
                                        break;
                                    case "Tuesday":
                                        profile.setTuesday(true);
                                        break;
                                    case "Wednesday":
                                        profile.setWednesday(true);
                                        break;
                                    case "Thursday":
                                        profile.setThursday(true);
                                        break;
                                    case "Friday":
                                        profile.setFriday(true);
                                        break;
                                    case "Saturday":
                                        profile.setSaturday(true);
                                        break;
                                    case "Sunday":
                                        profile.setSunday(true);
                                        break;
                                    default:
                                        break;
                                }

                            }
                            mCallBack.onDialogDismissListener(2);
                            dismiss();

                        }
                    })

                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            dismiss();
                        }
                    });

        }
        return builder.create();

    }
    /**
     * Adds Contacts to the Array cons to be displayed
     */
    public void addContacts(ArrayList<Contact> contactList){
        contacts = new ArrayList<>();
        mSelectedItems = new ArrayList<>();
        contacts = contactList;
        cons = new CharSequence[contacts.size()];
        selectedItems = new boolean[contacts.size()];
        for(int i = 0; i < cons.length; i++){
            cons[i] = contacts.get(i).getName();
            if(profile.getSelected().contains(contacts.get(i))){
                selectedItems[i] = true;
                mSelectedItems.add(contacts.get(i));
            }else{
                selectedItems[i] = false;
            }
        }
    }


    private void getSelected() {
        selectedItems[0] = profile.isSunday();
        selectedItems[1] = profile.isMonday();
        selectedItems[2] = profile.isTuesday();
        selectedItems[3] = profile.isWednesday();
        selectedItems[4] = profile.isThursday();
        selectedItems[5] = profile.isFriday();
        selectedItems[6] = profile.isSaturday();

        if(profile.isSunday())
            dSelectedItems.add(days[0]);
        if(profile.isMonday())
            dSelectedItems.add(days[1]);
        if(profile.isTuesday())
            dSelectedItems.add(days[2]);
        if(profile.isWednesday())
            dSelectedItems.add(days[3]);
        if(profile.isThursday())
            dSelectedItems.add(days[4]);
        if(profile.isFriday())
            dSelectedItems.add(days[5]);
        if (profile.isSaturday())
            dSelectedItems.add(days[6]);


    }



}
