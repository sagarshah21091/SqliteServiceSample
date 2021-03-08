package com.test.sitephototestapp.helper.db;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Sagar.Shah on 09-02-2015.
 */
public class DBadapterHelper extends SQLiteOpenHelper
{
    public static int SQLITE_DB_VERSION=1;
    public static String SQLITE_DB_NAME = "testSitePhotos.sqlite";
    public String TAG;
    Context context;
    // table details
    String tableNameLoc = "Location";
    String fieldObjectIdLoc = "id";
    String fieldObjectLatLoc = "lat";
    String fieldObjectLongLoc = "long";
//    String fieldObjectDateLoc = "datetime";

    // table details
    String tableNameEmp = "Employee";
    String fieldObjectIdEmp = "id";
    String fieldObjectNameEmp = "name";
    String fieldObjectEmailEmp = "email";
//    String fieldObjectDateEmp = "datetime";

    public DBadapterHelper(Context context)
    {
        super(context,SQLITE_DB_NAME,null,SQLITE_DB_VERSION);
        TAG = getClass().getSimpleName();
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {
        sqLiteDatabase.execSQL(createEmployeeTable());
        sqLiteDatabase.execSQL(createLocationTable());
    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        return super.getWritableDatabase();
    }

    @Override
    public SQLiteDatabase getReadableDatabase() {
        return super.getReadableDatabase();
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion)
    {}

    @Override
    public void onDowngrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion)
    {
        super.onDowngrade(sqLiteDatabase, oldVersion, newVersion);
    }

    @Override
    public synchronized void close()
    {
        super.close();
    }

    @Override
    public String getDatabaseName()
    {
        return super.getDatabaseName();
    }

    private String createEmployeeTable()
    {
        String sql = "";

        sql += "CREATE TABLE " + tableNameEmp;
        sql += " ( ";
        sql += fieldObjectIdEmp + " INTEGER PRIMARY KEY AUTOINCREMENT, ";
        sql += fieldObjectNameEmp + " TEXT, ";
        sql += fieldObjectEmailEmp + " TEXT ";
//        sql += fieldObjectDateEmp + " TEXT ";
        sql += " ) ";
        return sql;
    }

    private String createLocationTable()
    {
        String sql = "";

        sql += "CREATE TABLE " + tableNameLoc;
        sql += " ( ";
        sql += fieldObjectIdLoc + " INTEGER PRIMARY KEY AUTOINCREMENT, ";
        sql += fieldObjectLatLoc + " TEXT, ";
        sql += fieldObjectLongLoc + " TEXT ";
//        sql += fieldObjectDateLoc + " TEXT ";
        sql += " ) ";
        return sql;
    }
}
