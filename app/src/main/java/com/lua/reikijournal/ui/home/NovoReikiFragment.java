package com.lua.reikijournal.ui.home;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.lua.reikijournal.R;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;

public class NovoReikiFragment extends Fragment implements DatePickerDialog.OnDateSetListener {
    private View root;
    private MaterialButton btn_gravar_Sonho;
    private DatePickerDialog datePickerDialog ;
    private int Year, Month, Day ;
    private TextInputEditText dataSessao, oReiki;
    private TextInputLayout dataSessaoLayout;

    public NovoReikiFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_novo_reiki, container, false);
        Calendar calendar = Calendar.getInstance();
        Year = calendar.get(Calendar.YEAR) ;
        Month = calendar.get(Calendar.MONTH);
        Day = calendar.get(Calendar.DAY_OF_MONTH);
        btn_gravar_Sonho = root.findViewById(R.id.btn_gravar_sessaoreiki);
        dataSessao = root.findViewById(R.id.data_sessao_novo_novo);
        dataSessaoLayout = root.findViewById(R.id.titulo_sonho_novo);
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

        oReiki = root.findViewById(R.id.texto_reiki_novo_novo);
        btn_gravar_Sonho.setOnClickListener(v -> {
            if(!dataSessao.getText().equals("Escolher data")){

                    if(!oReiki.getText().toString().equals("")){
                        SharedPreferences prefs = root.getContext().getSharedPreferences("ReikiJournal", MODE_PRIVATE);
                        String SonhosFull = prefs.getString("ReikiJournal", "");
                        SharedPreferences.Editor editor = root.getContext().getSharedPreferences("ReikiJournal", MODE_PRIVATE).edit();
                        editor.putString("ReikiJournal", SonhosFull + dataSessao.getText().toString() + "»" + oReiki.getText().toString() + "§");
                        editor.apply();
                        Toast.makeText(root.getContext(), "Sessao de Reiki gravada com sucesso.", Toast.LENGTH_LONG).show();
                        dataSessao.setText("");
                        oReiki.setText("");
                    }else {
                        Toast.makeText(NovoReikiFragment.this.getContext(), "Escreve a observaçao", Toast.LENGTH_LONG).show();
                    }
            }else {
                Toast.makeText(NovoReikiFragment.this.getContext(), "Escolhe a data da sessao", Toast.LENGTH_LONG).show();
            }
        });
        return root;
    }

    @Override
    public void onDateSet(DatePickerDialog view, int Year, int Month, int Day) {
        dataSessao.setText(Day + "-" + (Month + 1) + "-" + Year);
    }
}
