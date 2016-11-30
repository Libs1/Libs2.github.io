package com.example.rakul.hubsanflightcontroller;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.concurrent.TimeUnit;

/*
 * @author: Richard Clapham
 * @author: Rakul Mahenthiran
 * @date: 4/8/2016
 * This is the SignUp screen it will allow the user to create an account with our application.
 * Currently the account will hold their runtime information as well as allow for
 * account recovery options through the web interface.
 */

public class SignUp extends AppCompatActivity {

    private Button SignUpButton;
    private String myEmail;
    private String myName;
    private String myPassword;
    private String myPasswordCheck;
    private EditText EmailEditText;
    private EditText UsernameEditText;
    private EditText PasswordEditText;
    private EditText cPasswordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        EmailEditText = (EditText) findViewById(R.id.editText2);
        UsernameEditText = (EditText) findViewById(R.id.editText4);
        PasswordEditText = (EditText) findViewById(R.id.editText5);
        cPasswordEditText = (EditText) findViewById(R.id.editText6);
        SignUpButton = (Button) findViewById(R.id.button5);

        if (isTablet()) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        //checks if network is avaialble if it isnt disables the signup button
        if(!isNetworkAvailable()){
            SignUpButton.setEnabled(false);
        }

        //When the signup button is clicked it will check the fields to make sure there is
        //a valid email address username and password it will then check again to make sure that
        //you have an internet connection and if you do it will create your account
        SignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myEmail = EmailEditText.getText().toString();
                myName = UsernameEditText.getText().toString().toLowerCase();
                myPassword = PasswordEditText.getText().toString();
                myPasswordCheck = cPasswordEditText.getText().toString();

                if (isValidEmail(myEmail)) {
                    if ((myPassword.length() > 0) && (myName.length() > 0)) {
                        if (myName.length() < 10){
                            if (myPassword.equals(myPasswordCheck)) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
                                builder.setCancelable(false);
                                builder.setTitle(getResources().getString(R.string.ConfirmS1));
                                builder.setMessage(getResources().getString(R.string.check2));
                                builder.setPositiveButton(getResources().getString(R.string.Confirm2), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //connect to DB add Signup here
                                        if (isNetworkAvailable()) {
                                            new SignUpActivity(getBaseContext(), 0).execute(myName, myPassword, myEmail);
                                        } else {
                                            Toast.makeText(SignUp.this, getResources().getString(R.string.network), Toast.LENGTH_SHORT).show();
                                            SignUpButton.setEnabled(false);
                                        }
                                    }
                                });
                                builder.setNegativeButton(getResources().getString(R.string.Cancel1), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Toast.makeText(SignUp.this, getResources().getString(R.string.CancelS2), Toast.LENGTH_SHORT).show();
                                    }
                                });
                                builder.show();
                            }else{Toast.makeText(SignUp.this, getResources().getString(R.string.incorrect2), Toast.LENGTH_SHORT).show();}
                        }else{Toast.makeText(SignUp.this, getResources().getString(R.string.incorrect6), Toast.LENGTH_SHORT).show();}
                    }else{Toast.makeText(SignUp.this, getResources().getString(R.string.incorrect3), Toast.LENGTH_SHORT).show();}
                }else{Toast.makeText(SignUp.this, getResources().getString(R.string.incorrect4), Toast.LENGTH_SHORT).show();}
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
                        .setTitle("Sign Up Help")
                        .setMessage("Welcome to the Flight Controller Application!\n\n\n" +
                                "Enter a Valid E-mail Address and unique username to associate with your account.\n\n" +
                                "Enter a password and retype the password in order to confirm then press Sign Up!\n\n" +
                                        "For Account modification options please vist our website @ cengdrone.esy.es/index.html"
                        )
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                            }
                        })
                        .setIcon(getResources().getDrawable(R.drawable.book))
                        .show();
                return true;
            case R.id.logout_icon:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Boolean that returns true if email is valid
    public final static boolean isValidEmail(CharSequence target)
    {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    //Checks if the device is a tablet returns true if it is
    private boolean isTablet() {
        return (this.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    /*Check if network is available
     * @return boolean if network is avaialble returns true
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /*
    SignUpActivity will take in your requested log in information and create an account based off
    the requested information. The username needs to be unique and you will not be allowed to
    create an account withe same username as another user
     */
    private class SignUpActivity  extends AsyncTask<String,Void,String>
    {
        private Context context;
        private int byGetOrPost = 0;
        private String myCheck = "";
        ProgressDialog builder;

        //flag 0 means get and 1 means post.(By default it is get.)
        public SignUpActivity(Context context,int flag) {
            this.context = context;
            byGetOrPost = flag;
        }

        //Disables signup button inorder to prevent the user from spamming the database
        //And creates a progres dialog so the user know that an action is being performed
        protected void onPreExecute()
        {
            SignUpButton.setEnabled(false);
            builder = new ProgressDialog(SignUp.this);
            builder.setCancelable(false);
            builder.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            builder.setTitle(getResources().getString(R.string.signingUp));
            builder.setMessage(getResources().getString(R.string.wait0));
            builder.show();
        }

        //Sends the users credentials to the server and creates an account
        @Override
        protected String doInBackground(String... arg0) {
            if(byGetOrPost == 0){ //means by Get Method

                try{
                    String username = arg0[0];
                    String password = arg0[1];
                    String email = arg0[2];
                    String link = "http://cengdrone.esy.es/DroneSignUp.php?username="+username+"& password="+password+"& email="+email;
                    link = link.replaceAll(" ", "%20");

                    URL url = new URL(link);
                    HttpClient client = new DefaultHttpClient();
                    HttpGet request = new HttpGet();
                    request.setURI(new URI(link));
                    HttpResponse response = client.execute(request);
                    BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

                    StringBuffer sb = new StringBuffer("");
                    String line="";

                    while ((line = in.readLine()) != null) {
                        sb.append(line);
                        break;
                    }
                    in.close();
                    myCheck = sb.toString();
                    return sb.toString();
                }
                catch(Exception e){Toast.makeText(context, getResources().getString(R.string.signupF), Toast.LENGTH_SHORT).show(); return new String("Exception: " + e.getMessage());}
            }
            else{Toast.makeText(context, getResources().getString(R.string.signupF), Toast.LENGTH_SHORT).show(); return "False";}
        }

        //Lets the user know if there registration was successful
        @Override
        protected void onPostExecute(String result){
            SignUpButton.setEnabled(true);
            builder.dismiss();
            if(myCheck.equals("recordexists\t\t")){
                Toast.makeText(context, getResources().getString(R.string.signupF2), Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(context, getResources().getString(R.string.signupS), Toast.LENGTH_SHORT).show();
                Intent myIntent = new Intent(SignUp.this, SignIn.class);
                Bundle myBundle = new Bundle();
                myIntent.putExtras(myBundle);
                SignUp.this.startActivity(myIntent);
            }
        }
    }
}
