package com.example.planic;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Delete;
import java.util.List;

@Dao
public interface TaskDao {
    @Query("SELECT * FROM TitleTask")
    List<TitleTask> getAll();

    @Insert
    void insert(TitleTask task);

    @Delete
    void delete(TitleTask task);
}
