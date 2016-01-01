package com.estevez95gmail.f.primarynotifications;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import java.util.ArrayList;

/**
 * Created by Freddy Estevez on 10/4/15.
 * This class is used to select which days this app will run on.
 */
public class SelectDialog extends DialogFragment {
    CharSequence[] cons;
    // The contacts user has selected
    ArrayList<String> mSelectedItems;
    // all contacts
    static String[] days = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
    Profile p;
    boolean isSelected[];

    public SelectDialog(Profile p) {
        this.p = p;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //just checking
        isSelected = new boolean[7];

        cons = days;
        mSelectedItems = new ArrayList<>();
        getSelected();
        // Where we track the selected items
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Set the dialog title
        builder.setTitle("Select Days")
                // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected


                .setMultiChoiceItems(cons, isSelected,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which,
                                                boolean isChecked) {
                                if (isChecked) {
                                    // If the user checked the item, add it to the selected items
                                    mSelectedItems.add(days[which]);
                                } else if (mSelectedItems.contains(days[which])) {
                                    // Else, if the item is already in the array, remove it
                                    String day = days[which];
                                    switch (day) {
                                        case "Monday":
                                            p.setMonday(false);
                                            break;
                                        case "Tuesday":
                                            p.setTuesday(false);
                                            break;
                                        case "Wednesday":
                                            p.setWednesday(false);
                                            break;
                                        case "Thursday":
                                            p.setThursday(false);
                                            break;
                                        case "Friday":
                                            p.setFriday(false);
                                            break;
                                        case "Saturday":
                                            p.setSaturday(false);
                                            break;
                                        case "Sunday":
                                            p.setSunday(false);
                                            break;

                                    }
                                    mSelectedItems.remove(mSelectedItems.indexOf(days[which]));
                                }


                            }

                        })
                        // Set the action buttons
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK, so save the mSelectedItems results somewhere
                        // or return them to the component that opened the dialog

                        for (int i = 0; i < mSelectedItems.size(); i++) {
                            String day = mSelectedItems.get(i);
                            switch (day) {
                                case "Monday":
                                    p.setMonday(true);
                                    break;
                                case "Tuesday":
                                    p.setTuesday(true);
                                    break;
                                case "Wednesday":
                                    p.setWednesday(true);
                                    break;
                                case "Thursday":
                                    p.setThursday(true);
                                    break;
                                case "Friday":
                                    p.setFriday(true);
                                    break;
                                case "Saturday":
                                    p.setSaturday(true);
                                    break;
                                case "Sunday":
                                    p.setSunday(true);
                                    break;

                            }

                        }

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


    private void getSelected() {
        isSelected[0] = p.isSunday();
        isSelected[1] = p.isMonday();
        isSelected[2] = p.isTuesday();
        isSelected[3] = p.isWednesday();
        isSelected[4] = p.isThursday();
        isSelected[5] = p.isFriday();
        isSelected[6] = p.isSaturday();

        if(p.isSunday())
            mSelectedItems.add(days[0]);
        if(p.isMonday())
            mSelectedItems.add(days[1]);
        if(p.isTuesday())
            mSelectedItems.add(days[2]);
        if(p.isWednesday())
            mSelectedItems.add(days[3]);
        if(p.isThursday())
            mSelectedItems.add(days[4]);
        if(p.isFriday())
            mSelectedItems.add(days[5]);
        if (p.isSaturday())
            mSelectedItems.add(days[6]);


    }


}
