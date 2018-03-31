package com.agenda;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class Main extends AppCompatActivity {

    //region >>> Variables
    db db;
    Cursor cursor;
    ContentValues values;
    long rowsAffected;
    TextView txt;
    LinearLayout layout;
    Button btn;
    MyCursorAdapter myCurAdaptor;
    ListView listV;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //AppCompatDelegate.setCompatVectorFromResourcesEnabled(true); //https://stackoverflow.com/questions/47526417/binary-xml-file-line-0-error-inflating-class-imageview

        //getApplication().deleteDatabase("myDb");//ONLY if db gets corupt then uncomment this line & run to delete db

        //initialize db helper class
        db = new db(getApplicationContext());
        listV = (ListView)findViewById(R.id.listV);
        layout = (LinearLayout)findViewById(R.id.layTasks);

        //find all students
        String sql = "select userId AS _id, firstName, status from users JOIN payments using (userId);";
        cursor = db.getAll();
        cursor.moveToFirst();
        int index = cursor.getColumnIndex("_desc");
        String aDescription = cursor.getString(index);


        //set CursorAdapter for ListView
        myCurAdaptor = new MyCursorAdapter(this, cursor);
        listV.setAdapter(myCurAdaptor);


        //region >>> Leave empty listener or else app crashes!!! - refreshing the list after an item deleted is NOT working
  /*      myCurAdaptor.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged()
            {
                //super.onChanged();


                //cursor.requery();
                //cursor.close();
                //myCurAdaptor.notifyDataSetChanged();
                //cursor.close();


                //cursor = db.getAll();
                //cursor.moveToFirst();
                //myCurAdaptor.changeCursor(cursor);


                //myCurAdaptor.set
                //myCurAdaptor.changeCursor(cursor);// swapCursor(cursor);

                //set CursorAdapter for ListView
                //myCurAdaptor = new MyCursorAdapter(getBaseContext(), cur);
                //listV.setAdapter(myCurAdaptor);


                //listV.invalidateViews();
            }
        });*/

        //reQuery cursor
        //c.requery();
        //endregion
    }

    //Create new task
    public void btnClick_NewTask(View view)
    {
        Intent i = new Intent(this, Task.class);
        startActivity(i);
    }

    //Edit PostCode
    public void click_editZip(View view)
    {
        Intent i = new Intent(this, ZipCode.class);
        startActivity(i);
    }

    //for debugging ONLY. Delete whole DB
    public void DeleteWholeDB(View view)
    {
        getApplication().deleteDatabase("myDb");
    }
}
