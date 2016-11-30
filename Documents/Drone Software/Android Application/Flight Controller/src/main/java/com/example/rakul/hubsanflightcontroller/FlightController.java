package com.example.rakul.hubsanflightcontroller;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;

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
import java.util.HashMap;
import java.util.Map;

/*
 * @author: Richard Clapham
 * @author: Rakul Mahenthiran
 * @date: 4/8/2016
 * This class is responsible for the actual communication between the Arduino and Android device.
 * This class takes the joysticks current position and sends them to the Arduino which allows
 * the Arduino to control the drone. This class has Async classes to communicate with the database
 */

public class FlightController extends AppCompatActivity {

    TextView txtX1, txtY1;
    TextView txtX2, txtY2;
    DualJoystickView joystick;
    private String myUserName;
    private long timeStart;
    private long timeEnd;
    private long timeRan;

    public final String ACTION_USB_PERMISSION = "com.example.rakul.hubsanflightcontroller.USB_PERMISSION";
    Button startButton, stopButton;
    byte input[] = {23, 0, 0x7f, 0x7f, 0x7f};//default input
    TextView textView;
    int tempVal;
    UsbManager usbManager;
    UsbDevice device;
    UsbSerialDevice serialPort;
    UsbDeviceConnection connection;

    UsbSerialInterface.UsbReadCallback mCallback = new UsbSerialInterface.UsbReadCallback() { //Defining a Callback which triggers whenever data is read.
        @Override
        public void onReceivedData(byte[] arg0) {
        }
    };

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { //Broadcast Receiver to automatically start and stop the Serial connection.
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (intent.getAction().equals(ACTION_USB_PERMISSION)) {
                    boolean granted = intent.getExtras().getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED);
                    if (granted) {
                        connection = usbManager.openDevice(device);
                        serialPort = UsbSerialDevice.createUsbSerialDevice(device, connection);
                        if (serialPort != null) {
                            if (serialPort.open()) { //Set Serial Connection Parameters.
                                setUiEnabled(true);
                                serialPort.setBaudRate(115200);
                                serialPort.setDataBits(UsbSerialInterface.DATA_BITS_8);
                                serialPort.setStopBits(UsbSerialInterface.STOP_BITS_1);
                                serialPort.setParity(UsbSerialInterface.PARITY_NONE);
                                serialPort.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);
                                serialPort.read(mCallback);
                                tvAppend(textView, "Serial Connection Opened!\n");

                            } else {
                                Log.d("SERIAL", "PORT NOT OPEN");
                            }
                        } else {
                            Log.d("SERIAL", "PORT IS NULL");
                        }
                    } else {
                        Log.d("SERIAL", "PERM NOT GRANTED");
                    }
                } else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)) {
                    onClickStart(startButton);
                } else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_DETACHED)) {
                    onClickStop(stopButton);
                }
            }catch(Exception e){}
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flight_controller);
        
        myUserName = readMyName();

        txtX1 = (TextView)findViewById(R.id.TextViewX1);
        txtY1 = (TextView)findViewById(R.id.TextViewY1);

        txtX2 = (TextView)findViewById(R.id.TextViewX2);
        txtY2 = (TextView)findViewById(R.id.TextViewY2);

        joystick = (DualJoystickView)findViewById(R.id.dualjoystickView);

        joystick.setOnJostickMovedListener(_listenerLeft, _listenerRight);

        usbManager = (UsbManager) getSystemService(this.USB_SERVICE);
        startButton = (Button) findViewById(R.id.buttonStart);
        stopButton = (Button) findViewById(R.id.buttonStop);
        textView = (TextView) findViewById(R.id.textView);

        setUiEnabled(false);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(broadcastReceiver, filter);
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
                        .setTitle("Flight Controller Help")
                        .setMessage("Please follow the following instructions to bind your drone: \n\n\n" +
                                "1) Connect the Flight Controller Hardware to the android device using an OTG adapter and micro USB cable.\n\n" +
                                "2) If the Android device prompts the user to accept the serial communication, select 'Accept'.\n\n" +
                                "3) The application will now display 'Serial Communication Open', if not, click the 'Serial Begin' button.\n\n" +
                                "4) Tap the left Joystick to set throttle to 0.\n\n" +
                                "5) Power ON the drone. Once the led's on the drone are solid, you are ready to have some fun!")
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

    @Override
    protected void onStop() {
        if(!readMyName().equals("GUEST")) {
            new DroneEnd(getBaseContext(), 0).execute();
        }
        super.onStop();
    }

    public void setUiEnabled(boolean bool) {
        startButton.setEnabled(!bool);
        stopButton.setEnabled(bool);
        textView.setEnabled(bool);
    }

    public void onClickStart(View view) {
        try {
            if(!readMyName().equals("GUEST")) {
                new DroneStart(getBaseContext(), 0).execute();
            }
            HashMap<String, UsbDevice> usbDevices = usbManager.getDeviceList();
            if (!usbDevices.isEmpty()) {
                boolean keep = true;
                for (Map.Entry<String, UsbDevice> entry : usbDevices.entrySet()) {
                    device = entry.getValue();
                    int deviceVID = device.getVendorId();
                    if (deviceVID == 0x2341)//Arduino Vendor ID
                    {
                        PendingIntent pi = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
                        usbManager.requestPermission(device, pi);
                        keep = false;
                    } else {
                        connection = null;
                        device = null;
                    }

                    if (!keep)
                        break;
                }
            }
        }catch(Exception e){
            //Toast.makeText(FlightController.this, "The Arduino Is Not Connected", Toast.LENGTH_SHORT).show();
        }
    }

    public void onClickStop(View view) {
        if(!readMyName().equals("GUEST")) {
            new DroneEnd(getBaseContext(), 0).execute();
        }
        byte input[] = {23, 0, 0x7f, 0x7f, 0x7f};
        serialPort.write(input);
        setUiEnabled(false);
        serialPort.close();
        tvAppend(textView, "\nSerial Connection Closed! \n");
    }

    private void tvAppend(TextView tv, CharSequence text) {
        final TextView ftv = tv;
        final CharSequence ftext = text;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ftv.append(ftext);
            }
        });
    }

    private int calculateSignal (int num){
        int returnVal;
        returnVal = (255*num)/100;
        return returnVal;
    }

    private JoystickMovedListener _listenerLeft = new JoystickMovedListener() {


        @Override
        public void OnMoved(int yaw, int throttle) {
            try {
                throttle = throttle + 128;
                yaw = yaw + 128;
                if(throttle >= 256)
                    throttle = 255;
                if(yaw >= 256)
                    yaw = 255;
                if(yaw >= 108 && yaw <= 148)
                    yaw = 127;

                txtX1.setText(Integer.toString(throttle));
                txtY1.setText(Integer.toString(yaw));
                input[1] = (byte) throttle;
                input[2] = (byte) yaw;
                serialPort.write(input);
            }catch(Exception e){
                //Toast.makeText(FlightController.this, "The Arduino Is Not Connected", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void OnReleased() {
            try{

            }catch(Exception e){
            }

        }

        public void OnReturnedToCenter() {
            try{

            }catch(Exception e){
            }
        };
    };

    private JoystickMovedListener _listenerRight = new JoystickMovedListener() {

        @Override
        public void OnMoved(int pitch, int roll) {
            try{
            pitch = pitch + 128;
            roll = roll + 128;
                if(pitch >= 256)
                    pitch = 255;
                if(pitch == 128)
                    pitch = 127;
                if(roll >= 256)
                    roll = 255;
                if(roll == 128)
                    roll = 127;

            txtX2.setText(Integer.toString(pitch));
            txtY2.setText(Integer.toString(roll));

            input[3] = (byte) pitch;//left or right
            input[4] = (byte) roll;//forward or backward
            serialPort.write(input);
            }catch(Exception e){
                //Toast.makeText(FlightController.this, "The Arduino Is Not Connected", Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        public void OnReleased() {
            try{

            }catch(Exception e){
            }
        }

        public void OnReturnedToCenter() {
            try{

            }catch(Exception e){
            }

        };
    };

    //Gets username from file
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
        finally{
        }
        return text.toString();
    }


    /*
 * @author: Richard Clapham
 * @author: Rakul Mahenthiran
 * @date: 4/8/2016
 * Sends an get request to the mySQL server that will log a start command this is used later
 * to parse data.
 */
    private class DroneStart extends AsyncTask<String,Void,String>
    {
        private Context context;
        private int byGetOrPost = 0;

        public DroneStart(Context context,int flag) {
            this.context = context;
            byGetOrPost = flag;
        }

        protected void onPreExecute() {}

        @Override
        protected String doInBackground(String... arg0) {
            if(byGetOrPost == 0){ //means by Get Method
                try{
                    String link = "http://cengdrone.esy.es/DroneStart.php?username="+myUserName;
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
                    return sb.toString();
                }
                catch(Exception e) {return "False";}
            }
            else {return "False";}
        }

        @Override
        protected void onPostExecute(String myString)
        {
            timeStart = System.currentTimeMillis() / 1000;
        }
    }

    /*
 * @author: Richard Clapham
 * @author: Rakul Mahenthiran
 * @date: 4/8/2016
 * Sends an get request to the mySQL server that will log a end command this is used later
 * to parse data.
 */

    private class DroneEnd extends AsyncTask<String,Void,String>
    {
        private Context context;
        private int byGetOrPost = 0;
        private String myRun;

        //flag 0 means get and 1 means post.(By default it is get.)
        public DroneEnd(Context context,int flag) {
            this.context = context;
            byGetOrPost = flag;
        }

        protected void onPreExecute() {
            timeEnd = System.currentTimeMillis() / 1000;
            try {
                timeRan = timeEnd - timeStart;
                myRun = Long.toString(timeRan);
            }catch(Exception e){}
        }

        @Override
        protected String doInBackground(String... arg0) {
            if(byGetOrPost == 0){ //means by Get Method
                try{
                    String link = "http://cengdrone.esy.es/DroneEnd.php?username="+myUserName+"& duration="+myRun;
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
                    return sb.toString();
                }
                catch(Exception e) {return "False";}
            }
            else {return "False";}
        }

        @Override
        protected void onPostExecute(String myString) {}
    }
}

