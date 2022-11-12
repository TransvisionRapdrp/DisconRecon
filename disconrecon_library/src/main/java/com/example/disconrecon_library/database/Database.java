package com.example.disconrecon_library.database;

import androidx.room.RoomDatabase;

import com.example.disconrecon_library.dao.DaoAccess;
import com.example.disconrecon_library.model.DisconData;

@androidx.room.Database(entities = {DisconData.class}, version = 1, exportSchema = false)
public abstract class Database extends RoomDatabase {

    public abstract DaoAccess daoAccess();
}
