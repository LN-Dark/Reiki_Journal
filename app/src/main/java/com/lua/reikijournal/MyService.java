package com.lua.reikijournal;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class MyService extends JobService {
    public MyService() {
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        startAlarm();
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startAlarm();
    }

    private void startAlarm() {
        AlarmManager manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent myIntent;
        PendingIntent pendingIntent;
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("ReikiJournal_Clock", MODE_PRIVATE);
        String HorasClock = prefs.getString("ReikiJournal_Clock", "");
        if(!HorasClock.equals("")){
            String dayClock = prefs.getString("ReikiJournal_Clock_dayClock", "");
            String monthClock = prefs.getString("ReikiJournal_Clock_monthClock", "");
            String yearClock = prefs.getString("ReikiJournal_Clock_yearClock", "");
            Calendar Datecompare = Calendar.getInstance();
            Datecompare.set(Integer.parseInt(yearClock),Integer.parseInt(monthClock),Integer.parseInt(dayClock));
            String[] clockDivided = HorasClock.split(":");
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(clockDivided[0]));
            calendar.set(Calendar.MINUTE, Integer.parseInt(clockDivided[1]));
            boolean sameDay = Datecompare.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) && Datecompare.get(Calendar.DAY_OF_YEAR) == calendar.get(Calendar.DAY_OF_YEAR);
            if(!sameDay){
                myIntent = new Intent(this, AlarmReceiver.class);
                pendingIntent = PendingIntent.getBroadcast(this,0,myIntent,0);
                manager.setAlarmClock(new AlarmManager.AlarmClockInfo(calendar.getTimeInMillis(), pendingIntent), pendingIntent);
            }else if(Datecompare.getTime().equals(calendar.getTime())) {
                myIntent = new Intent(this, AlarmReceiver.class);
                pendingIntent = PendingIntent.getBroadcast(this,0,myIntent,0);
                manager.setAlarmClock(new AlarmManager.AlarmClockInfo(calendar.getTimeInMillis(), pendingIntent), pendingIntent);
            }else if(Datecompare.getTime().before(calendar.getTime())) {
                myIntent = new Intent(this, AlarmReceiver.class);
                pendingIntent = PendingIntent.getBroadcast(this,0,myIntent,0);
                manager.setAlarmClock(new AlarmManager.AlarmClockInfo(calendar.getTimeInMillis(), pendingIntent), pendingIntent);
            }
        }
    }
}
