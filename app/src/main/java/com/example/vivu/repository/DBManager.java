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
    private static final String addr = "address";
    private static final int verson = 1;
    private String query = "CREATE TABLE " + tableName + " ( "
            + id + " integer primary key autoincrement, "
            + lat + " REAL, "
            + lng + " REAL, "
            + info + " TEXT, "
            +addr + " TEXT)";

    private static final String TAG= "DBManager";

    public DBManager(Context context){
        super(context,dataBaseName,null,verson);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(query);

        //        --------DEFAULT MARKER--------
        ArrayList<com.example.vivu.model.Marker> dftMarkers = new ArrayList<com.example.vivu.model.Marker>();
        dftMarkers.add( new com.example.vivu.model.Marker(10.756491, 106.647285,"Tiệm sửa xe Mã Kim So\n151 Nguyễn Chí Thanh, Phường 15, Quận 5","23"));
        dftMarkers.add( new com.example.vivu.model.Marker(10.752905, 106.668726,"Tiệm sửa xe Thạch Nguyễn\n142 Trần Hưng Đạo, Phường 7, Quận 5","ad"));
        dftMarkers.add( new com.example.vivu.model.Marker(10.758617, 106.678479,"Tiệm sửa xe Quân\n402/36 An Dương Vương, Phường 4, Quận 5","cwd"));
        dftMarkers.add( new com.example.vivu.model.Marker(10.758552, 106.681848,"Tiệm sửa xe 61\n61 Nguyên Trãi, Phường 3, Quận 5","scsvc"));
        dftMarkers.add( new com.example.vivu.model.Marker(10.755457, 106.672480,"Tiệm sửa xe A Dìa\n385 Trần Phú, Phường 8, Quận 5","scfv"));
        dftMarkers.add( new com.example.vivu.model.Marker(10.758748, 106.663163,"Tiệm sửa xe Anh Em\n177 Nguyễn Chí Thanh, Phường 6, Quận 10","vdfvv"));
        dftMarkers.add( new com.example.vivu.model.Marker(10.758263, 106.675186,"Tiệm sửa xe Đắc Thời\n247A Trần Phú, Phường 9, Quận 5",null));
//        dftMarkers.add( new com.example.vivu.model.Marker(10.761214, 106.683134," Tiệm sửa xe Ngã 3 ADV-NVC","vsvdsfv"));
        dftMarkers.add( new com.example.vivu.model.Marker(10.765748, 106.682129,
                "Tiệm sửa xe Phi\n102 Phạm Viết Chánh, Phường Nguyễn Cư Trinh, Quận 1",null));
        dftMarkers.add( new com.example.vivu.model.Marker(10.766438, 106.679522,
                "Tiệm sửa xe A Dũng\nHẻm 109 Nguyễn Thiện Thuật, Phường 2, Quận 3",null));
        dftMarkers.add( new com.example.vivu.model.Marker(10.781193, 106.688722,
                "Tiệm sửa xe Thuận\n42 Trần Quốc Thảo, Phường 7, Quận 3",null));
        dftMarkers.add( new com.example.vivu.model.Marker(10.791654, 106.690316,
                "Tiệm sửa xe Hậu\n159-147 Trần Quang Khải, Tân Định, Quận 1",null));
        dftMarkers.add( new com.example.vivu.model.Marker(10.767192, 106.698261,
                "Tiệm sửa xe Kim Châu\n119 Ký Con, Phường Nguyễn Thái Bình, Quận 1",null));
        dftMarkers.add( new com.example.vivu.model.Marker(10.765692, 106.696719,
                "Tiệm sửa xe Hoàng Sơn\n234-236 Nguyễn Thái Học, Phường Cầu Ông Lãnh, Quận 1",null));
        dftMarkers.add( new com.example.vivu.model.Marker(10.775444, 106.662725,
                "Tiệm sửa xe Tài\n473/8/18, Tô Hiến Thành, Phường 14, Quận 10",null));
        dftMarkers.add( new com.example.vivu.model.Marker(10.782252, 106.663916,
                "Tiệm sửa xe Kiếm Tay Ga\nTrường Sơn, Phường 15, Quận 10",null));
        dftMarkers.add( new com.example.vivu.model.Marker(10.768679, 106.674463,
                "Tiệm sửa xe Minh Tuấn\n533 Lê Hồng Phong, Phường 10, Quận 10",null));
        dftMarkers.add( new com.example.vivu.model.Marker(10.746471, 106.689973,
                "Tiệm sửa xe Hải Exciter\n311 Dương Bá Trạc, Phường 1, Quận 8",null));


        for (Marker marker:dftMarkers) {
            ContentValues values = new ContentValues();
            values.put(lat, marker.getmLat());
            values.put(lng, marker.getmLng());
            values.put(info, marker.getmInfo());
            values.put(addr, marker.getmAddr());
            db.insert(tableName, null, values);
        }
//        addMarker(new Marker(10.762984, 106.686797,"quan1",1));
//        addMarker(new Marker(10.763154, 106.677991,"quan5",0));
//        addMarker(new Marker(10.767222, 106.684348,"quan1",0));
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
        values.put(addr,marker.getmAddr());
        db.insert(tableName,null,values);
        db.close();
        Log.e(TAG, "addMarker: successfully");
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
                marker.setmAddr(cursor.getString(4));
                listMarker.add(marker);
            } while (cursor.moveToNext());
        }
        db.close();
        return listMarker;
    }
}
