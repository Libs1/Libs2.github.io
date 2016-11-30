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
import android.widget.SeekBar;
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
 * The class allows the user to control their drone using seekbars it allows for easier
 * debugging and can be used for demonstration purposes. This class does not communicate with the
 * database at all.
 */
public class DemoMode extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener{

    private String myUserName;  //Stores username if needed

    public final String ACTION_USB_PERMISSION = "com.example.rakul.hubsanflightcontroller.USB_PERMISSION";
    Button startButton, stopButton;
    TextView text1, text2, text3, text4;
    SeekBar throttleSlider, yawSlider, pitchSlider, rollSlider;
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
            //Arduino to Android Communication Handler (not needed at the moment)
            /*
            String data = null;
            try {
                data = new String(arg0, "UTF-8");
                data.concat("/n");
                tvAppend(textView, data);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            */
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
            }catch (Exception e){}
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_mode);

        myUserName = readMyName();

        usbManager = (UsbManager) getSystemService(this.USB_SERVICE);
        startButton = (Button) findViewById(R.id.buttonStart);
        stopButton = (Button) findViewById(R.id.buttonStop);
        textView = (TextView) findViewById(R.id.textView);

        text1 = (TextView) findViewById(R.id.t1);
        text2 = (TextView) findViewById(R.id.t2);
        text3 = (TextView) findViewById(R.id.t3);
        text4 = (TextView) findViewById(R.id.t4);

        try {
            throttleSlider = (SeekBar) findViewById(R.id.throttleSeekBar);
            yawSlider = (SeekBar) findViewById(R.id.yawSeekBar);
            pitchSlider = (SeekBar) findViewById(R.id.pitchSeekBar);
            rollSlider = (SeekBar) findViewById(R.id.rollSeekBar);
            throttleSlider.setOnSeekBarChangeListener(this);
            yawSlider.setOnSeekBarChangeListener(this);
            pitchSlider.setOnSeekBarChangeListener(this);
            rollSlider.setOnSeekBarChangeListener(this);

            setUiEnabled(false);
            IntentFilter filter = new IntentFilter();
            filter.addAction(ACTION_USB_PERMISSION);
            filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
            filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
            registerReceiver(broadcastReceiver, filter);
        }catch(Exception e){}
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
                        .setTitle("Demo Mode Help")
                        .setTitle("Demo Mode Help")
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


    public void setUiEnabled(boolean bool) {
        startButton.setEnabled(!bool);
        stopButton.setEnabled(bool);
        textView.setEnabled(bool);
    }

    public void onClickStart(View view) {
        try {
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
            //Toast.makeText(DemoMode.this, "The Arduino Is Not Connected", Toast.LENGTH_SHORT).show();
        }
    }

    public void onClickStop(View view) {
        byte input[] = {23, 0, 0x7f, 0x7f, 0x7f};
        serialPort.write(input);
        setUiEnabled(false);
        serialPort.close();
        tvAppend(textView, "\nSerial Connection Closed! \n");
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        try {
            switch (seekBar.getId()) {
                case R.id.throttleSeekBar:
                    tempVal = calculateSignal(progress);
                    text1.setText("Set Throttle: " + tempVal);
                    input[1] = (byte) tempVal;
                    serialPort.write(input);
                    break;

                case R.id.yawSeekBar:
                    tempVal = calculateSignal(progress);
                    text2.setText("Set Yaw: " + tempVal);
                    input[2] = (byte) tempVal;
                    serialPort.write(input);
                    break;

                case R.id.rollSeekBar:
                    tempVal = calculateSignal(progress);
                    text3.setText("Set Roll: " + tempVal);
                    input[3] = (byte) tempVal;
                    serialPort.write(input);
                    break;

                case R.id.pitchSeekBar:
                    tempVal = calculateSignal(progress);
                    text4.setText("Set Pitch: " + tempVal);
                    input[4] = (byte) tempVal;
                    serialPort.write(input);
                    break;
            }
        }catch(Exception e){}
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

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
        returnVal = (256*num)/100;
        return returnVal;
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
                //text.append('\n');
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally{
        }
        return text.toString();
    }
}
