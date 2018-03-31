package com.agenda;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class ZipCode extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zipcode);
    }

    //Save PostCode
    public void btnClick_saveZip(View view)
    {
        Intent i = new Intent(this, Main.class);
        startActivity(i);
    }
}
