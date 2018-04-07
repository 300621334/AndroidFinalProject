package com.agenda;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;//checkSelfPermission(Manifest.permission.SEND_SMS)

import org.w3c.dom.Text;

public class SMS extends AppCompatActivity
{
    //region >>> Variables
    PendingIntent notifySmsSent;
    BroadcastReceiver listenToSmsSent, listenToSmsReceived;
    IntentFilter filterSmsRcvd;
    SmsMessage smsMessage;
    SMSReceiver otp;//for incoming SMS
    EditText txtPhV, txtSmsV;
    TextView txtSmsRcvdV;
    Button btnSendSMS;
    String phoneNum, smsTxt, SMS_SENT="SMS_SENT", smsReceived;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);

        //Get references to views
        txtPhV = (EditText)findViewById(R.id.txtPhone);
        txtSmsV = (EditText) findViewById(R.id.txtSendSMS);
        btnSendSMS = (Button)findViewById(R.id.btnSendSMS);
        txtSmsRcvdV = (TextView) findViewById(R.id.txtSmsReceived);

        //send broadcast hen sms successfully sent
        notifySmsSent = PendingIntent.getBroadcast(this, 0, new Intent(SMS_SENT), 0);


        //for incoming SMS
        otp=new SMSReceiver();

        //ask for permission to use SMS
        grant_permission();
    }

    //once activity comes into fg
    @Override
    protected void onResume()
    {
        super.onResume();

        //receive custom broadcast
        listenToSmsSent = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                switch (getResultCode())
                {
                    case Activity.RESULT_OK:
                        Toast.makeText(SMS.this, "SMS sent", Toast.LENGTH_LONG).show();
                        break;
                }
            }
        };

        //receive in-built broadcast
        filterSmsRcvd = new IntentFilter();
        //filterSmsRcvd.addAction("SMS_RECEIVED_ACTION");
        listenToSmsReceived = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                /*
               Bundle bundle = intent.getExtras();

                if (Build.VERSION.SDK_INT >= 19) //https://stackoverflow.com/questions/18876274/android-create-sms-from-pdu-deprecated-api
                {
                    SmsMessage[] msgs = Telephony.Sms.Intents.getMessagesFromIntent(intent);//err: require sd 19 or above. Current min is set to 16
                    String format = intent.getStringExtra("format");

                    SmsMessage sms = msgs[0];
                    Log.v("TAG", "handleSmsReceived" + (sms.isReplace() ? "(replace)" : "") +
                            " messageUri: " +
                            ", address: " + sms.getOriginatingAddress() +
                            ", body: " + sms.getMessageBody());

                    smsReceived = sms.getMessageBody();
                }
                else
                {
                    Object pdus[] = (Object[]) bundle.get("pdus");
                    smsMessage = SmsMessage.createFromPdu((byte[]) pdus[0]);
                    smsReceived = smsMessage.getMessageBody();

                }
                this.abortBroadcast();
                */


                //display received SMS
                    //txtSmsRcvdV.setText(smsReceived);
            }
        };

        //register listener
        registerReceiver(listenToSmsSent, new IntentFilter(SMS_SENT));
       // registerReceiver(listenToSmsReceived, filterSmsRcvd);


        //register broadcast receiver
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(otp, filter);
    }

    //once app is in bg
    @Override
    protected void onPause()
    {
        super.onPause();
        unregisterReceiver(listenToSmsSent);
        //unregisterReceiver(listenToSmsReceived);


        //for incoming SMS
        unregisterReceiver(otp);
    }

    //send SMS
    public void btn_SendSMS(View view)
    {
        phoneNum = txtPhV.getText().toString().trim();
        smsTxt = txtSmsV.getText().toString().trim();

        //check for SMS permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)//M for Marshmallow // Check Permission fails on Android OSes below Marshmallow
        {
            if(checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED)//import android.Manifest;//https://stackoverflow.com/questions/35056911/cannot-find-symbol-variable-send-sms
            {
                requestPermissions(new String[] {Manifest.permission.SEND_SMS}, 1);//1 could be any value that is returned as "requestCode" in onRequestPermissionsResult(), if user grants permission
            }
            else
            {
                SendSMS();
            }
        }


    }

    //method to send SMS
    private void SendSMS()
    {
        SmsManager smsMgr = SmsManager.getDefault();
        smsMgr.sendTextMessage(phoneNum,null, smsTxt, notifySmsSent, null);//add permissions to use send SMS
    }

    //check is user granted permission for SMS
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case 1:
            {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)//https://stackoverflow.com/questions/33666071/android-marshmallow-request-permission
                {
                    // permission was granted, yay!
                    SendSMS();
                }
                else
                {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "Permission denied for SMS", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    //Sub-Clss for receiving SMS
    private class SMSReceiver extends BroadcastReceiver
    {
        private Bundle bundle;
        private SmsMessage currentSMS;
        private String message;

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
                bundle = intent.getExtras();
                if (bundle != null) {
                    Object[] pdu_Objects = (Object[]) bundle.get("pdus");
                    if (pdu_Objects != null) {

                        for (Object aObject : pdu_Objects) {

                            currentSMS = getIncomingMessage(aObject, bundle);

                            String senderNo = currentSMS.getDisplayOriginatingAddress();

                            message = currentSMS.getDisplayMessageBody();
                            Toast.makeText(SMS.this, "senderNum: " + senderNo + " :\n message: " + message, Toast.LENGTH_LONG).show();
                        }
                        this.abortBroadcast();
                        // End of loop

                        txtSmsRcvdV.setText(message);
                    }
                }
            } // bundle null
        }//onReceive


        private SmsMessage getIncomingMessage(Object aObject, Bundle bundle) {
            SmsMessage currentSMS;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)//M for Marshmallow
            {
                String format = bundle.getString("format");
                currentSMS = SmsMessage.createFromPdu((byte[]) aObject, format);
            } else {
                currentSMS = SmsMessage.createFromPdu((byte[]) aObject);
            }

            return currentSMS;
        }//getIncomingMessage()
    }//class SMSReceiver

    //ask for permission to use SMS
    private void grant_permission()
    {
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {android.Manifest.permission.SEND_SMS};

        if (!hasPermissions(this, PERMISSIONS))
        {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
    }
    public static boolean hasPermissions(Context context, String... permissions)
    {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null)
        {
            for (String permission : permissions)
            {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED)
                {
                    return false;
                }
            }
        }
        return true;
    }

}//class SMS
