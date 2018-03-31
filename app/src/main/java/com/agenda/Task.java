package com.agenda;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
//import java.util.Date;
import java.util.Locale;

public class Task extends AppCompatActivity {

    //region >>> Variables
    Long dateTimeUnix;
    TextView dateTxtV, timeTxtV;
    EditText descTxtV;
    Calendar cal;
    String dateStr, timeStr, dateTimeStr, descriptionStr;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        //region >>> Task Description set-up
        descTxtV = (EditText)findViewById(R.id.txtDescription);
        //endregion

        //region >>>DatePicker Set Up
        dateTxtV = (TextView)findViewById(R.id.txtDate);
        cal = Calendar.getInstance();
        //Create Listener for DatePicker
        final DatePickerDialog.OnDateSetListener dateChangeListener = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker datePicker, int yr, int mo, int day)
            {
                cal.set(Calendar.YEAR, yr);
                cal.set(Calendar.MONTH, mo);
                cal.set(Calendar.DAY_OF_MONTH, day);
                updateDateTxtV();//Update txtDateV
            }
        };
        //Assign Listener to dateTxtV
        dateTxtV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog
                        (
                           Task.this,
                            dateChangeListener,
                            cal.get(Calendar.YEAR),
                            cal.get(Calendar.MONTH),
                            cal.get(Calendar.DAY_OF_MONTH)
                        ).show();
            }
        });
        updateDateTxtV();//set current date as soon as activity loads
        //endregion

        //region >>>TimePicker Set Up
        timeTxtV = (TextView)findViewById(R.id.txtTime);
        //Create Listener for DatePicker
        final TimePickerDialog.OnTimeSetListener timeChangeListener = new TimePickerDialog.OnTimeSetListener()
        {
            @Override
            public void onTimeSet(TimePicker timePicker, int _24hr, int min)
            {
                cal.set(Calendar.HOUR, _24hr);//auto converts 24hr  tie into 12hr time. But we need to set AM/PM
                cal.set(Calendar.MONTH, min);

                //https://developer.android.com/reference/java/text/SimpleDateFormat.html
                //https://stackoverflow.com/questions/2659954/timepickerdialog-and-am-or-pm
                //https://stackoverflow.com/questions/7527138/timepicker-how-to-get-am-or-pm
                /*String AM_PM ;
                if(_24hr < 12) {
                    AM_PM = "AM";
                } else {
                    AM_PM = "PM";
                }*/

                updateTimeTxtV();
            }
        };
        //Assign Listener to dateTxtV
        timeTxtV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new TimePickerDialog
                        (
                                Task.this,
                                timeChangeListener,
                                cal.get(Calendar.HOUR),
                                cal.get(Calendar.MINUTE),
                                false
                        ).show();
            }
        });
        updateDateTxtV();//set current date as soon as activity loads
        updateTimeTxtV();
        //endregion
    }

    //Update Date in text field
    private void updateDateTxtV()
    {
        String dateFormat ="EEE, d MMM yyyy";//"MM-dd-yy";
        //String dateFormat ="yyyyy.MMMMM.dd GGG";//"MM-dd-yy";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.CANADA);
        dateStr = sdf.format(cal.getTime());
        dateTxtV.setText(dateStr);

        //unix datetime for saving in db
        dateTimeUnix = cal.getTimeInMillis() / 1000L;
    }
    //Update Time in text field
    private void updateTimeTxtV()
    {
        String timeFormat ="hh:mm aaa";//12:08 PM
        SimpleDateFormat sdf = new SimpleDateFormat(timeFormat, Locale.CANADA);
        timeStr = sdf.format(cal.getTime());
        timeTxtV.setText(timeStr);

        //unix datetime for saving in db
        dateTimeUnix = cal.getTimeInMillis() / 1000L;
    }

    //Save Task
    public void btnClick_saveTask(View view)
    {
        //Get fresh copy of text from task description box
        descriptionStr = descTxtV.getText().toString();
        //instead of datetime str, save datetime as integer in db so that sort could be done
        //dateTimeStr = dateStr + " " + timeStr;

        //region >>> ERR : convert datetime str into Unix Time
      /*  SimpleDateFormat _12hrFormat = new SimpleDateFormat("hh:mm aaa");
        SimpleDateFormat _24hrFormat = new SimpleDateFormat("HH:mm:ss Z");

        DateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.CANADA);

        Date date = null;
        try //following parse MUST be in try block
        {
            dateTimeStr = dateStr + " " +_24hrFormat.format(_12hrFormat.parse(timeStr));

            date = (Date)formatter.parse(dateTimeStr);
            //long unixTime =  date.getTime();
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        System.out.println("Today is " +date.getTime());*/
        //endregion


        //save to database

        //move back to main activity
        Intent i = new Intent(this, Main.class);
        //temporary testing activity
        //Intent i = new Intent(this, Testing.class);
        startActivity(i);
    }
}
