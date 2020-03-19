package com.lua.reikijournal.ui.meditar;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.lua.reikijournal.AlarmReceiver;
import com.lua.reikijournal.DeviceBootReceiver;
import com.lua.reikijournal.MainActivity;
import com.lua.reikijournal.MyService;
import com.lua.reikijournal.R;

import java.io.IOException;
import java.util.Calendar;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.MODE_PRIVATE;

public class MeditarFragment extends Fragment {
    private Chronometer cmTimer;
    private MaterialButton btnStart;
    private MaterialButton btnStop;
    private Boolean resume = false;
    private long elapsedTime;
    private View root;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_meditar, container, false);
        cmTimer = root.findViewById(R.id.cmTimer);
        btnStart = root.findViewById(R.id.btnStart_timer);
        btnStop = root.findViewById(R.id.btnStop_timer);
        TimePicker picker= root.findViewById(R.id.datePicker1);
        picker.setIs24HourView(true);
        SharedPreferences prefs = root.getContext().getSharedPreferences("ReikiJournal_Clock", MODE_PRIVATE);
        String clockhourset = prefs.getString("ReikiJournal_Clock", "");
        if(!clockhourset.equals("")){
            String[] clockhoursetDivided = clockhourset.split(":");
            picker.setHour(Integer.parseInt(clockhoursetDivided[0]));
            picker.setMinute(Integer.parseInt(clockhoursetDivided[1]));
        }
        MaterialButton btnProgramarRelogio = root.findViewById(R.id.btnprogramclock);
        MaterialButton btnReset = root.findViewById(R.id.btnReset_timer);
        MaterialButton btnEscolherMusica = root.findViewById(R.id.btnEscolher_musica);
        MaterialButton btnStartMusica = root.findViewById(R.id.btnStart_Musica);
        MaterialButton btnStopMusica = root.findViewById(R.id.btnStop_Musica);
        btnStartMusica.setOnClickListener(v -> {
            if(uriSound.equals(Uri.EMPTY)){
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 10);
            }else {
                play(root.getContext().getApplicationContext(), uriSound);
            }
        });
        btnStopMusica.setOnClickListener(v -> {
            if(mp.isPlaying()){
                mp.stop();
            }
        });
        btnEscolherMusica.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 10);
        });
        btnProgramarRelogio.setOnClickListener(v -> {
            int hour, minute;
            hour = picker.getHour();
            minute = picker.getMinute();
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            AlarmManager alarmMgr;
            PendingIntent alarmIntent;
            alarmMgr = (AlarmManager)root.getContext().getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(root.getContext(), AlarmReceiver.class);
            alarmIntent = PendingIntent.getBroadcast(root.getContext(), 0, intent, 0);
            alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, alarmIntent);
            ComponentName receiver = new ComponentName(root.getContext(), DeviceBootReceiver.class);
            PackageManager pm = root.getContext().getPackageManager();
            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);
            SharedPreferences.Editor editor = root.getContext().getSharedPreferences("ReikiJournal_Clock", MODE_PRIVATE).edit();
                editor.putString("ReikiJournal_Clock", hour + ":" + minute);
                editor.apply();
                    JobScheduler jobScheduler = (JobScheduler) getActivity().getSystemService(Context.JOB_SCHEDULER_SERVICE);
                    JobInfo jobInfo = new JobInfo.Builder(11, new ComponentName(root.getContext(), MyService.class))
                            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                            .build();
                    jobScheduler.schedule(jobInfo);
                Toast.makeText(getContext(), getString(R.string.relogioprogramado), Toast.LENGTH_LONG).show();
        });
        cmTimer.setOnChronometerTickListener(arg0 -> {
            if (!resume) {
                elapsedTime = SystemClock.elapsedRealtime();
            } else {
                elapsedTime = elapsedTime + 1000;
            }
        });
        btnStart.setOnClickListener(v -> {
            btnStart.setEnabled(false);
            btnStop.setEnabled(true);

            if (!resume) {
                cmTimer.setBase(SystemClock.elapsedRealtime());
                cmTimer.start();
            } else {
                cmTimer.start();
            }
        });
        btnStop.setOnClickListener(v -> {
            btnStart.setEnabled(true);
            btnStop.setEnabled(false);
            cmTimer.stop();
            resume = true;
            btnStart.setText(getString(R.string.resume));
        });
        btnReset.setOnClickListener(v -> {
            cmTimer.stop();
            cmTimer.setText("00:00");
            resume = false;
            btnStop.setEnabled(false);
        });
        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode == RESULT_OK && requestCode == 10){
            uriSound=data.getData();
            play(root.getContext(), uriSound);
        }
    }
    private Uri uriSound;
    private MediaPlayer mp;
    private void play(Context context, Uri uri) {
        try {
            mp=MediaPlayer.create(context,uri);
            mp.setOnPreparedListener(mediaPlayer -> mediaPlayer.start());
            mp.prepareAsync();
        } catch (IllegalArgumentException | SecurityException | IllegalStateException e) {
            e.printStackTrace();
        }
    }

}
