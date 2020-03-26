package com.lua.reikijournal.ui.meditar;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.lua.reikijournal.AlarmReceiver;
import com.lua.reikijournal.DeviceBootReceiver;
import com.lua.reikijournal.MyService;
import com.lua.reikijournal.R;

import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;

public class TimePickerFragment extends DialogFragment {
    private TimePicker timePicker;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_date,null);

        timePicker = v.findViewById(R.id.dialog_time_picker);
        timePicker.setIs24HourView(true);
        return new MaterialAlertDialogBuilder(getActivity())
                .setView(v)
                .setTitle(getString(R.string.escolheahora))
                .setIcon(getContext().getDrawable(R.drawable.reiki_icon))
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int hour = 0;
                                hour = timePicker.getHour();
                                int minute = 0;
                                minute = timePicker.getMinute();
                                updateTime(hour, minute);
                                dismiss();
                            }
                        })
                .create();
    }

    private void updateTime(int hours, int mins) {
        int hour, minute;
        hour = hours;
        minute = mins;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        AlarmManager manager = (AlarmManager)getContext().getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent myIntent;
        PendingIntent pendingIntent;
        myIntent = new Intent(getContext().getApplicationContext(), AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(getContext().getApplicationContext(),0,myIntent,0);
        manager.setAlarmClock(new AlarmManager.AlarmClockInfo(calendar.getTimeInMillis(), pendingIntent), pendingIntent);
        ComponentName receiver = new ComponentName(getContext(), DeviceBootReceiver.class);
        PackageManager pm = getContext().getPackageManager();
        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        SharedPreferences.Editor editor = getContext().getSharedPreferences("ReikiJournal_Clock", MODE_PRIVATE).edit();
        editor.putString("ReikiJournal_Clock", hour + ":" + minute);
        editor.putString("ReikiJournal_Clock_dayClock", String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
        editor.putString("ReikiJournal_Clock_monthClock", String.valueOf(calendar.get(Calendar.MONTH)));
        editor.putString("ReikiJournal_Clock_yearClock", String.valueOf(calendar.get(Calendar.YEAR)));
        editor.apply();
        JobScheduler jobScheduler = (JobScheduler) getActivity().getSystemService(Context.JOB_SCHEDULER_SERVICE);
        JobInfo jobInfo = new JobInfo.Builder(11, new ComponentName(getContext(), MyService.class))
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .build();
        jobScheduler.schedule(jobInfo);
        Toast.makeText(getContext(), getString(R.string.relogioprogramado), Toast.LENGTH_LONG).show();
    }
}
