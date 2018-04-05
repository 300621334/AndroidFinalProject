package com.agenda;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

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
    private LatLng myLoc;
    private String address = "M1G3S6";
    private TextView txtDisplayWeather;
    String weatherStr;
    AlertDialog.Builder alert;
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
        txtDisplayWeather = (TextView)findViewById(R.id.txtWeather);

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
    
    GetWeather();
    }

    private void GetWeather()
    {
        //get post code from shared prefs
        SharedPreferences prefs = getSharedPreferences("PostCode", 0);
        String pCode = prefs.getString("postCode", null);
        if(pCode != null)
        {
            address = pCode.trim();
        }

        //=============Get coords===============
        LocationManager location = (LocationManager)getSystemService(Context.LOCATION_SERVICE);//p-20 lecture
        Geocoder coder = new Geocoder(this);//p-28

        try //following MUST be in try blk or else won't compile
        {
            List<Address> geocodeResults = coder.getFromLocationName(address, 1);//p-33
            Iterator<Address> locations = geocodeResults.iterator();
            while (locations.hasNext())
            {
                Address loc = locations.next();
                String locInfo = String.format(Locale.CANADA,"Location: %f, %f\n", loc.getLatitude(), loc.getLongitude());
                myLoc = new LatLng(loc.getLatitude() , loc.getLongitude());
            }
        }
        catch (IOException e)
        {
            //
            Log.e("GeoAddress", "Failed to get location info", e);
        }
        //=====================================

        String url = "http://api.openweathermap.org/data/2.5/weather?";
        url+="lat="+myLoc.latitude;
        url+="&lon="+myLoc.longitude;
        //url+="&appid=13f04464b7119837cf1dc4fa8b39caa3"; //from OpenWeatherMap website
        url+="&appid=6f118e8e10b5354720a4816bca7b0a7a";

        Log.d("URL",url);
        new ReadJSONFeedTask().execute(url);
    }



    //Async Task
    private class ReadJSONFeedTask extends AsyncTask<String, Void, String>//must b sub-class. Perform background operations and publish results on the UI thread without having to manipulate threads
    {
        //at least this fn must be overridden
        protected String doInBackground(String... urls)//(String ...) is an array of parameters of type String, where as String[] is a single parameter.
        {
            return readJSONFeed(urls[0]);
        }

        protected void onPostExecute(String result)
        {
            try
            {
                //parsing weather data from OpenWeatherMap
                //JSONArray jsonArray = new JSONArray(result);
                JSONObject weatherJson = new JSONObject(result);//json str into a json obj w name-value pairs
                JSONObject main = weatherJson.getJSONObject("main");
                weatherStr = main.getString("temp");
                double celciusDouble = Double.parseDouble(weatherStr) - 273.16;
                int celcius = Double.valueOf(celciusDouble).intValue();
                txtDisplayWeather.setText(String.valueOf(celcius) + (char) 0x00B0 + "C");//must comment-out call to task in onCreate or else this doesn't display ath


                ////uncomment the code below & comment-out above 2 lines for it to work
                ////parsing survey data
                //JSONArray jsonArray = new JSONArray(result);
                //Log.i("JSON", "Number of surveys in feed: " + jsonArray.length());
                ////---print out the content of the json feed---
                //for (int i = 0; i < jsonArray.length(); i++)
                //{
                //    JSONObject jsonObject = jsonArray.getJSONObject(i);
                //    Toast.makeText(getBaseContext(), "Time is: " + jsonObject.getString("surveyTime") + " & ID is:  " + jsonObject.getString("appeId"), Toast.LENGTH_LONG).show();
                //    return;//just show 1st result = surveyTime & appeId == Time is: 12:19:47 & ID is: 1
                //}
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    //run in bg via doInBackground() of AsyncTask<>
    public String readJSONFeed(String address) {
        URL url = null;
        try {
            url = new URL(address);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        };

        StringBuilder stringBuilder = new StringBuilder();//For example, if z refers to a string builder object whose current contents are "start", then the method call z.append("le") would cause the string builder to contain "startle", whereas z.insert(4, "le") would alter the string builder to contain "starlet"

        HttpURLConnection urlConnection = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();//returns remote obj representing what was returned by that URL
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            InputStream content = new BufferedInputStream( urlConnection.getInputStream() );//Returns an input stream that reads from this open connection
            BufferedReader reader = new BufferedReader( new InputStreamReader(content) );
            String line;

            while ((line = reader.readLine()) != null)
            {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }
        return stringBuilder.toString();
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
        //alert dialogue before deleting db
        alert = new AlertDialog.Builder(this);//https://stackoverflow.com/questions/2115758/how-do-i-display-an-alert-dialog-on-android
        alert.setTitle("About to delete database!").setMessage("Are you sure you want to delete all tasks?").show();

        getApplication().deleteDatabase("myDb");
    }

    //start activity to send SMS
    public void btn_StartSmsActivity(View view)
    {
        startActivity(new Intent(this, SMS.class));
    }
}
