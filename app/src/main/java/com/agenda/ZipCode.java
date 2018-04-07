package com.agenda;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class ZipCode extends AppCompatActivity {

    EditText pCodeV;
    String pCodeStr , postCodeRegEx = "[ABCEGHJKLMNPRSTVXYabceghjklmnprstvxy][0-9][ABCEGHJKLMNPRSTVWXYZabceghjklmnprstvxy] ?[0-9][ABCEGHJKLMNPRSTVWXYZabceghjklmnprstvxy][0-9]";//Canadian postal codes can't contain the letters D, F, I, O, Q, or U, and cannot start with W or Z:


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zipcode);

        pCodeV = (EditText)findViewById(R.id.txtZip);
    }

    //Save PostCode
    public void btnClick_saveZip(View view)
    {
        pCodeStr = pCodeV.getText().toString();
        if(!(pCodeStr.trim().matches(postCodeRegEx)) || pCodeStr.trim() == "")
        {
            Toast.makeText(this, "Invalid Post Code", Toast.LENGTH_LONG).show();
        }
        else
        {
            SharedPreferences prefs = getSharedPreferences("PostCode", 0);
            prefs.edit().putString("postCode", pCodeStr).apply();
        }


        Intent i = new Intent(this, ToDo.class);
        startActivity(i);
    }
}
