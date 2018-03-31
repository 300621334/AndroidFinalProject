package com.agenda;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Shafiq on 2018-03-31.
 */

public class db extends SQLiteOpenHelper
{
    //region >>>works for ORDER BY
    /*create table tasks( _id INTEGER PRIMARY KEY AUTOINCREMENT, _isdone INTEGER default 0, _desc TEXT, _datetime INTEGER);
    insert into tasks(  _desc , _datetime ) VALUES ("do grocery" , 1522800000);
    insert into tasks(  _desc , _datetime ) VALUES ("Assignment due" , 1459728000);
    insert into tasks(  _desc , _datetime ) VALUES ("do grocery" , 1514764800);
    insert into tasks(  _desc , _datetime ) VALUES ("Assignment due" , 1515024000);
    SELECT * FROM tasks order by _datetime desc;*/
    //endregion

    //region >>> not working for ORDER BY...
    /* not working for ORDER BY...
    create table tasks( _id INTEGER PRIMARY KEY AUTOINCREMENT, _isdone INTEGER default 0, _desc TEXT, _datetime TEXT);

    create table tasks( _id INTEGER PRIMARY KEY AUTOINCREMENT, desc TEXT, datetime TEXT);

    insert into tasks(  _desc , _datetime ) VALUES ("do grocery" , "Sat, 31 Mar 2018 04:54 PM");
    insert into tasks(  _desc , _datetime ) VALUES ("Assignment due" , "Sun, 01 Apr 2018 11:54 PM");
    insert into tasks(  _desc , _datetime ) VALUES ("do grocery" , "Sat, 31 Mar 2018 04:54 PM");
    insert into tasks(  _desc , _datetime ) VALUES ("Assignment due" , "Sun, 01 Apr 2018 11:54 PM");

    //not working on abobe inserts: SELECT * FROM 'tasks' order by date('_datetime');
    //nor do this work: SELECT * FROM [tasks] order by date([_datetime]);

    insert into tasks(  _desc , _datetime ) VALUES ("do grocery" , "2018/04/04");
    insert into tasks(  _desc , _datetime ) VALUES ("Assignment due" , "2016/04/04");
    insert into tasks(  _desc , _datetime ) VALUES ("do grocery" , "2018/01/01");
    insert into tasks(  _desc , _datetime ) VALUES ("Assignment due" , "2018/01/04");

    insert into tasks(  _desc , _datetime ) VALUES ("do grocery" , "2018-04-04");
    insert into tasks(  _desc , _datetime ) VALUES ("Assignment due" , "2016-04-04");
    insert into tasks(  _desc , _datetime ) VALUES ("do grocery" , "2018-01-01");
    insert into tasks(  _desc , _datetime ) VALUES ("Assignment due" , "2018-01-04");

     insert into tasks(  _desc , _datetime ) VALUES ("do grocery" , "2018-04-04 00:00:00.000");
    insert into tasks(  _desc , _datetime ) VALUES ("Assignment due" , "2016-04-04 00:00:00.000");
    insert into tasks(  _desc , _datetime ) VALUES ("do grocery" , "2018-01-01 00:00:00.000");
    insert into tasks(  _desc , _datetime ) VALUES ("Assignment due" , "2018-01-04 00:00:00.000");

    insert into tasks(  _desc , _datetime ) VALUES ("do grocery" , 1522800000);
    insert into tasks(  _desc , _datetime ) VALUES ("Assignment due" , 1459728000);
    insert into tasks(  _desc , _datetime ) VALUES ("do grocery" , 1514764800);
    insert into tasks(  _desc , _datetime ) VALUES ("Assignment due" , 1515024000);
    */
    //endregion

    //region >>> variables
    static final String DbName = "myDb",tblName = " tasks ", id_col=" _id ", sqlSelectAll = "select * from tasks";
    static int version = 1;
    long rowsAfected;
    String[] createTbls = {"create table "+ tblName +"( _id INTEGER PRIMARY KEY AUTOINCREMENT, _isdone INTEGER default 0, _desc TEXT, _datetime INTEGER);"}
            ,dropTbls = {"DROP TABLE IF EXISTS tasks;"}
            ,insertRows = {
            "insert into tasks(  _desc , _datetime ) VALUES  ('do grocery' , 1522800000);",
             "insert into tasks(  _desc , _datetime ) VALUES ('Assignment due' , 1459728000);",
             "insert into tasks(  _desc , _datetime ) VALUES ('do grocery', 1514764800);",
             "insert into tasks(  _desc , _datetime ) VALUES ('Assignment due', 1515024000);"

            //,"insert into tasks(  _desc , _datetime ) VALUES  ('do grocery' , 1522800000);",
            //"insert into tasks(  _desc , _datetime ) VALUES ('Assignment due' , 1459728000);",
            //"insert into tasks(  _desc , _datetime ) VALUES ('do grocery', 1514764800);",
            //"insert into tasks(  _desc , _datetime ) VALUES ('Assignment due', 1515024000);",
            //"insert into tasks(  _desc , _datetime ) VALUES  ('do grocery' , 1522800000);",
            //"insert into tasks(  _desc , _datetime ) VALUES ('Assignment due' , 1459728000);",
            //"insert into tasks(  _desc , _datetime ) VALUES ('do grocery', 1514764800);",
            //"insert into tasks(  _desc , _datetime ) VALUES ('Assignment due', 1515024000);",
            //"insert into tasks(  _desc , _datetime ) VALUES  ('do grocery' , 1522800000);",
            //"insert into tasks(  _desc , _datetime ) VALUES ('Assignment due' , 1459728000);",
            //"insert into tasks(  _desc , _datetime ) VALUES ('do grocery', 1514764800);",
            //"insert into tasks(  _desc , _datetime ) VALUES ('Assignment due', 1515024000);"
    };
    //endregion

    //region >>> Basic Methods
    public db(Context context/*, String name, SQLiteDatabase.CursorFactory factory, int version*/)
    {
        super(context, DbName, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        for ( int i=0; i < createTbls.length ;i++){db.execSQL(createTbls[i]);}
        for ( int i=0; i < insertRows.length ;i++){db.execSQL(insertRows[i]);}//few sample rows
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        for ( int i=0; i < dropTbls.length ;i++){db.execSQL(dropTbls[i]);}//drop all tables if version # changes
        onCreate(db);// Create tables again
    }
    //endregion

    //region >>> Custom Methods

    //get ALL records by JOINing students,payments,programs tables
    public Cursor getAll()
    {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery(sqlSelectAll , null);
    }

    //get one record
    public Cursor getRow(String id_col_value)
    {
        SQLiteDatabase db = getReadableDatabase();
        String sql =  "SELECT * FROM " + tblName + " WHERE " + id_col + " = " + id_col_value;
        return db.rawQuery(sql , null );
    }

    //add new records
    public long addRow(ContentValues values)
    {
        SQLiteDatabase db = getWritableDatabase();
        rowsAfected = db.insert(tblName, null, values);
        db.close();
        return rowsAfected;
    }

    //change existing records
    public long updateRow(ContentValues values, String filterByValue)
    {
        SQLiteDatabase db = getWritableDatabase();
        rowsAfected = db.update(tblName, values , id_col + " = ?" , new String[]{filterByValue});
        db.close();
        return rowsAfected;
    }

    //change existing records
    public long deleteRow(String rowIdToDelete)
    {
        SQLiteDatabase db = getWritableDatabase();
        rowsAfected = db.delete(tblName , id_col + " = ?" , new String[]{rowIdToDelete});
        db.close();
        return rowsAfected;
    }
    //endregion
}
