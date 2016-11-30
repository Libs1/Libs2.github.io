package com.example.rakul.hubsanflightcontroller;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

/*
* @author: Richard Clapham
* @author: Rakul Mahenthiran
* @date: 4/8/2016
* This class is responsible for getting a user runtime information and then parsing it into an
* array list which is then converted into a list view
*/
public class FlightData extends AppCompatActivity
{
    private ListView myListView;
    private ArrayList<FlightDataFile> myList;
    private String myUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flight_data);

        myList = new ArrayList<>();
        myUserName = readMyName();

        myListView = (ListView) findViewById(R.id.listView);

        //Checks for internet connection and if it is available connects to the reote database
        //Else it will use the local database
        if (isNetworkAvailable()) {
            new LoadFlightData(getBaseContext(), 0).execute();
        } else {
            Toast.makeText(FlightData.this,getResources().getString(R.string.offline), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        if(readMyName().equals("GUEST")) {
            MenuItem item = menu.findItem(R.id.logout_icon);
            item.setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.help_icon:
                new AlertDialog.Builder(this)
                        .setTitle("Flight Data Help")
                        .setMessage("Flight Data is displaying all the flights that took place under your account. For each flight, the start time, end time, duration and battery life are displayed.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                            }
                        })
                        .setIcon(getResources().getDrawable(R.drawable.book))
                        .show();
                return true;
            case R.id.logout_icon:
                new AlertDialog.Builder(this)
                        .setTitle("Leaving Already!")
                        .setMessage("Are you sure you want to Logout?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(getApplicationContext(), SignIn.class);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setIcon(getResources().getDrawable(R.drawable.logout))
                        .show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public String readMyName()
    {
        File file = new File(getFilesDir(),"myUser.txt");
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader buffReader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = buffReader.readLine()) != null) {
                text.append(line);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally{}

        return text.toString();
    }

    /*
 * @author: Richard Clapham
 * @author: Rakul Mahenthiran
 * @date: 4/8/2016
 * Sends an get request to the mySQL server that will ask for all the users data in the form of
 * a JSON array. The json array will then be parsed into an object.
 */

    private class LoadFlightData extends AsyncTask<String, Void, String> {
        private Context context;
        private int byGetOrPost = 0;
        JSONArray stuff = null;
        private static final String TAG_JSONNAME = "stuff";
        private static final String TAG_USERNAME = "User"; //edit these
        private static final String TAG_INFO = "Info";
        private static final String TAG_TIME = "Time";
        private static final String TAG_DURATION = "Duration";
        ProgressDialog builder;

        //flag 0 means get and 1 means post.(By default it is get.)
        public LoadFlightData(Context context, int flag) {
            this.context = context;
            byGetOrPost = flag;
        }

        //Creates a progressdialog so the user knows the app is performing actions.
        protected void onPreExecute() {
            builder = new ProgressDialog(FlightData.this);
            builder.setCancelable(false);
            builder.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            builder.setTitle(getResources().getString(R.string.loading));
            builder.setMessage(getResources().getString(R.string.wait2));
            builder.show();
        }

        //Connects to database and recieves a JSONArray
        @Override
        protected String doInBackground(String... arg0) {
            if (byGetOrPost == 0) { //means by Get Method
                try {
                    String link = "http://cengdrone.esy.es/DroneData.php?username="+myUserName;
                    link = link.replaceAll(" ", "%20");

                    URL url = new URL(link);
                    HttpClient client = new DefaultHttpClient();
                    HttpGet request = new HttpGet();
                    request.setURI(new URI(link));
                    HttpResponse response = client.execute(request);
                    HttpEntity httpEntity = response.getEntity();
                    String myResponse = EntityUtils.toString(httpEntity);

                    // Making a request to url and getting response
                    Log.d("Response: ", "> " + myResponse);

                    if (myResponse != null) {
                        try {
                            JSONObject jsonObj = new JSONObject(myResponse);
                            // Getting JSON Array node
                            stuff = jsonObj.getJSONArray(TAG_JSONNAME);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.e("ServiceHandler", "Couldn't get any data from the url");
                    }
                    return null;
                } catch (Exception e) {
                    return "Exception: " + e.getMessage();
                }
            } else {
                return "False";
            }
        }

        //Iterates through the data gathered from the return string and displays it.
        @Override
        protected void onPostExecute(String result) {
            builder.dismiss();
            try {
                int x = 0;
                if(stuff.length()%2==0){x = 0;}else{x = 1;}
                // looping through All Contacts
                for (int i = x; i < stuff.length(); i++) {
                    FlightDataFile myTemp = new FlightDataFile();

                    JSONObject c = stuff.getJSONObject(i);

                    String tempUserName1 = c.getString(TAG_USERNAME);
                    String tempInfo1 = c.getString(TAG_INFO);
                    String tempTime1 = c.getString(TAG_TIME);
                    String tempDuration1 = c.getString(TAG_DURATION);

                    myTemp.setUsername1(tempUserName1);
                    myTemp.setInfo1(tempInfo1);
                    myTemp.setTime1(tempTime1);
                    myTemp.setDuration1(tempDuration1);

                    i = i + 1;
                    JSONObject d = stuff.getJSONObject(i);

                    String tempUserName2 = d.getString(TAG_USERNAME);
                    String tempInfo2 = d.getString(TAG_INFO);
                    String tempTime2 = d.getString(TAG_TIME);
                    String tempDuration2 = d.getString(TAG_DURATION);

                    myTemp.setUsername2(tempUserName2);
                    myTemp.setInfo2(tempInfo2);
                    myTemp.setTime2(tempTime2);
                    myTemp.setDuration2(tempDuration2);

                    myList.add(myTemp);
                }
                myListView.setEmptyView(findViewById(R.id.empty_list_item));
                FlightDataAdapter arrayAdapter = new FlightDataAdapter(FlightData.this, R.layout.flight_data_view, myList);
                myListView.setAdapter(arrayAdapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
