package com.jostlingjacks.craftlife;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper {

    //Set up the SQLite database
    //Database Name
    public static final String DATABASE_NAME = "craftlife";
    //Database Version
    private static final int DATABASE_VERSION = 7;

    //Table Names
    public static final String CHECK_TABLE = "CHECKLIST";
    public static final String SUGGESTION_TABLE = "Suggestion";

    public static final String T1_COL_1 = "item_id";
    public static final String T1_COL_2 = "email";
    public static final String T1_COL_3 = "item";
    public static final String T1_COL_4 = "Status";
    public static final String T1_COL_5 = "date";


    public static final String T2_COL_1 = "suggestion_id";
    public static final String T2_COL_2 = "email";
    public static final String T2_COL_3 = "type";
    public static final String T2_COL_4 = "title";
    public static final String T2_COL_5 = "details";
    public static final String T2_COL_6 = "address";
    public static final String T2_COL_7 = "time";
    public static final String T2_COL_8 = "notifying_time";

    //The SQL of create CHECK_TABLE
    public static final String CREATE_CHECK_TABLE = "CREATE TABLE "
            + CHECK_TABLE + "(" + T1_COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + T1_COL_2 + " TEXT, " + T1_COL_3 + " TEXT, " + T1_COL_4 + " BOOLEAN, "
            + T1_COL_5 + " TEXT)";

    //The SQL of create SUGGESTION_TABLE
    public static final String CREATE_SUGGESTION_TABLE = "CREATE TABLE "
            + SUGGESTION_TABLE + "(" + T2_COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + T2_COL_2 + " TEXT, "+ T2_COL_3 + " TEXT, " + T2_COL_4 + " TEXT, " + T2_COL_5 + " TEXT, "
            + T2_COL_6 + " TEXT, " + T2_COL_7  + " TEXT, " + T2_COL_8 + " TEXT) ";


    //sql of dropping the table
    public static final String DROP_SUGGESTION = "DROP TABLE IF EXISTS " + SUGGESTION_TABLE;
    public static final String DROP_CHECK = "DROP TABLE IF EXISTS " + CHECK_TABLE;

    public DataBaseHelper(Context context){
        super(context, DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_SUGGESTION_TABLE);
        db.execSQL(CREATE_CHECK_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //on upgrade deop older tables
        db.execSQL(DROP_SUGGESTION);
        db.execSQL(DROP_CHECK);

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


    public Cursor getCheck(String email, String date) throws SQLException{
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor mCursor = db.query(CHECK_TABLE,new String[] {T1_COL_1,T1_COL_2,T1_COL_3,T1_COL_4,T1_COL_5}, T1_COL_2 + "=? and "  + T1_COL_5 +"=?", new String[] {email,date},
                null,null,null,null);
        if (mCursor != null){
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public Boolean getCheckRepeat(String email, String date,String item) throws SQLException{
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor mCursor = db.query(CHECK_TABLE,new String[] {T1_COL_1,T1_COL_2,T1_COL_3,T1_COL_4,T1_COL_5}, T1_COL_2 + "=? and "  + T1_COL_5 +"=? and " + T1_COL_3+"=?", new String[] {email,date,item},
                null,null,null,null);
        if (mCursor.getCount() <= 0){
            mCursor.close();
            return false;
        }

        mCursor.close();
        return true;
    }

    public Cursor getAllCheck(){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(CHECK_TABLE,new String[] {T1_COL_1,T1_COL_2,T1_COL_3,T1_COL_4,T1_COL_5},null,null,null,null,null);
    }

    public boolean addCheck(String email, String item, Boolean status, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(T1_COL_2, email);
        values.put(T1_COL_3, item);
        values.put(T1_COL_4, status);
        values.put(T1_COL_5, date);

        db.insert(CHECK_TABLE, null, values);
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

    public boolean deleteCheck(int rowId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(CHECK_TABLE, T1_COL_1 + "=" + rowId,null);
        db.close();
        return true;
    }

    public boolean updateCheck(int rowId, String status){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(T1_COL_4, status);
        db.update(CHECK_TABLE,values,T1_COL_1+"="+rowId,null);
        db.close();
        return true;
    }
}