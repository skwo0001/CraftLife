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
    //Database Version i
    private static final int DATABASE_VERSION = 10;

    //Table Names
    public static final String SUGGESTION_TABLE = "Suggestion";
    public static final String SETTING_TABLE = "Setting";

    public static final String T1_COL_1 = "setting_id";
    public static final String T1_COL_2 = "email";
    public static final String T1_COL_3 = "type";
    public static final String T1_COL_4 = "interval";

    public static final String T2_COL_1 = "suggestion_id";
    public static final String T2_COL_2 = "email";
    public static final String T2_COL_3 = "type";
    public static final String T2_COL_4 = "title";
    public static final String T2_COL_5 = "details";
    public static final String T2_COL_6 = "address";
    public static final String T2_COL_7 = "time";
    public static final String T2_COL_8 = "notifying_time";
    public static final String T2_COL_9 = "options";

    //The SQL of create SUGGESTION_TABLE
    public static final String CREATE_SUGGESTION_TABLE = "CREATE TABLE "
            + SUGGESTION_TABLE + "(" + T2_COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + T2_COL_2 + " TEXT, "+ T2_COL_3 + " TEXT, " + T2_COL_4 + " TEXT, " + T2_COL_5 + " TEXT, "
            + T2_COL_6 + " TEXT, " + T2_COL_7  + " TEXT, " + T2_COL_8 + " TEXT, " + T2_COL_9 + " TEXT) ";

    public static final String CREATE_SETTING_TABLE = "CREATE TABLE " + SETTING_TABLE + "(" + T1_COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + T1_COL_2 + " TEXT, "+ T1_COL_3 + " TEXT, " + T1_COL_4 + " TEXT) ";

    //sql of dropping the table
    public static final String DROP_SUGGESTION = "DROP TABLE IF EXISTS " + SUGGESTION_TABLE;
    public static final String DROP_SETTING = "DROP TABLE IF EXISTS " + SETTING_TABLE;

    public DataBaseHelper(Context context){
        super(context, DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_SUGGESTION_TABLE);
        db.execSQL(CREATE_SETTING_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //on upgrade deop older tables
        db.execSQL(DROP_SUGGESTION);
        db.execSQL(DROP_SETTING);

        onCreate(db);
    }

    public boolean addSuggestion(String type, String title, String details, String address, String time, String email, String notiTime, Boolean options) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(T2_COL_2, email);
        values.put(T2_COL_3, type);
        values.put(T2_COL_4,title);
        values.put(T2_COL_5, details);
        values.put(T2_COL_6, address);
        values.put(T2_COL_7, time);
        values.put(T2_COL_8, notiTime);
        values.put(T2_COL_9, options);


        db.insert(SUGGESTION_TABLE, null, values);
        db.close();
        return true;
    }

    public boolean addSetting(String email, String type, String interval) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(T1_COL_2, email);
        values.put(T1_COL_3, type);
        values.put(T1_COL_4, interval);

        db.insert(SETTING_TABLE, null, values);
        db.close();
        return true;
    }

    public Cursor getSetting(String email, String type) {
        SQLiteDatabase db = this.getReadableDatabase();

        //select title,details,address,time from suggestion s where s.email = email and s.type = type;
        Cursor mCursor = db.query(SETTING_TABLE,new String[] {T1_COL_4}, T1_COL_2 + "=? and "  + T1_COL_3+"=?", new String[] {email,type},
                null,null,null,null);
        if (mCursor != null){
            mCursor.moveToLast();
        }
        return mCursor;
    }

    public Boolean getSettingRepeat(String email,String type) throws SQLException{
        SQLiteDatabase db = this.getReadableDatabase();

        //select title,details,address,time from suggestion s where s.email = email and s.type = type;
        Cursor mCursor = db.query(SETTING_TABLE,new String[] {T1_COL_4}, T1_COL_2 + "=? and "  + T1_COL_3+"=?", new String[] {email,type},
                null,null,null,null);
        if (mCursor.getCount() <= 0){
            mCursor.close();
            return false;
        }

        mCursor.close();
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

    public Cursor getOptions (String email, String title){
        SQLiteDatabase db = this.getReadableDatabase();

        //select id and option from suggestion s where s.email = email and s.title = title;
        Cursor mCursor = db.query(SUGGESTION_TABLE,new String[] {T2_COL_1,T2_COL_9}, T2_COL_2 + "=? and "  + T2_COL_4+"=?", new String[] {email,title},
                null,null,null,null);
        if (mCursor != null){
            mCursor.moveToLast();
        }

        return mCursor;
    }

    //get the suggestion_id
    public Cursor getSuggestionID (String email, String title, String address){
        SQLiteDatabase db = this.getReadableDatabase();

        //select id from suggestion s where s.email = email and s.title = title and s.details = details and s.address = address;
        Cursor mCursor = db.query(SUGGESTION_TABLE,new String[] {T2_COL_1}, T2_COL_2 + "=? and "  + T2_COL_4+"=? and " + T2_COL_6+"=? ", new String[] {email,title,address},
                null,null,null,null);
        if (mCursor != null){
            mCursor.moveToLast();
        }
        return mCursor;
    }

    public Cursor getRegularSuggestions(String email, String type){
        SQLiteDatabase db = this.getReadableDatabase();

        //select title,details,address,time from suggestion s where s.email = email and s.type = type;
        Cursor mCursor = db.query(SUGGESTION_TABLE,new String[] {T2_COL_3,T2_COL_4,T2_COL_5,T2_COL_6,T2_COL_7}, T2_COL_2 + "=? and "  + T2_COL_3+"=?", new String[] {email,type},
                null,null,T2_COL_8 + " DESC");

        if (mCursor != null){
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public Cursor getSuggestions(String email, String type){
        SQLiteDatabase db = this.getReadableDatabase();

        //select title,details,address,time from suggestion s where s.email = email and s.type = type;
        Cursor mCursor = db.query(SUGGESTION_TABLE,new String[] {T2_COL_3,T2_COL_4,T2_COL_5,T2_COL_6,T2_COL_7,T2_COL_9}, T2_COL_2 + "=? and "  + T2_COL_3+"=?", new String[] {email,type},
                null,null,T2_COL_8 + " DESC");

        if (mCursor != null){
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public boolean updateSetting(String email, String type, String interval){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(T1_COL_4, interval);

        db.update(SETTING_TABLE,values,T1_COL_2+" =? and " + T1_COL_3 +" =? ",new String[] {email,type});
        db.close();
        return true;
    }

    public boolean updateOption(String id, String option){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(T2_COL_9, option);

        db.update(SUGGESTION_TABLE,values,T2_COL_1+" =? " ,new String[] {id});
        db.close();
        return true;
    }


}