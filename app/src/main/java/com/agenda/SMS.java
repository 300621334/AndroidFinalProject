package com.agenda;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

public class SMS extends AppCompatActivity
{
    EditText txtPhV, txtSmsV;
    Button btnSendSMS;
    String phoneNum, smsTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);

        //Get references to views
        txtPhV = (EditText)findViewById(R.id.txtPhone);
        txtSmsV = (EditText) findViewById(R.id.txtSendSMS);
        btnSendSMS = (Button)findViewById(R.id.btnSendSMS);

    }

    public void btn_SendSMS(View view)
    {
        phoneNum = txtPhV.getText().toString().trim();
        smsTxt = txtSmsV.getText().toString().trim();

        SmsManager smsMgr = SmsManager.getDefault();
        smsMgr.sendTextMessage(phoneNum,null, smsTxt, null, null);//add permissions to use send SMS
    }
}
