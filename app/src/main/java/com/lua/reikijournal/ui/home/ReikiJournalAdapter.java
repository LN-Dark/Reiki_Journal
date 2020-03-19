package com.lua.reikijournal.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.lua.reikijournal.EmptyRecyclerView;
import com.lua.reikijournal.R;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class ReikiJournalAdapter extends EmptyRecyclerView.Adapter<ReikiJournalAdapter.ReikiViewHolder> implements Filterable {
    private ArrayList<ReikiObject> reikiList, reikiListFull;
    private Context context;

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    reikiList = reikiListFull;
                } else {
                    ArrayList<ReikiObject> filteredList = new ArrayList<>();
                    for (ReikiObject row : reikiListFull) {

                        if (row.getData().toLowerCase().contains(charString.toLowerCase()) || row.getObservacao().contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }

                    reikiList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = reikiList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                reikiList = (ArrayList<ReikiObject>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public ReikiJournalAdapter(ArrayList<ReikiObject> reikiList) {
        this.reikiList = reikiList;
        this.reikiListFull = reikiList;
    }

    @NonNull
    @Override
    public ReikiJournalAdapter.ReikiViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.reiki_item, viewGroup, false);
        EmptyRecyclerView.LayoutParams lp = new EmptyRecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        context = viewGroup.getContext();
        return new ReikiJournalAdapter.ReikiViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ReikiJournalAdapter.ReikiViewHolder ReikiViewHolder, int i) {
        ReikiViewHolder.mData.setText(this.reikiList.get(i).data);
        ReikiViewHolder.mTitulo.setText(this.reikiList.get(i).observacao);

        ReikiViewHolder.mlayout.setOnClickListener(v -> {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
            LinearLayout layout = new LinearLayout(context);
            layout.setOrientation(LinearLayout.VERTICAL);

            builder.setIcon(context.getDrawable(R.mipmap.ic_launcher));
            builder.setTitle("Sessao de Reiki do dia " + reikiList.get(i).getData());
            layout.setGravity(Gravity.CENTER);
            final TextView espaco4 = new TextView(context);
            espaco4.setText("\n");
            espaco4.setTextSize(16);
            espaco4.setGravity(Gravity.CENTER);
            layout.addView(espaco4);
            final TextView espaco2 = new TextView(context);
            espaco2.setText(reikiList.get(i).observacao);
            espaco2.setTextSize(20);
            espaco2.setGravity(Gravity.CENTER);
            layout.addView(espaco2);
            builder.setView(layout);
            builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
            builder.setNeutralButton("Editar", (dialog, which) -> {
                MaterialAlertDialogBuilder builder1 = new MaterialAlertDialogBuilder(context);
                LinearLayout layout1 = new LinearLayout(context);
                layout1.setOrientation(LinearLayout.VERTICAL);
                builder.setIcon(context.getDrawable(R.mipmap.ic_launcher));
                builder1.setTitle("Editar sonho de " + reikiList.get(i).getData());
                layout1.setGravity(Gravity.CENTER);
                final EditText espaco41 = new EditText(context);
                espaco41.setText(reikiList.get(i).data);
                espaco41.setTextSize(16);
                espaco41.setGravity(Gravity.CENTER);
                layout1.addView(espaco41);
                final TextView espaco3 = new TextView(context);
                espaco3.setText("\n\n");
                layout1.addView(espaco3);
                final EditText espaco21 = new EditText(context);
                espaco21.setText(reikiList.get(i).observacao);
                espaco21.setTextSize(20);
                espaco21.setGravity(Gravity.CENTER);
                layout1.addView(espaco21);
                builder1.setView(layout1);
                builder1.setPositiveButton("Gravar", (dialog1, which1) -> {
                    reikiList.get(i).setData(espaco41.getText().toString());
                    reikiList.get(i).setObservacao(espaco21.getText().toString());
                    String NewSonhosSave = "";
                    for(int j = 0 ; j < reikiList.size(); j++){
                        NewSonhosSave = NewSonhosSave + "§" + reikiList.get(j).data + "»" + reikiList.get(j).observacao;
                    }
                    SharedPreferences.Editor editor = context.getSharedPreferences("ReikiJournal", MODE_PRIVATE).edit();
                    editor.putString("ReikiJournal", NewSonhosSave);
                    editor.apply();
                    notifyDataSetChanged();
                    dialog1.dismiss();
                });
                builder1.setNeutralButton("Apagar", (dialog12, which12) -> {
                    reikiList.remove(i);
                    String NewSonhosSave = "";
                    for(int j = 0 ; j < reikiList.size(); j++){
                        NewSonhosSave = NewSonhosSave + "§" + reikiList.get(j).data + "»" + reikiList.get(j).observacao;
                    }
                    SharedPreferences.Editor editor = context.getSharedPreferences("ReikiJournal", MODE_PRIVATE).edit();
                    editor.putString("ReikiJournal", NewSonhosSave);
                    editor.apply();
                    notifyDataSetChanged();
                    dialog12.dismiss();
                });
                builder1.show();
            });
            builder.show();
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return reikiList == null ? 0 : reikiList.size();
    }

    class ReikiViewHolder extends EmptyRecyclerView.ViewHolder {
        final TextView mData;
        final TextView mTitulo;
        final LinearLayout mlayout;

        ReikiViewHolder(View view) {
            super(view);
            mData = view.findViewById(R.id.data_reiki);
            mTitulo = view.findViewById(R.id.observaçao_reiki);
            mlayout = view.findViewById(R.id.layout_reiki_item);
        }
    }
}
