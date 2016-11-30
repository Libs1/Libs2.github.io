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

boolean isSerialAvailable = false;
int throttle = 0;
int yaw = 0;
int pitch = 0;
int roll = 0;

HCD drone0;

//Drone ID's (pick 4 random numbers)
unsigned char ID0[]={0x16,0x01,0x55,0x11};

void setup()
{
	Serial.begin(115200);
	Serial.println("\n\nCommunication Starting...\n");
}

unsigned long timer=0;

void loop()
{    
	if(Serial.available()){
		isSerialAvailable = true;
	}
	
	while(isSerialAvailable == true){
		unsigned char c = '0';
    
		switch(c){
			case '0':             
			if(drone0.inactive())
				drone0.bind(ID0);
			break;
		}

		if(millis()>=timer){
			if (Serial.available( )> 4){
				if (Serial.read()!=23){
					throttle = yaw = pitch = roll = 0;
				} 
				else {
					throttle=Serial.read();
					yaw=Serial.read();
					roll=Serial.read();
					pitch=Serial.read();
					yaw=255-yaw;
					roll=255-roll;
				}
			}
		timer+=20;
		drone0.update(throttle,yaw,0x40,pitch,roll,0x40,0x40,0);
		}
	}	
	
	while(isSerialAvailable == false){
		unsigned char c = '0';
    
		switch(c){
			case '0':             
			if(drone0.inactive())
				drone0.bind(ID0);
			break;
		}
	
		if(millis()>=timer){
			timer+=20;
			drone0.update(analogRead(0)/4,analogRead(1)/4,0x40,analogRead(2)/4,analogRead(3)/4,0x40,0x40,0);
		}
	}
}
































