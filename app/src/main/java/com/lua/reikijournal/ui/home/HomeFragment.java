package com.lua.reikijournal.ui.home;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.lua.reikijournal.EmptyRecyclerView;
import com.lua.reikijournal.MainActivity;
import com.lua.reikijournal.R;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment {
    private ArrayList<ReikiObject> ReikiJournalList;
    private ReikiJournalAdapter mReikiAdapter;
    private View root;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_reiki, container, false);
        SharedPreferences prefs = root.getContext().getSharedPreferences("ReikiJournal", MODE_PRIVATE);
        String SonhosFull = prefs.getString("ReikiJournal", "");
        String[] SonhosDivided = SonhosFull.split("§");

        ReikiJournalList = new ArrayList<>();
        for(int i = 0 ; i < SonhosDivided.length; i++){
            ReikiObject reikiNew = new ReikiObject();
            String[] Reiki = SonhosDivided[i].split("»");
            if(Reiki[0].length() > 2){
                reikiNew.setId(String.valueOf(i));
                reikiNew.setData(Reiki[0]);
                reikiNew.setObservacao(Reiki[1]);
                ReikiJournalList.add(reikiNew);
            }
        }

        InitializeRecyclerView();
        mReikiAdapter.notifyDataSetChanged();
        return root;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        menu.findItem(R.id.action_search).setVisible(true);
        searchView.setImeOptions(searchView.getImeOptions() | EditorInfo.IME_ACTION_SEARCH | EditorInfo.IME_FLAG_NO_EXTRACT_UI | EditorInfo.IME_FLAG_NO_FULLSCREEN);
        searchView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mReikiAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    private void InitializeRecyclerView() {
        EmptyRecyclerView mReikiList = root.findViewById(R.id.reikiJournalRecycler);
        mReikiList.setNestedScrollingEnabled(false);
        mReikiList.setHasFixedSize(false);
        Display display = this.getActivity().getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        LinearLayoutManager lLayout;
        lLayout = new LinearLayoutManager(root.getContext(), EmptyRecyclerView.VERTICAL, false);
        mReikiList.setLayoutManager(lLayout);
        mReikiAdapter = new ReikiJournalAdapter(ReikiJournalList);
        mReikiList.setAdapter(mReikiAdapter);
    }
}
