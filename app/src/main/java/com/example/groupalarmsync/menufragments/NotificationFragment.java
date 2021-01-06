package com.example.groupalarmsync.menufragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.groupalarmsync.R;

public class NotificationFragment extends Fragment {

    RecyclerView notifyListView;
    LinearLayout defaultNotifyLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_4_notification, container, false);

        notifyListView = view.findViewById(R.id.notifyListView);
        defaultNotifyLayout = view.findViewById(R.id.defaultNotifyLayout);

        return view;
    }
}