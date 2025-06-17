package com.example.planic;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.PopupMenu;

import androidx.appcompat.app.AppCompatActivity;

import com.example.planic.utils.AndroidUtil;
import com.example.planic.utils.FirebaseUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    FloatingActionButton fabAdd;
    HomeFragment homeFragment;
    EventFragment eventFragment;
    ChatFragment chatFragment;
    NoteFragment noteFragment;
    ImageView toolbarPic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbarPic = findViewById(R.id.profile_picture);

        FirebaseUtil.getCurrentProfilePicStorageRef().getDownloadUrl()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Uri uri = task.getResult();
                        AndroidUtil.setProfilePic(this, uri, toolbarPic);
                    }
                });

        homeFragment = new HomeFragment();
        eventFragment = new EventFragment();
        chatFragment = new ChatFragment();
        noteFragment = new NoteFragment();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
                getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, homeFragment).commit();
            }
            if (item.getItemId() == R.id.nav_event) {
                getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, eventFragment).commit();
            }
            if (item.getItemId() == R.id.nav_chat) {
                getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, chatFragment).commit();
            }
            if (item.getItemId() == R.id.nav_note) {
                getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, noteFragment).commit();
            }
            return true;
        });
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        toolbarPic.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
        });

        fabAdd = findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(MainActivity.this, v);
            popup.getMenuInflater().inflate(R.menu.fab_menu, popup.getMenu());
            popup.setOnMenuItemClickListener(item -> {
                Intent intent;
                if (item.getItemId() == R.id.action_add_task) {
                    intent = new Intent(MainActivity.this, AddTaskActivity.class);
                } else if (item.getItemId() == R.id.action_add_event) {
                    intent = new Intent(MainActivity.this, AddEventActivity.class);
                } else if (item.getItemId() == R.id.action_add_note) {
                    intent = new Intent(MainActivity.this, AddNoteActivity.class);
                } else {
                    return false;
                }
                startActivity(intent);
                return true;
            });
            popup.show();
        });
    }
}