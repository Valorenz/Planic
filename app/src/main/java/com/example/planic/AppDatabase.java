package com.example.planic;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {TitleTask.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract TaskDao taskDao();
}
