package com.example.disconrecon_library.repository;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.room.Room;

import com.example.disconrecon_library.database.Database;
import com.example.disconrecon_library.model.DisconData;
import com.example.disconrecon_library.values.FunctionCall;

import java.io.File;
import java.util.List;

public class Repository {
    private FunctionCall functionCall = new FunctionCall();
    private String DATABASE_PATH = functionCall.filepath("database") + File.separator;
    private String DB_NAME = "DisconRecon.db";
    private Database database;

    public Repository(Context context) {
        database = Room.databaseBuilder(context, Database.class, DATABASE_PATH + DB_NAME).build();
    }

    //-----------------------------------------------------------------------------------------------------------------------
    @SuppressLint("StaticFieldLeak")
    public void insertDisData(final DisconData details) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                database.daoAccess().insertTask(details);
                return null;
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    public void insertReData(final DisconData details) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                database.daoAccess().insertTask(details);
                return null;
            }
        }.execute();
    }


    //------------------------------------------------------------------------------------------------------------------------------
    public LiveData<List<DisconData>> getDisData() {
        return database.daoAccess().fetchDisconData();
    }

    public LiveData<List<DisconData>> getReData() {
        return database.daoAccess().fetchReconData();
    }

    //---------------------------------------------------------------------------------------------------------------------------------

   /* @SuppressLint("StaticFieldLeak")
    public void updateTask(final Note note) {
        note.setModifiedAt(AppUtils.getCurrentDateTime());

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                database.daoAccess().updateTask(note);
                return null;
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    public void deleteTask(final int id) {
        final LiveData<Note> task = getTask(id);
        if (task != null) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    database.daoAccess().deleteTask(task.getValue());
                    return null;
                }
            }.execute();
        }
    }

    @SuppressLint("StaticFieldLeak")
    public void deleteTask(final Note note) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                database.daoAccess().deleteTask(note);
                return null;
            }
        }.execute();
    }

    public LiveData<Note> getTask(int id) {
        return database.daoAccess().getTask(id);
    }
*/
}
