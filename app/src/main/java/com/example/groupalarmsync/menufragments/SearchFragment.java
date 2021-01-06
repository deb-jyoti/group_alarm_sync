package com.example.groupalarmsync.menufragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.groupalarmsync.R;

public class SearchFragment extends Fragment {

    SearchView friendSearch;
    TextView recentTextView;
    ListView searchHistoryListView;
    LinearLayout defaultSearchLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_2_search, container, false);

        friendSearch = view.findViewById(R.id.friendSearch);
        recentTextView = view.findViewById(R.id.recentTextView);
        searchHistoryListView = view.findViewById(R.id.searchHistoryListView);
        defaultSearchLayout = view.findViewById(R.id.defaultSearchLayout);

        return view;
    }
}