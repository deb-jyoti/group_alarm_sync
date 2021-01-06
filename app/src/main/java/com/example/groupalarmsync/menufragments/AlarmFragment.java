package com.example.groupalarmsync.menufragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.groupalarmsync.LoginActivity;
import com.example.groupalarmsync.R;
import com.google.firebase.auth.FirebaseAuth;

public class AlarmFragment extends Fragment implements View.OnClickListener {

    LinearLayout menuBox;
    TextView alarmStatus;
    ListView alarmListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_1_alarm, container, false);

        menuBox = view.findViewById(R.id.menuBox);

        view.findViewById(R.id.menuBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(menuBox.getVisibility() == View.GONE)
                    menuBox.setVisibility(View.VISIBLE);
                else
                    menuBox.setVisibility(View.GONE);
            }
        });

//        view.findViewById(R.id.addAlarmBtn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });

//        view.findViewById(R.id.deleteAlarmsTextView).setOnClickListener(this);
//        view.findViewById(R.id.leaveGroupsTextView).setOnClickListener(this);
//        view.findViewById(R.id.aboutAppTextView).setOnClickListener(this);
        view.findViewById(R.id.logOutTextView).setOnClickListener(this);

        return view;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
//            case R.id.deleteAlarmsTextView:
//
//                break;
//
//            case R.id.leaveGroupsTextView:
//
//                break;
//
//            case R.id.aboutAppTextView:
//
//                break;

            case R.id.logOutTextView:
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
        }
    }
}