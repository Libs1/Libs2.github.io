package com.example.rakul.hubsanflightcontroller;

import android.support.v7.app.AppCompatActivity;

import java.io.Serializable;

/*
* @author: Richard Clapham
* @author: Rakul Mahenthiran
* @date: 4/8/2016
* Object file used to store the drones runtime information before it is parsed into the listview
*/
public class FlightDataFile extends AppCompatActivity implements Serializable
{
    private static final long serialVersionUID = -7060210544600464481L;
    private String Time1;
    private String Info1;
    private String Username1;
    private String Duration1;
    private String Time2;
    private String Info2;
    private String Username2;
    private String Duration2;
    private int BatteryLife;

    //Default Constructor
    public FlightDataFile() {
        this.Time1 = "";
        this.Info1 = "";
        this.Username1 = "";
        this.Duration1 = "";
        this.Time2 = "";
        this.Info2 = "";
        this.Username2 = "";
        this.Duration2 = "";
        this.BatteryLife = 0;
    }

    //My Setters
    public void setTime1(String i){Time1 = i;}
    public void setInfo1(String i){Info1 = i;}
    public void setUsername1(String i){Username1 = i;}
    public void setDuration1(String i){Duration1 = i;}

    public void setTime2(String i){Time2 = i;}
    public void setInfo2(String i){Info2 = i;}
    public void setUsername2(String i){Username2 = i;}
    public void setDuration2(String i){Duration2 = i;}

    public void setBatteryLife(int i){BatteryLife = i;}

    //My Getters
    public String getTime1(){return Time1;}
    public String getInfo1(){return Info1;}
    public String getUsername1(){return Username1;}
    public String getDuration1(){return Duration1;}

    public String getTime2(){return Time2;}
    public String getInfo2(){return Info2;}
    public String getUsername2(){return Username2;}
    public String getDuration2(){return Duration2;}

    public int getBatteryLife(){return BatteryLife;}
}
