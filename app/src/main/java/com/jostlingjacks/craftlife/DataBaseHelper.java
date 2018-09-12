package com.jostlingjacks.craftlife;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper{

    //Set up the SQLite database
    //Database Name
    public static final String DATABASE_NAME = "craftlife";
    //Database Version
    private static final int DATABASE_VERSION = 1;

    //Table Names
    public static final String USER_TABLE = "User";
    public static final String SUGGESTION_TABLE = "Suggestion";

    //The column of the table, T1 is USER_TABLE and T2 is SUGGESTION_TABLE
    public static final String T1_COL_1 = "user_id";
    public static final String T1_COL_2 = "email";

    public static final String T2_COL_1 = "suggestion_id";
    public static final String T2_COL_2 = "user_id";
    public static final String T2_COL_3 = "type";
    public static final String T2_COL_4 = "details";
    public static final String T2_COL_5 = "address";
    public static final String T2_COL_6 = "time";
    public static final String T2_COL_7 = "notifying_time";

    //The SQL of create USER_TABLE
    public static final String CREATE_USER_TABLE = "CREATE TABLE "
            + USER_TABLE + "(" + T1_COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + T1_COL_2 + "TEXT UNIQUE)";

    //The SQL of create SUGGESTION_TABLE
    public static final String CREATE_SUGGESTION_TABLE = "CREATE TABLE "
            + SUGGESTION_TABLE + "(" + T2_COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + T2_COL_3 + " TEXT, " + T2_COL_4 + " TEXT, " + T2_COL_5 + " TEXT, "
            + T2_COL_6 + " TEXT, " + T2_COL_7 + " LONG, "+ T2_COL_2 + " INT, "
            + "FOREIGN KEY(" + T2_COL_2 + ") REFERENCES "
            + USER_TABLE + "(user_id) " + ")";


    //sql of dropping the table
    public static final String DROP_USER = "DROP TABLE IF EXISTS " + USER_TABLE;
    public static final String DROP_SUGGESTION = "DROP TABLE IF EXISTS " + SUGGESTION_TABLE;

    public DataBaseHelper(Context context){
        super(context, DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_SUGGESTION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //on upgrade deop older tables
        db.execSQL(DROP_USER);
        db.execSQL(DROP_SUGGESTION);

        onCreate(db);
    }

    public boolean createUser(String email) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(T1_COL_2, email);

        //insert row
        db.insert(USER_TABLE, null, values);
        db.close();
        return true;
    }
}
