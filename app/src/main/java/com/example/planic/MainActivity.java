package com.example.planic;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    FloatingActionButton fabAdd;
    HomeFragment homeFragment;
    CalendarFragment calendarFragment;
    ChatFragment chatFragment;
    ProfileFragment profileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        homeFragment = new HomeFragment();
        calendarFragment = new CalendarFragment();
        chatFragment = new ChatFragment();
        profileFragment = new ProfileFragment();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
                getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, homeFragment).commit();
            }
            if (item.getItemId() == R.id.nav_calendar) {
                getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, calendarFragment).commit();
            }
            if (item.getItemId() == R.id.nav_chat) {
                getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, chatFragment).commit();
            }
            if (item.getItemId() == R.id.nav_profile) {
                getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, profileFragment).commit();
            }
            return true;
        });
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        fabAdd = findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
            startActivity(intent);
        });
    }
}