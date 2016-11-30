package com.example.rakul.hubsanflightcontroller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

/*
* @author: Richard Clapham
* @author: Rakul Mahenthiran
* @date: 4/8/2016
* Responsible for receiving an object of flightdatafile. This will take the onject and parse the
* data so that it is comparable with a listview.
*/
public class FlightDataAdapter extends ArrayAdapter<FlightDataFile>
{
    private ArrayList<FlightDataFile> objects;

    public FlightDataAdapter(Context context, int textViewResourceId, ArrayList<FlightDataFile> objects)
    {
        super(context, textViewResourceId, objects);
        this.objects = objects;
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {

        View v = convertView;

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.flight_data_view, null);
        }

        FlightDataFile i = objects.get(position);

        //Checks if object isn't null and if it isnt loads the data into the listview
        if (i != null) {

            // This is how you obtain a reference to the TextViews.
            // These TextViews are created in the XML files we defined.
            TextView t1 = (TextView) v.findViewById(R.id.textView5);
            TextView t2 = (TextView) v.findViewById(R.id.textView6);
            TextView t3 = (TextView) v.findViewById(R.id.textView8);
            TextView t4 = (TextView) v.findViewById(R.id.textView9);

            try {
                if (t1 != null) {
                    t1.setText(i.getTime1());
                }
                if (t2 != null) {
                    t2.setText(i.getTime2());
                }
                if (t3 != null) {
                    t3.setText(i.getDuration2()+"s");
                }
                if (t4 != null) {
                    String myDuration = i.getDuration2();
                    double myDurationDouble = Double.parseDouble(myDuration);
                    double myBatteryLife = (myDurationDouble/480) * 100;
                    myBatteryLife = 100 - myBatteryLife;
                    DecimalFormat df = new DecimalFormat("#.##");
                    myBatteryLife = Double.valueOf(df.format(myBatteryLife));
                    t4.setText("Approximent Battery Life:\t"+myBatteryLife+"%");
                    if(myBatteryLife < 0)
                        t4.setText("Approximent Battery Life:\t0.00%");
                }
            }catch(Exception e){}

        }
        // the view must be returned to our activity
        return v;
    }
}
