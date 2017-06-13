package com.example.admin.zestawpodroznika;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class LocDbDirector extends SQLiteOpenHelper {


    private SQLiteDatabase db;

    public LocDbDirector(Context context)
    {
        super(context, "Localisations.db", null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("create table Localisation(" +
                "Id integer primary key autoincrement," +
                "Name text," +
                "Longi double," +
                "Lati double);"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

    }

    public void Add(Localisation loc)
    {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("Name", loc.getName());
        values.put("Longi", loc.getLongi());
        values.put("Lati", loc.getLati());
        db.insertOrThrow("Localisation", null, values);
    }

    public ArrayList<Localisation> GetAll()
    {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        Cursor cursor = db.query("Localisation", null, null, null, null, null, null); //select * from Localisation
        ArrayList<Localisation> result = new ArrayList<>();
        while (cursor.moveToNext()) {
            Localisation loc = new Localisation();
            loc.setName(cursor.getString(1));
            loc.setLongi(cursor.getDouble(2));
            loc.setLati(cursor.getDouble(3));

            result.add(loc);
        }
        return result;
    }

    public int Count()
    {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("Select Count(*) from Localisation", null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count;

    }

    public Localisation GetLoc(int position)
    {
        Cursor cursor = GetRecordByPosition(position);

        Localisation loc = new Localisation();
        loc.setName(cursor.getString(1));
        loc.setLongi(cursor.getDouble(2));
        loc.setLati(cursor.getDouble(3));
        cursor.close();
        return loc;
    }
    private Cursor GetRecordByPosition(int position)
    {
        db = getWritableDatabase();
        Cursor cursor = db.rawQuery("Select * from Localisation", null);
        if(cursor.moveToPosition(position)==false) {
            cursor.close();
            return null;
        }
        return cursor;
    }


    public void ChangeName(int id, String newName)
    {
        db = getWritableDatabase();
        ContentValues newValues = new ContentValues();
        newValues.put("Name", newName);
        db.update("Localisation", newValues, "Id=" + String.valueOf(id), null);
    }

    public void DeleteRow(int positon)
    {
        Cursor cursor=GetRecordByPosition(positon);
        db.delete("Localisation", "Id = ?", new String[]{cursor.getString(0)});
    }


}
