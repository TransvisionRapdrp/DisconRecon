package com.example.disconrecon_library.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.disconrecon_library.model.DisconData;

import java.util.List;

@Dao
public interface DaoAccess {

    @Insert
    void insertTask(DisconData details);

//    @Insert
//    void insertTask(DisconData details);

    @Query("SELECT * FROM DisconData")
    LiveData<List<DisconData>> fetchDisconData();

    @Query("SELECT * FROM DisconData")
    LiveData<List<DisconData>> fetchReconData();


//    @Query("SELECT * FROM Note WHERE id =:taskId")
//    LiveData<Note> getTask(int taskId);
//
//
    @Update
    void updateDiscon(DisconData disconData);

//
//    @Delete
//    void deleteTask(Note note);
}
