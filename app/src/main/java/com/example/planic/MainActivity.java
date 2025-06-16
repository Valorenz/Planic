package com.example.planic;

import android.content.Intent;
import android.os.Bundle;
import android.widget.PopupMenu;

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
            PopupMenu popup = new PopupMenu(MainActivity.this, v);
            popup.getMenuInflater().inflate(R.menu.fab_menu, popup.getMenu());
            popup.setOnMenuItemClickListener(item -> {
                Intent intent;
                if (item.getItemId() == R.id.action_add_task) {
                    intent = new Intent(MainActivity.this, AddTaskActivity.class);
                } else if (item.getItemId() == R.id.action_add_event) {
                    intent = new Intent(MainActivity.this, AddTaskActivity.class);
                } else { // item.getItemId() == R.id.action_add_note
                    intent = new Intent(MainActivity.this, AddTaskActivity.class);
                }
                startActivity(intent);
                return true;
            });
            popup.show();
        });
    }
}