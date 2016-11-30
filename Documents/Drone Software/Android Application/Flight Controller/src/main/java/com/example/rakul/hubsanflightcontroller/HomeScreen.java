package com.example.rakul.hubsanflightcontroller;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;

   /*
 * @author: Richard Clapham
 * @author: Rakul Mahenthiran
 * @date: 4/8/2016
 * Homescreen for the app after sign in that allows the user to choose between demo mode,
 * flight controller and flight data.
 */

public class HomeScreen extends AppCompatActivity {
    private Button controllerButton, dataButton, demoButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        controllerButton = (Button) findViewById(R.id.controllerBtn);
        dataButton = (Button) findViewById(R.id.dataBtn);
        demoButton = (Button) findViewById(R.id.demoBtn);

        controllerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeScreen.this, FlightController.class));
            }
        });

        if(readMyName().equals("GUEST"))
            dataButton.setVisibility(View.GONE);

        dataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeScreen.this, FlightData.class));
            }
        });

        demoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeScreen.this, DemoMode.class));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem item = menu.findItem(R.id.logout_icon);
        item.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.help_icon:
                new AlertDialog.Builder(this)
                        .setTitle("Menu Selection Help")
                        .setMessage("Lets get Flying!\n\n\n" +
                                "Flight Controller: The Flight Controller allows you to operate your drone using software based joysticks.\n\n" +
                                "Demo Mode: The Demo Mode is a testing environment where users can learn the operation of each flight control.\n\n" +
                                "Flight Data: Flight Data allows users to view their flight logs and battery life details. (Note: this feature is only available for users signed in as members)")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                            }
                        })
                        .setIcon(getResources().getDrawable(R.drawable.book))
                        .show();
                return true;
            case android.R.id.home:
                if(readMyName().equals("GUEST")) {
                    Intent intent = new Intent(getApplicationContext(), SignIn.class);
                    startActivity(intent);
                    return true;
                }
                else {
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
                }

            //case android.R.id.home:
            //    return true;
            default:
                //return super.onOptionsItemSelected(item);
                return true;
        }
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

    @Override
    public void onBackPressed() {
    }
}
