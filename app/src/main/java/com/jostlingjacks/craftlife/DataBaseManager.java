package com.jostlingjacks.craftlife;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.concurrent.atomic.AtomicInteger;

public class DataBaseManager {

    private static DataBaseManager instance;
    private static SQLiteOpenHelper mDatabaseHelper;
    private AtomicInteger mOpenCounter = new AtomicInteger();
    private SQLiteDatabase mDb;

    public static synchronized void initializeInstance(SQLiteOpenHelper helper){
        if (instance == null) {
            instance = new DataBaseManager();
            mDatabaseHelper = helper;
        }
    }

    public static  synchronized DataBaseManager getInstance(){
        if(instance == null) {
            throw new IllegalStateException(
                    DataBaseManager.class.getSimpleName() + "is not initialized, call initializeInstance(..) method first.");
        }
        return instance;
    }

    public synchronized  void openDatabase(){
        if (mOpenCounter.incrementAndGet() == 1){
            //open database
            mDb = mDatabaseHelper.getWritableDatabase();

        }
    }


    public synchronized void closeDatabase(){
        if (mOpenCounter.decrementAndGet() == 0) {
            //close the database
            mDb.close();
        }
    }

    public Cursor getDetails(String query) {
        return mDb.rawQuery(query,null);
    }

    public boolean inserts(String tableName, ContentValues values){
        long l = -1;
        try{
            l = mDb.insert(tableName, null, values);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        return l != -1;
    }

    public boolean delete(String tableName){
        try {
            mDb.delete(tableName,null,null);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        return true;
    }
}
