package com.agenda;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Date;
import java.text.SimpleDateFormat;

/**
 * Created by Shafiq on 2018-03-31.
 */

public class MyCursorAdapter extends CursorAdapter
{
    //region >>> Variables
    db db;
    ContentValues values;
    long rowsAffected;
    int taskID;
    String taskDescStr, taskDateStr;
    MyCursorAdapter itself;
    TextView txtDateV, txtDescV;
    Button btnDeleteTask;
    //endregion

    //Constructor
    public MyCursorAdapter(Context ctx, Cursor cursor)
    {
        super(ctx, cursor);
        values = new ContentValues();
        db = new db(ctx);
        itself = this;
    }

    //create a new view for ea row in cursor - using inflated list.xml as template
    @Override
    public View newView(Context ctx, Cursor cursor, ViewGroup parent)
    {
        //get values from db
        taskID = cursor.getInt(cursor.getColumnIndex("_id"));
        taskDescStr = cursor.getString(cursor.getColumnIndex("_desc"));
        taskDateStr = getDate( Long.parseLong( cursor.getString(cursor.getColumnIndex("_datetime"))));

        //inflate xml template for each item in ListView
        LayoutInflater inflator = LayoutInflater.from(ctx);
        View v = inflator.inflate(R.layout.listitem, parent, false);//v is LinearLayout of template
        txtDateV = (TextView)v.findViewById(R.id.txtDateTime);
        txtDescV = (TextView)v.findViewById(R.id.txtTaskDesc);
        btnDeleteTask = (Button)v.findViewById(R.id.btnDeleteTask);
        txtDescV.setText(taskDescStr);
        txtDateV.setText(taskDateStr);
        MyClkListener listen = new MyClkListener(taskID, db, this, parent, cursor);
        btnDeleteTask.setOnClickListener(listen);
        txtDescV.setOnClickListener(listen);
        return v;
    }

    //convert unix date into string
    private String getDate(Long timeStamp)
    {
        try{
            String dateFormat ="EEE, d MMM yyyy";
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat/*"MM/dd/yyyy "*/);
            Date netDate = (new Date(timeStamp * 1000L));//convert sec into ms
            return sdf.format(netDate);
        }
        catch(Exception ex){
            return "xx";
        }
    }

    //this method is unused but must be present
    @Override
    public void bindView(View view, Context context, Cursor cursor){}

    //custom listener for each button
    class MyClkListener implements View.OnClickListener
    {
        String taskIdStr;
        Context ctx;
        int taskId;
        db db;
        long rowsAffected;
        MyCursorAdapter curAdapter;
        ListView v;
        Cursor cursor;


        //constructor to pass user ID to listener for each btn
        public MyClkListener(int taskID, db db, MyCursorAdapter adapter, ViewGroup parent, Cursor cursor)
        {
            this.taskIdStr = String.valueOf(taskID);
            this.taskId = taskID;
            this.db = db;
            this.v = (ListView) parent;
            curAdapter = adapter;
            this.cursor = cursor;
        }

        @Override
        public void onClick(View view)
        {
            if(view instanceof Button)
            {
                //get context
                ctx = view.getContext();

                //delete row from db
                rowsAffected = db.deleteRow(taskIdStr);
                Toast.makeText(ctx, rowsAffected + " rows deleted", Toast.LENGTH_LONG).show();

                //Intent i = new Intent(ctx, Task.class);
                SharedPreferences prefs = ctx.getSharedPreferences("tasks",0);
                prefs.edit().putInt("taskID", taskID).commit();
                //ctx.startActivity(i);
                //((Activity)ctx).finish();

                //refresh ListView
                cursor.requery();
                curAdapter.notifyDataSetChanged();//not refreshing!!!

                //curAdapter.runQueryOnBackgroundThread(null);
                //v.invalidateViews();//not refreshing!!!
            }
            else if(view instanceof TextView)
            {
                ctx = view.getContext();

                Intent i = new Intent(ctx, Task.class);
                i.putExtra("taskId", taskId);
                ctx.startActivity(i);
            }

        }
    }
}
