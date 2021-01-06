package com.example.groupalarmsync;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.groupalarmsync.menufragments.AlarmFragment;
import com.example.groupalarmsync.menufragments.SearchFragment;
import com.example.groupalarmsync.menufragments.GroupsFragment;
import com.example.groupalarmsync.menufragments.NotificationFragment;
import com.example.groupalarmsync.menufragments.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private long backPressedTime;
    private Toast backToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // setting the bottom navigation view of the main activity which is designed in the "bottom_nav_menu.xml" file
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        // setting default fragment as the "FirstFragment"
        getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, new AlarmFragment()).commit();
    }

    // setting listener to the bottom navigation menu buttons as we shift from one fragment to other
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;
                    switch (item.getItemId()){
                        case R.id.alarmTab:
                            selectedFragment = new AlarmFragment();
                            break;
                        case R.id.searchTab:
                            selectedFragment = new SearchFragment();
                            break;
                        case R.id.groupsTab:
                            selectedFragment = new GroupsFragment();
                            break;
                        case R.id.notifyTab:
                            selectedFragment = new NotificationFragment();
                            break;
                        case R.id.profileTab:
                            selectedFragment = new ProfileFragment();
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.flFragment, selectedFragment).commit();
                    return true;
                }
            };

    // code to exit the MainActivity upon successive pressing of back button within an interval of 2sec
    @Override
    public void onBackPressed() {

        if(backPressedTime + 2000 > System.currentTimeMillis()) {
            backToast.cancel();
            super.onBackPressed();
            return;
        }
        else{
            backToast = Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT);
            backToast.show();
        }

        backPressedTime = System.currentTimeMillis();
    }
}