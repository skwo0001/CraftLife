package com.jostlingjacks.craftlife;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteTransactionListener;

public class DataBaseHelper extends SQLiteOpenHelper {

    //Set up the SQLite database
    //Database Name
    public static final String DATABASE_NAME = "craftlife";
    //Database Version
    private static final int DATABASE_VERSION = 7;

    //Table Names
    public static final String SUGGESTION_TABLE = "Suggestion";

    public static final String T2_COL_1 = "suggestion_id";
    public static final String T2_COL_2 = "email";
    public static final String T2_COL_3 = "type";
    public static final String T2_COL_4 = "title";
    public static final String T2_COL_5 = "details";
    public static final String T2_COL_6 = "address";
    public static final String T2_COL_7 = "time";
    public static final String T2_COL_8 = "notifying_time";


    //The SQL of create SUGGESTION_TABLE
    public static final String CREATE_SUGGESTION_TABLE = "CREATE TABLE "
            + SUGGESTION_TABLE + "(" + T2_COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + T2_COL_2 + " TEXT, "+ T2_COL_3 + " TEXT, " + T2_COL_4 + " TEXT, " + T2_COL_5 + " TEXT, "
            + T2_COL_6 + " TEXT, " + T2_COL_7  + " TEXT, " + T2_COL_8 + " TEXT) ";


    //sql of dropping the table
    public static final String DROP_SUGGESTION = "DROP TABLE IF EXISTS " + SUGGESTION_TABLE;

    public DataBaseHelper(Context context){
        super(context, DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_SUGGESTION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //on upgrade deop older tables
        db.execSQL(DROP_SUGGESTION);

        onCreate(db);
    }

    public boolean addSuggestion(String type, String title, String details, String address, String time, String email, String notiTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(T2_COL_2, email);
        values.put(T2_COL_3, type);
        values.put(T2_COL_4,title);
        values.put(T2_COL_5, details);
        values.put(T2_COL_6, address);
        values.put(T2_COL_7, time);
        values.put(T2_COL_8, notiTime);

        db.insert(SUGGESTION_TABLE, null, values);
        db.close();
        return true;
    }




    public Cursor getRecentRegularSuggestion(String email,String type){
        SQLiteDatabase db = this.getReadableDatabase();

        //select title,details,address,time from suggestion s where s.email = email and s.type = type;
        Cursor mCursor = db.query(SUGGESTION_TABLE,new String[] {T2_COL_4,T2_COL_5,T2_COL_6,T2_COL_7}, T2_COL_2 + "=? and "  + T2_COL_3+"=?", new String[] {email,type},
                null,null,null,null);
        if (mCursor != null){
            mCursor.moveToLast();
        }

        return mCursor;

    }

    public Cursor getSuggestions(String email,String type){
        SQLiteDatabase db = this.getReadableDatabase();

        //select title,details,address,time from suggestion s where s.email = email and s.type = type;
        Cursor mCursor = db.query(SUGGESTION_TABLE,new String[] {T2_COL_3,T2_COL_4,T2_COL_5,T2_COL_6,T2_COL_7}, T2_COL_2 + "=? and "  + T2_COL_3+"=?", new String[] {email,type},
                null,null,T2_COL_8 + " DESC",null);

        if (mCursor != null){
            mCursor.moveToFirst();
        }
        return mCursor;
    }

}