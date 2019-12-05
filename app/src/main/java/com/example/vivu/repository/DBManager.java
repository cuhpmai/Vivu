package com.example.vivu.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.vivu.model.Marker;
import java.util.ArrayList;
import java.util.List;

public class DBManager extends SQLiteOpenHelper {

    private static final String dataBaseName = "markers_manager";
    private static final String tableName = "markers";
    private static final String id = "id";
    private static final String lat = "latitude";
    private static final String lng = "longitude";
    private static final String info = "info";
    private static final String liked = "liked";
    private static final int verson = 1;
    private String query = "CREATE TABLE " + tableName + " ( "
            + id + " integer primary key autoincrement, "
            + lat + " REAL, "
            + lng + " REAL, "
            + info + " TEXT, "
            +liked + " integer)";

    private static final String TAG= "DBManager";

    public DBManager(Context context){
        super(context,dataBaseName,null,verson);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + tableName);
    }
    public void addMarker(Marker marker){
        SQLiteDatabase db= this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(lat,marker.getmLat());
        values.put(lng,marker.getmLng());
        values.put(info,marker.getmInfo());
        values.put(liked,marker.getmLiked());
        db.insert(tableName,null,values);
        db.close();
        Log.d(TAG, "addMarker: successfully");
    }
    public List<Marker> getAllMarker(){
        List<Marker> listMarker = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + tableName;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        if (cursor.moveToFirst()){
            do{
                Marker marker = new Marker();
                marker.setmID(cursor.getInt(0));
                marker.setmLat(cursor.getDouble(1));
                marker.setmLng(cursor.getDouble(2));
                marker.setmInfo(cursor.getString(3));
                marker.setmLiked(cursor.getInt(4));
                listMarker.add(marker);
            } while (cursor.moveToNext());
        }
        db.close();
        return listMarker;
    }
}
