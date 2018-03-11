package com.example.yc.lab10;

/**
 * Created by yc on 2017/12/4.
 */

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class myDataBase extends SQLiteOpenHelper {
    private static final String DB_NAME = "MyDB.db";
    private static final String TABLE_NAME = "Info";
    private static final int DB_VERSION = 1;

    public myDataBase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public myDataBase(Context context) {
        this(context, DB_NAME, null, DB_VERSION);
    }

    // 创建数据库，直接执行SQl语句即可
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_TABLE = "Create Table "
                + TABLE_NAME
                + "(_id integer primary key , "
                + "name text, "
                + "birth text, "
                + "gift text);";
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
