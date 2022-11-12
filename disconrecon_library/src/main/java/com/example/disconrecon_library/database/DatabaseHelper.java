package com.example.disconrecon_library.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;


import com.example.disconrecon_library.model.DateTime;
import com.example.disconrecon_library.model.DisconData;
import com.example.disconrecon_library.values.FunctionCall;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.io.File;
import java.util.List;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    private static SQLiteDatabase myDataBase;
    private static FunctionCall fcall = new FunctionCall();
    private static final String DATABASE_NAME = "online.db";
    private static String path = fcall.filepath("database");
    private final static String DATABASE_PATH = path + File.separator;
    private static final int DATABASE_VERSION = 1;
    private Dao<DisconData, Integer> billingOutputsDao;
    private Dao<DateTime, Integer> dateTimeDao;



    //****************************************************************************************************************************************
    public DatabaseHelper(Context context) {
        super(context, DATABASE_PATH + DATABASE_NAME, null, DATABASE_VERSION);
    }

    //****************************************************************************************************************************************
    public void openDatabase() throws SQLException {
        myDataBase = this.getWritableDatabase();
    }

    //****************************************************************************************************************************************
    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, DisconData.class);
            TableUtils.createTable(connectionSource, DateTime.class);
        } catch (java.sql.SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //****************************************************************************************************************************************
    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {

    }

    //-------------------------------------------------insert billing output------------------------------------------------------------------------
    public Dao<DisconData, Integer> getBillingOutputsDao() throws java.sql.SQLException {
        if (billingOutputsDao == null) {
            billingOutputsDao = getDao(DisconData.class);
        }
        return billingOutputsDao;
    }

    //-------------------------------------------------insert date------------------------------------------------------------------------
    public Dao<DateTime, Integer> getDateTimeDao() throws java.sql.SQLException {
        if (dateTimeDao == null) {
            dateTimeDao = getDao(DateTime.class);
        }
        return dateTimeDao;
    }

    //-------------------------------------------------fetch all data------------------------------------------------------------------------
    public List getData(Class clazz) throws java.sql.SQLException {
        Dao dao = getDao(clazz);
        return dao.queryForAll();
    }

    //-------------------------------------------------delete table------------------------------------------------------------------------
    public void deleteData() {
        try {
            TableUtils.dropTable(connectionSource, DisconData.class, true);
            TableUtils.dropTable(connectionSource, DateTime.class, true);
            onCreate(myDataBase, connectionSource);
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }

}
