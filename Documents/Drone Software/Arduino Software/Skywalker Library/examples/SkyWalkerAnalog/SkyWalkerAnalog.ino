//************************************************************************
//  HCD flying example
//  (C) DZL December2013
//
// Inputs:
// Throttle  analog 0
// Yaw       analog 1
// Pitch     analog 2
// Roll      analog 3
//
//************************************************************************

#include <HCD.h>

HCD drone0;

//Drone ID's (pick 4 random numbers)
unsigned char ID0[]={
  0x16,0x01,0x55,0x11};

void setup()
{
  Serial.begin(19200);
  Serial.println("\n\nCommunication Starting...\n");
}

unsigned long timer=0;

void loop()
{
  unsigned char c = '0';
    
    switch(c)
    {
    case '0':             
      if(drone0.inactive())
        drone0.bind(ID0);
      break;
    }

  if(millis()>=timer)
  {
    timer+=20;
    drone0.update(analogRead(0)/4,analogRead(1)/4,0x40,analogRead(2)/4,analogRead(3)/4,0x40,0x40,0);
    Serial.print("Throttle:\t");
    Serial.println(analogRead(0)/4);
    Serial.print("Yaw:\t\t");
    Serial.println(analogRead(1)/4);
    Serial.print("Pitch:\t\t");
    Serial.println(analogRead(2)/4);
    Serial.print("Roll:\t\t");
    Serial.println(analogRead(3)/4);
  }
}
































