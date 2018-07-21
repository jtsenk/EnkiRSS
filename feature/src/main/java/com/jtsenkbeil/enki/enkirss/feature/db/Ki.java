package com.jtsenkbeil.enki.enkirss.feature.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.jtsenkbeil.enki.enkirss.feature.MainActivity;
import com.jtsenkbeil.enki.enkirss.feature.util.Utils;

import java.io.File;
import java.sql.ResultSet;
import java.util.Arrays;

import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;

//Ki is the DB Connection Class: Use this class for all DB-related tasks
public class Ki {

    private SQLiteDatabase db;
    private File dbFile;
    private ContentValues values;
    private String sql;
    private Cursor curs;
    private final String showTbl = "tbl_shows";
    private final String dbName = "test_db.db";
    private String dbDir;
    private File f;

    public Ki() {
        try {
            dbFile = MainActivity.mainContext.getDatabasePath(dbName);
            //get the directory portion of the dbFile path
            dbDir = dbFile.getPath().substring(0, dbFile.getPath().length()-dbName.length());
            Utils.logD("Ki","dbDir= " + dbDir);
            //check and, if necessary, create the db directory
            f = new File(dbDir);
            if (f.exists()) {
                db = openOrCreateDatabase(dbFile.getPath(), null);
            } else {
                f.mkdir();
                db = openOrCreateDatabase(dbFile.getPath(), null);
            }
            Utils.logD("Ki", "DB is alive at " + db.getPath()  + ".  Calling create...");
            createTable();
        } catch (Exception exc) {
            Utils.logD("KiError","Constructor Error: " + exc.getMessage());
        }
    }

    private void createTable() {
        sql="create table if not exists tbl_shows(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL,xml_link TEXT NOT NULL, xml_loc TEXT)";
        db.execSQL(sql);
        Utils.logD("Ki","Executing the create tbl_shows (if not exists)...");
        sql = "create table if not exists tbl_dl(id INTEGER PRIMARY KEY AUTOINCREMENT, path TEXT NOT NULL, show TEXT NOT NULL, title TEXT NOT NULL, size INTEGER NOT NULL, is_done TEXT NOT NULL)";
        db.execSQL(sql);
        Utils.logD("Ki","Executing the create tbl_dl (if not exists)...");
    }

    public String getLinkURL(String showName) {
        sql = "select * from " + showTbl + " where name=\'" + showName + "\'";
        Utils.logD("Ki","sql: " + sql);
        curs = db.rawQuery(sql, null);
        Utils.logD("getLinkURL", Arrays.toString(curs.getColumnNames()) );
        curs.moveToFirst();
        return curs.getString(curs.getColumnIndex("xml_link"));
    }

    private void addValue(){
        values=new ContentValues();
        values.put("name", "Yan");
        values.put("password", "123456");
        db.insert("t_user", "id", values);
    }

    public void addDL(String path, String show, String title, long size) {
        values=new ContentValues();
        values.put("path", path);
        values.put("show", show);
        values.put("title", title);
        values.put("size", size);
        //set is_done to false until finished downloading
        values.put("is_done", "false");
        db.insert("tbl_dl", "id", values);
    }

    private void addTestValues() {
        values=new ContentValues();
        values.put("name", "How Did This Get Made?");
        values.put("xml_link", "https://rss.earwolf.com/how-did-this-get-made");
        values.put("xml_loc", "uncreated");
        db.insert("tbl_shows", "id", values);
        values = new ContentValues();
        values.put("name", "Stuff You Should Know");
        values.put("xml_link", "https://feeds.megaphone.fm/stuffyoushouldknow");
        values.put("xml_loc", "uncreated");
        db.insert("tbl_shows", "id", values);
        values = new ContentValues();
        values.put("name", "The Daily");
        values.put("xml_link", "https://rss.art19.com/the-daily");
        values.put("xml_loc", "uncreated");
        db.insert("tbl_shows", "id", values);
    }

    public void debugAddTestVals() {
        addTestValues();
    }

    public void debugClearTestVals() {
        clearShowTable();
    }

    public Cursor getTable(String tbl) {
        return db.rawQuery("select * from " + tbl, null);
    }

    public void clearShowTable() {
        sql = "delete from " + showTbl;
        db.execSQL(sql);
        sql = "vacuum";
        db.execSQL(sql);
    }

    private Cursor getShow(int id) {
        return db.rawQuery("select * from tbl_shows where id=" + id, null);
    }

    private void addShow(String name, String xml_link) {
        sql="insert into tbl_shows values(\'" + name + "\',\'" + xml_link + "\')";
        db.execSQL(sql);
    }

    private void deleteShow(String name){
        // use id
        //db.delete("tbl_shows", "id=1", null);
        // use name
        db.delete("tbl_shows", "name=\'" + name + "\'", null);
    }

    private void queryValue(){
        // use rawQuery
        Cursor c1 = db.rawQuery("select * from t_user", null);
        Cursor c2 = db.rawQuery("select * from t_user where id=1", null);
        Cursor c3 = db.rawQuery("select * from t_user where id=?", new String[]{"1"});
        // use query()
        Cursor c = db.query("t_user", new String[]{"id","name"}, "name=?", new String[]{"Yan"}, null, null, null);
        c.moveToFirst();
        while(!c.isAfterLast()){
            String msg="";
            for(int i=0,j=c.getColumnCount();i<j;i++){
                msg+="--"+c.getString(i);
            }
            Log.d("SQLite", "data:"+msg);
            c.moveToNext();
        }
    }

    private void update(){
        values = new ContentValues();
        values.put("password", "111111");
        // method 1
        db.update("t_user", values, "id=1", null);
        // method 2
        db.update("t_user", values, "name=? or password=?",new String[]{"Yan","123456"});
    }

    public void debugDropDB() {
        resetDB();
    }

    private void resetDB() {
        sql = "drop table " + showTbl;
        db.execSQL(sql);
        //sql = "drop table " + showTbl;
        //db.execSQL(sql);
        sql = "vacuum";
        db.execSQL(sql);
        Utils.logD("Ki","Dropped tables, now creating:");
        createTable();
    }

    public void closeDown() {
        db.close();
        values = null;
        dbFile = null;
        sql = null;
    }

}//end Ki class
