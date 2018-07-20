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

import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;

//Ki is the DB Connection Class: Use this class for all DB-related tasks
public class Ki {

    private SQLiteDatabase db;
    private File dbFile;
    private ContentValues values;
    private String sql;
    private final String showTbl = "tbl_shows";

    public Ki() {
        try {
            dbFile = MainActivity.mainContext.getDatabasePath("test_db.db");
            db = openOrCreateDatabase(dbFile.getPath(), null);
            Utils.logD("Ki", "DB is alive at " + db.getPath()  + ".  Calling create...");
            createTable();
            //add test values to test the db -- TODO: make this a settings option
            //addTestValues();
            //clear the show table to clean up -- TODO: make this a settings option
            //clearShowTable();
        } catch (Exception exc) {
            Utils.logD("KiError","Constructor Error: " + exc.getMessage());
        }
    }

    private void createTable() {
        sql="create table if not exists tbl_shows(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL,xml_link TEXT NOT NULL)";
        db.execSQL(sql);
        Utils.logD("Ki","Executing the create (if not exists)...");
    }

    private void addValue(){
        values=new ContentValues();
        values.put("name", "Yan");
        values.put("password", "123456");
        db.insert("t_user", "id", values);
    }

    private void addTestValues() {
        values=new ContentValues();
        values.put("name", "How Did This Get Made?");
        values.put("xml_link", "how-did-this-get-made.xml");
        db.insert("tbl_shows", "id", values);
        values = new ContentValues();
        values.put("name", "Stuff You Should Know");
        values.put("xml_link", "stuff-you-should-know.xml");
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

    public void closeDown() {
        db.close();
        values = null;
        dbFile = null;
        sql = null;
    }

}//end Ki class