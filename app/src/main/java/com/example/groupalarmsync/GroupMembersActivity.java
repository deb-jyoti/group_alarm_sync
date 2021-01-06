package com.example.groupalarmsync;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

public class GroupMembersActivity extends AppCompatActivity {

    ImageView groupImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_members);

        groupImage = findViewById(R.id.groupImage);

    }
}