package com.example.mynote;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
//建立一个存储笔记的数据库
public class DATA extends SQLiteOpenHelper {
    public static final String TABLE = "notes";
    public static final String CONTENT = "content";
    public static final String ID = "_id";
    public static final String TITLE ="title";
    public static final String TIME = "time";
    public static final String TYPE="type";
    public DATA(Context context) {
        super(context,"notebook.db",null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE "+TABLE+"( "+ID+
                " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                TITLE +" VARCHAR(30) ,"+
                CONTENT + " TEXT , "+
                TIME + " DATETIME NOT NULL ,"+TYPE+" VARCHAR(10) )";
        db.execSQL(sql);
//        执行该sql语句
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}