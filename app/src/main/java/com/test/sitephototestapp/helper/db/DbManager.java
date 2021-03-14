package com.test.sitephototestapp.helper.db;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.location.Location;
import android.util.Log;

import com.test.sitephototestapp.MainActivity;
import com.test.sitephototestapp.helper.Utils;
import com.test.sitephototestapp.helper.model.BinEmpData;
import com.test.sitephototestapp.helper.model.BinLocationData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class DbManager {
    Context context;
    DBadapterHelper dBadapterHelper;
    SQLiteDatabase db;
    public String TAG;
    public DbManager(Context context) {
        this.context = context;
        dBadapterHelper = new DBadapterHelper(context);
        db = dBadapterHelper.getWritableDatabase();
        TAG = this.getClass().getSimpleName();
    }

    int getRandomNo()
    {
        return new Random().nextInt(2147483647);
    }

    public void insertRandomEmpData()
    {
        Log.e(TAG, "Start inserting data...");
        Log.e(TAG, new SimpleDateFormat("ddMMyyyy HH:mm:ss.SSS").format(new Date()));
        String sql = "INSERT OR REPLACE INTO " + dBadapterHelper.tableNameEmp + " ( "+dBadapterHelper.fieldObjectNameEmp+", "+dBadapterHelper.fieldObjectEmailEmp+" ) VALUES ( ?, ? )";
        db.beginTransaction();
        SQLiteStatement stmt = db.compileStatement(sql);
        for(int x=0; x<20000; x++){
            int XXX = getRandomNo();
            stmt.bindString(1, "Name # " +XXX);
            stmt.bindString(2, XXX+"@"+XXX+".com");
            stmt.execute();
            stmt.clearBindings();
        }
        try {
            db.setTransactionSuccessful();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally {
            db.endTransaction();
            Log.e(TAG, "Finish inserting data...");
            Log.e(TAG, new SimpleDateFormat("ddMMyyyy HH:mm:ss.SSS").format(new Date()));
            context.sendBroadcast(new Intent(Utils.ACTION_INSERT_EMP_DATA));
        }
    }

    public void insertLocationData(Location currentLocation)
    {
        Log.e(TAG, "Start inserting location data...");
        Log.e(TAG, new SimpleDateFormat("ddMMyyyy HH:mm:ss.SSS").format(new Date()));
        String sql = "INSERT OR REPLACE INTO " + dBadapterHelper.tableNameLoc + " ( "+dBadapterHelper.fieldObjectLatLoc+", "+dBadapterHelper.fieldObjectLongLoc+" ) VALUES ( ?, ? )";
        db.beginTransaction();
        SQLiteStatement stmt = db.compileStatement(sql);
        stmt.bindString(1, ""+currentLocation.getLatitude());
        stmt.bindString(2, ""+currentLocation.getLongitude());
        stmt.execute();
        stmt.clearBindings();
        try {
            db.setTransactionSuccessful();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally {
            db.endTransaction();
            Log.e(TAG, "Finish inserting location data...");
            Log.e(TAG, new SimpleDateFormat("ddMMyyyy HH:mm:ss.SSS").format(new Date()));
            context.sendBroadcast(new Intent(Utils.ACTION_INSERT_LOC_DATA));
        }
    }

    public int fetchRandomEmpCount() {
        String[] columns = {dBadapterHelper.fieldObjectIdEmp};
        Cursor cursor = db.query(dBadapterHelper.tableNameEmp, columns, null,null,null,null,null);
        int counts = cursor.getCount();
        cursor.close();
        return counts;
    }

    public int fetchRandomLocCount() {
        String[] columns = {dBadapterHelper.fieldObjectIdLoc};
        Cursor cursor = db.query(dBadapterHelper.tableNameLoc, columns, null,null,null,null,null);
        int counts = cursor.getCount();
        cursor.close();
        return counts;
    }

    public int fetchLastLocId() {
        int id;
        String sql = "SELECT "+dBadapterHelper.fieldObjectIdLoc+" FROM "+dBadapterHelper.tableNameLoc+" ORDER BY "+dBadapterHelper.fieldObjectIdLoc+" DESC LIMIT 1";
        Cursor cursor = db.rawQuery(sql,null);
        if(cursor!=null && cursor.getCount()>0)
        {
            cursor.moveToFirst();
            do
            {
                id = cursor.getInt(cursor.getColumnIndex(dBadapterHelper.fieldObjectIdLoc));
            }
            while (cursor.moveToNext());
            cursor.close();
            return id;
        }
        else
            return 0;
    }

    public int fetchLastEmpId() {
        int id;
        String sql = "SELECT "+dBadapterHelper.fieldObjectIdEmp+" FROM "+dBadapterHelper.tableNameEmp+" ORDER BY "+dBadapterHelper.fieldObjectIdEmp+" DESC LIMIT 1";
        Cursor cursor = db.rawQuery(sql,null);
        if(cursor!=null && cursor.getCount()>0)
        {
            cursor.moveToFirst();
            do
            {
                id = cursor.getInt(cursor.getColumnIndex(dBadapterHelper.fieldObjectIdEmp));
            }
            while (cursor.moveToNext());
            cursor.close();
            return id;
        }
        else
            return 0;
    }

    public ArrayList<BinEmpData> fetchEmpData() {
        ArrayList<BinEmpData> listData = new ArrayList<>();
        String sql = "SELECT * FROM "+dBadapterHelper.tableNameEmp+" ORDER BY "+dBadapterHelper.fieldObjectIdEmp+" DESC"; /*LIMIT 100*/
        Cursor cursor = db.rawQuery(sql,null);
        if(cursor!=null && cursor.getCount()>0)
        {
            cursor.moveToFirst();
            do
            {
                BinEmpData binEmpData = new BinEmpData();
                binEmpData.setId(cursor.getInt(cursor.getColumnIndex(dBadapterHelper.fieldObjectIdEmp)));
                binEmpData.setName(cursor.getString(cursor.getColumnIndex(dBadapterHelper.fieldObjectNameEmp)));
                binEmpData.setEmail(cursor.getString(cursor.getColumnIndex(dBadapterHelper.fieldObjectEmailEmp)));
                listData.add(binEmpData);
            }
            while (cursor.moveToNext());
            cursor.close();
            return listData;
        }
        else
            return listData;
    }

    public ArrayList<BinLocationData> fetchLocData() {
        ArrayList<BinLocationData> listData = new ArrayList<>();
        String sql = "SELECT * FROM "+dBadapterHelper.tableNameLoc+" ORDER BY "+dBadapterHelper.fieldObjectIdLoc+" DESC"; /*LIMIT 100*/
        Cursor cursor = db.rawQuery(sql,null);
        if(cursor!=null && cursor.getCount()>0)
        {
            cursor.moveToFirst();
            do
            {
                BinLocationData binLocationData = new BinLocationData();
                binLocationData.setId(cursor.getInt(cursor.getColumnIndex(dBadapterHelper.fieldObjectIdLoc)));
                binLocationData.setLatitude(cursor.getString(cursor.getColumnIndex(dBadapterHelper.fieldObjectLatLoc)));
                binLocationData.setLongitude(cursor.getString(cursor.getColumnIndex(dBadapterHelper.fieldObjectLongLoc)));
                listData.add(binLocationData);
            }
            while (cursor.moveToNext());
            cursor.close();
            return listData;
        }
        else
            return listData;
    }

    public boolean deleteEmpData(int empId)
    {
        String columnsSelection = dBadapterHelper.fieldObjectIdEmp+"=?";
        String SelectionArgs[] = {String.valueOf(empId)};
        return db.delete(dBadapterHelper.tableNameEmp,columnsSelection,SelectionArgs) > 0;
    }

    public boolean deleteLocData(int locId)
    {
        String columnsSelection = dBadapterHelper.fieldObjectIdLoc+"=?";
        String SelectionArgs[] = {String.valueOf(locId)};
        return db.delete(dBadapterHelper.tableNameLoc,columnsSelection,SelectionArgs) > 0;
    }

}
