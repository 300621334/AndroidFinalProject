package com.agenda;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class Main extends AppCompatActivity {

    //region >>> Variables
    db db;
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


        //getApplication().deleteDatabase("myDb");//ONLY if db gets corupt then uncomment this line & run to delete db

        //initialize db helper class
        db = new db(getApplicationContext());
        listV = (ListView)findViewById(R.id.listV);
        layout = (LinearLayout)findViewById(R.id.layTasks);

        //find all students
        String sql = "select userId AS _id, firstName, status from users JOIN payments using (userId);";
        Cursor c = db.getAll();
        c.moveToFirst();
        int index = c.getColumnIndex("_desc");
        String aDescription = c.getString(index);

        //region >>> refreshing the list after an item deleted is NOT working
        /* myCurAdaptor.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged()
            {
                super.onChanged();

                Cursor cur = db.getAll();
                cur.moveToFirst();

                myCurAdaptor.swapCursor(cur);
            }
        });*/

        //reQuery cursor
        //c.requery();
        //endregion

        //set CursorAdapter for ListView
        myCurAdaptor = new MyCursorAdapter(this, c);
        listV.setAdapter(myCurAdaptor);
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
