package com.lua.reikijournal.ui.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.lua.reikijournal.R;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class NovoReikiFragment extends Fragment implements DatePickerDialog.OnDateSetListener {
    private View root;
    private DatePickerDialog datePickerDialog ;
    private int Year, Month, Day ;
    private TextInputEditText dataSessao, oReiki;
    private MaterialButton btnStartMusica;
    private MaterialButton btnStopMusica;
    private Chronometer cmTimer;
    private MaterialButton btnStart;
    private MaterialButton btnStop;
    private MaterialButton btnReset;
    private Boolean resume = false;
    private long elapsedTime;
    private Uri uriSound;
    private MediaPlayer mp;

    public NovoReikiFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_novo_reiki, container, false);
        cmTimer = root.findViewById(R.id.cmTimer);
        btnStart = root.findViewById(R.id.btnStart_timer);
        btnStop = root.findViewById(R.id.btnStop_timer);
        btnReset = root.findViewById(R.id.btnReset_timer);
        MaterialButton btnEscolherMusica = root.findViewById(R.id.btnEscolher_musica);
        btnStartMusica = root.findViewById(R.id.btnStart_Musica);
        btnStopMusica = root.findViewById(R.id.btnStop_Musica);
        oReiki = root.findViewById(R.id.texto_reiki_novo_novo);
        btnStopMusica.setEnabled(false);
        btnStartMusica.setEnabled(false);
        btnStart.setEnabled(true);
        btnStop.setEnabled(false);
        btnReset.setEnabled(false);
        oReiki.setEnabled(false);
        btnStartMusica.setOnClickListener(v -> {
            if(uriSound.equals(Uri.EMPTY)){
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 10);
            }else {
                play(root.getContext().getApplicationContext(), uriSound);
                btnStopMusica.setEnabled(true);
                btnStartMusica.setEnabled(false);
            }
        });
        btnStopMusica.setOnClickListener(v -> {
            if(mp.isPlaying()){
                mp.stop();
                btnStopMusica.setEnabled(false);
                btnStartMusica.setEnabled(false);
            }
        });
        btnEscolherMusica.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 10);
        });

        cmTimer.setOnChronometerTickListener(arg0 -> {
            if (!resume) {
                elapsedTime = SystemClock.elapsedRealtime();
                oReiki.setText(getString(R.string.meditationtime) + " -> " + cmTimer.getText() + "\n\n\n");
            } else {
                elapsedTime = elapsedTime + 1000;
                oReiki.setText(getString(R.string.meditationtime) + " -> " + cmTimer.getText() + "\n\n\n");
            }
        });
        btnStart.setOnClickListener(v -> {
            btnStart.setEnabled(false);
            btnStop.setEnabled(true);
            if (!resume) {
                cmTimer.setBase(SystemClock.elapsedRealtime());
                cmTimer.start();
                oReiki.setText(getString(R.string.meditationtime) + " -> " + cmTimer.getText() + "\n\n\n");
            } else {
                cmTimer.start();
                oReiki.setText(getString(R.string.meditationtime) + " -> " + cmTimer.getText() + "\n\n\n");
            }
        });
        btnStop.setOnClickListener(v -> {
            btnStart.setEnabled(true);
            btnStop.setEnabled(false);
            btnReset.setEnabled(true);
            cmTimer.stop();
            resume = true;
            oReiki.setEnabled(true);
            btnStart.setText(getString(R.string.resume));
        });
        btnReset.setOnClickListener(v -> {
            cmTimer.stop();
            oReiki.setEnabled(true);
            cmTimer.setText("00:00");
            resume = false;
            btnStop.setEnabled(false);
        });
        Calendar calendar = Calendar.getInstance();
        Year = calendar.get(Calendar.YEAR) ;
        Month = calendar.get(Calendar.MONTH);
        Day = calendar.get(Calendar.DAY_OF_MONTH);
        MaterialButton btn_gravar_Sonho = root.findViewById(R.id.btn_gravar_sessaoreiki);
        dataSessao = root.findViewById(R.id.data_sessao_novo_novo);
        dataSessao.setText(Day + "-" + Month + "-" + Year);
        TextInputLayout dataSessaoLayout = root.findViewById(R.id.titulo_sonho_novo);
        dataSessao.setOnClickListener(view -> {
            datePickerDialog = DatePickerDialog.newInstance(NovoReikiFragment.this, Year, Month, Day);
            datePickerDialog.setThemeDark(true );
            datePickerDialog.showYearPickerFirst(false);
            datePickerDialog.setAccentColor(Color.parseColor("#FF5722"));
            datePickerDialog.setTitle("Escolhe a data");
            datePickerDialog.show(getChildFragmentManager(), "DatePickerDialog");
        });
        dataSessaoLayout.setOnClickListener(view -> {
            datePickerDialog = DatePickerDialog.newInstance(NovoReikiFragment.this, Year, Month, Day);
            datePickerDialog.setThemeDark(true );
            datePickerDialog.showYearPickerFirst(false);
            datePickerDialog.setAccentColor(Color.parseColor("#FF5722"));
            datePickerDialog.setTitle("Escolhe a data");
            datePickerDialog.show(getChildFragmentManager(), "DatePickerDialog");
        });
        btn_gravar_Sonho.setOnClickListener(v -> {
            if(!dataSessao.getText().equals(getString(R.string.escolheradata))){
                    if(!oReiki.getText().toString().equals("")){
                        SharedPreferences prefs = root.getContext().getSharedPreferences("ReikiJournal", MODE_PRIVATE);
                        String SonhosFull = prefs.getString("ReikiJournal", "");
                        SharedPreferences.Editor editor = root.getContext().getSharedPreferences("ReikiJournal", MODE_PRIVATE).edit();
                        editor.putString("ReikiJournal", SonhosFull + dataSessao.getText().toString() + "»" + oReiki.getText().toString() + "§");
                        editor.apply();
                        Toast.makeText(root.getContext(), getString(R.string.sessaogravadacomsucesso), Toast.LENGTH_LONG).show();
                        dataSessao.setText(Day + "-" + Month + "-" + Year);
                        oReiki.setText("");
                        oReiki.setEnabled(false);
                        cmTimer.stop();
                        oReiki.setEnabled(false);
                        cmTimer.setText("00:00");
                        resume = false;
                        btnStop.setEnabled(false);
                    }else {
                        Toast.makeText(NovoReikiFragment.this.getContext(), getString(R.string.escreveaobservacao), Toast.LENGTH_LONG).show();
                    }
            }else {
                Toast.makeText(NovoReikiFragment.this.getContext(), getString(R.string.escolheadata), Toast.LENGTH_LONG).show();
            }
        });
        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK && requestCode == 10){
            uriSound=data.getData();
            btnStartMusica.setEnabled(true);
        }
    }

    private void play(Context context, Uri uri) {
        try {
            mp=MediaPlayer.create(context,uri);
            mp.setOnPreparedListener(mediaPlayer -> mediaPlayer.start());
            mp.prepareAsync();
        } catch (IllegalArgumentException | SecurityException | IllegalStateException e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDateSet(DatePickerDialog view, int Year, int Month, int Day) {
        dataSessao.setText(Day + "-" + (Month + 1) + "-" + Year);
    }
}
