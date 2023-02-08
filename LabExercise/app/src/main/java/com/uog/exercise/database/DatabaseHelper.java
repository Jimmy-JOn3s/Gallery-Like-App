package com.uog.exercise.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.uog.exercise.IImage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME ="myLabExercise.db";
    private static final String TABLE_IMAGE ="tblImage";

    public static final String _ID = "id";
    public static final String _PATH = "path";
    public static final String _TYPE = "type";
    public static final String _LATITUDE = "latitude";
    public static final String _LONGITUDE = "longitude";

    private SQLiteDatabase database;

    private static final String CREATE_IMAGE_TABLE =String.format(
            "CREATE TABLE IF NOT EXISTS %s (" +
                    " %s INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " %s TEXT," +
                    " %s TEXT," +
                    " %s REAL," +
                    " %s REAL)"
    , TABLE_IMAGE, _ID, _PATH, _TYPE, _LATITUDE, _LONGITUDE);

    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, 1);
        database =getWritableDatabase();
        if(database !=null) {
            database.execSQL( "PRAGMA encoding ='UTF-8'" );
            database.execSQL( "PRAGMA foreign_keys = ON" );
        }
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_IMAGE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public long save(IImage iImage) throws Exception{
        long result =0;

        ContentValues rowValues =new ContentValues();
        rowValues.put(_PATH, iImage.getPath());
        rowValues.put(_TYPE, iImage.getType());
        rowValues.put(_LATITUDE, iImage.getLatitude());
        rowValues.put(_LONGITUDE, iImage.getLongitude());
        result =database.insertOrThrow(TABLE_IMAGE, null, rowValues);
        return result;
    }

    public List<IImage> list() throws Exception{
        Cursor cursor = null;
        String query ="SELECT * FROM " + TABLE_IMAGE;

        List<IImage> results =new ArrayList<>();
        cursor = database.rawQuery( query, null );
        cursor.moveToFirst( );
        while( !cursor.isAfterLast() ){
            IImage expenses =new IImage(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getDouble(3),
                    cursor.getDouble(4)
            );
            results.add(expenses);
            cursor.moveToNext( );
        }
        cursor.close();
        return results;
    }

}
