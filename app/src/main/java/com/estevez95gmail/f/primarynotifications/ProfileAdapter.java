package com.estevez95gmail.f.primarynotifications;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by Freddy Estevez on 12/30/15.
 */
public class ProfileAdapter extends ArrayAdapter<Profile> {
    protected Context mContext;
    protected List<Profile> mProfiles;

    public ProfileAdapter(Context context, List<Profile> profiles) {
        super(context, R.layout.profile_view, profiles);

        mContext = context;
        mProfiles = profiles;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        ViewHolder holder;
        if(convertView == null){
            //do stuff in video

            convertView = LayoutInflater.from(mContext).inflate(R.layout.profile_view, null);
            holder = new ViewHolder();
            holder.enabled = (CheckBox) convertView.findViewById(R.id.confirmBox);
            holder.enabled.setEnabled(true);
            holder.endTime = (TextView) convertView.findViewById(R.id.endTime);
            holder.startTime = (TextView) convertView.findViewById(R.id.startTime);
            holder.mon = (TextView) convertView.findViewById(R.id.Mon);
            holder.tue = (TextView) convertView.findViewById(R.id.Tue);
            holder.wed = (TextView) convertView.findViewById(R.id.Wed);
            holder.thur = (TextView) convertView.findViewById(R.id.Thur);
            holder.fri = (TextView) convertView.findViewById(R.id.Fri);
            holder.sat = (TextView) convertView.findViewById(R.id.Sat);
            holder.sun = (TextView) convertView.findViewById(R.id.Sun);
            convertView.setTag(holder);

        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        if(mProfiles.isEmpty())
            return convertView;
        Profile currentProfile = mProfiles.get(position);
        holder.startTime.setText(currentProfile.getStartTime());
        holder.endTime.setText(currentProfile.getEndTime());

        if(!(currentProfile.isMonday())){
            holder.mon.setTextColor(Color.GRAY);
        }else{
            SpannableString content = new SpannableString(holder.mon.getText());
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            holder.mon.setText(content);
        }

        if(!(currentProfile.isTuesday())){
            holder.tue.setTextColor(Color.GRAY);
        }else{
            SpannableString content = new SpannableString(holder.tue.getText());
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            holder.tue.setText(content);
        }

        if(!(currentProfile.isWednesday())){
            holder.wed.setTextColor(Color.GRAY);
        }else{
            SpannableString content = new SpannableString(holder.wed.getText());
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            holder.wed.setText(content);
        }

        if(!(currentProfile.isThursday())){
            holder.thur.setTextColor(Color.GRAY);
        }else{
            SpannableString content = new SpannableString(holder.thur.getText());
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            holder.thur.setText(content);
        }

        if(!(currentProfile.isFriday())){
            holder.fri.setTextColor(Color.GRAY);
        }else{
            SpannableString content = new SpannableString(holder.fri.getText());
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            holder.fri.setText(content);
        }

        if(!(currentProfile.isSaturday())){
            holder.sat.setTextColor(Color.GRAY);
        }else{
            SpannableString content = new SpannableString(holder.sat.getText());
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            holder.sat.setText(content);
        }

        if(!(currentProfile.isSunday())){
            holder.sun.setTextColor(Color.GRAY);
        }else{
            SpannableString content = new SpannableString(holder.sun.getText());
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            holder.sun.setText(content);
        }

        return convertView;
    }

    public static class ViewHolder{
        TextView startTime;
        TextView endTime;
        TextView sun, mon, tue, wed, thur, fri, sat;
        CheckBox enabled;
    }
}
