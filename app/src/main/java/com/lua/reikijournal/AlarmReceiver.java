package com.lua.reikijournal;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int reqCode = 1;
        Intent intent1 = new Intent(context.getApplicationContext(), MainActivity.class);
        showNotification(context, context.getString(R.string.meditar), context.getString(R.string.jasaohorasdemeditar), intent1, reqCode);
    }

    public void showNotification(Context context, String title, String message, Intent intent, int reqCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "ReikiJournal_channel";;
            String description = context.getString(R.string.notificacaodealarmeparanaoesquecermeditar);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("ReikiJournal_channel", name, importance);
            channel.setDescription(description);
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            channel.enableLights(true);
            channel.setSound(alarmSound, audioAttributes);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context.getApplicationContext(), 0, intent, 0);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context.getApplicationContext(), "ReikiJournal_channel")
                .setSmallIcon(R.drawable.reiki_icon)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVibrate(new long[]{0, 500, 1000})
                .setSound(alarmSound)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context.getApplicationContext());
        notificationManager.notify(reqCode, builder.build());
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        SharedPreferences.Editor editor = context.getSharedPreferences("ReikiJournal_Clock", MODE_PRIVATE).edit();
        editor.putString("ReikiJournal_Clock_dayClock", String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
        editor.putString("ReikiJournal_Clock_monthClock", String.valueOf(calendar.get(Calendar.MONTH)));
        editor.putString("ReikiJournal_Clock_yearClock", String.valueOf(calendar.get(Calendar.YEAR)));
        editor.apply();
    }
}
