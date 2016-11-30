#define RES1 1023
#define RES2 32
#define AREF 255

int startTime;
double throttleVal, rudderVal;
int throttleInput, rudderInput;
int count = 0;
int xIn, yIn, zIn;
boolean calibrated = false;
int serialAvailable = 1;

//pins used
const int xInput = A0;
const int yInput = A1;
const int zInput = A2;
const int throttlePot = A3;
const int rudderPot = A4;
const int buttonPin = 2;
const int ledPin = 4;
 
// Accelerometer Raw Ranges:
// initialize to mid-range and allow calibration to
// find the minimum and maximum for each axis
int xRawMin = 512;
int xRawMax = 512;
 
int yRawMin = 512;
int yRawMax = 512;
 
int zRawMin = 512;
int zRawMax = 512;
 
// Accelerometer takes multiple samples to reduce noise
const int sampleSize = 10;


void setup() {
  Serial.begin(115200);
  A7105_Setup(); 
  initialize();
  startTime = micros();
  pinMode(ledPin, OUTPUT);
  pinMode(throttlePot, INPUT);
  pinMode(rudderPot, INPUT);
  analogReference(EXTERNAL);
  throttle = rudder = aileron = elevator = 0;
  digitalWrite(ledPin, LOW);
  if(analogRead(throttlePot) == 0){
    serialAvailable = 0;  
  }
}  


void loop() { 
  //Serial.println(serialAvailable);
  if(serialAvailable == 1) {
    if (Serial.available( )> 4) {
      if (Serial.read()!=23) {
        
        throttle = rudder = aileron = elevator = 0;
      } else {
        throttle=Serial.read();
        rudder=Serial.read();
        aileron=Serial.read();
        elevator=Serial.read();
      }
    }
  }

  if(serialAvailable == 0) {
    throttleVal=analogRead(throttlePot);  
    throttleVal = (AREF*(throttleVal/RES1));
      
    rudderVal=analogRead(rudderPot);  
    rudderVal = (AREF*(rudderVal/RES1));
    rudderVal = 255 - rudderVal;
      
    throttle=(byte)throttleVal;
    rudder=(byte)rudderVal;
    aileron=(byte)127;//x
    elevator=(byte)127;//y
  
    if (calibrated == true){      
          useAccelerometer();  
    }
  
    if(digitalRead(buttonPin) == LOW){
      calibrated = true;  
    }
  }
  
  int hubsanWait = hubsan_cb();
  delayMicroseconds(hubsanWait);
}  






void useAccelerometer(){
  int xRaw = ReadAxis(xInput);
  int yRaw = ReadAxis(yInput);
  int zRaw = ReadAxis(zInput);

  if (digitalRead(buttonPin) == LOW)
  {
    digitalWrite(ledPin, HIGH);
    AutoCalibrate(xRaw, yRaw, zRaw);
  }
  else
  {
    digitalWrite(ledPin, LOW); 
    // Convert raw values to 'milli-Gs"
    long xScaled = map(xRaw, xRawMin, xRawMax, -1000, 1000);
    long yScaled = map(yRaw, yRawMin, yRawMax, -1000, 1000);
    long zScaled = map(zRaw, zRawMin, zRawMax, -1000, 1000);

    // re-scale to fractional Gs
    float xAccel = xScaled / 1000.0;
    float yAccel = yScaled / 1000.0;
    float zAccel = zScaled / 1000.0;

    //set limit to all axis 'G' range from -16 to 16
    if(xAccel > 16.0){
      xAccel = 16.0;
    }
    if(xAccel < (-16.0)){
      xAccel = (-16.0);
    }

    if(yAccel > 16.0){
      yAccel = 16.0;
    }
    if(yAccel < (-16.0)){
      yAccel = (-16.0);
    }

    if(zAccel > 16.0){
      zAccel = 16.0;
    }
    if(zAccel < (-16.0)){
      zAccel = (-16.0);
    }

    //add 16 to all 'G' values to shift range to 0 to 32
    xAccel = (xAccel * (-1)) + 16;
    yAccel += 16;
    zAccel += 16;

    //modify the 0 to 32 range to 0 to 255 inorder to use as analog input for drone
    xIn = AREF*(xAccel/RES2);
    yIn = AREF*(yAccel/RES2);
    zIn = AREF*(zAccel/RES2); 

    aileron=(byte)xIn;//x
    elevator=(byte)yIn;//y
    //Testing
    //Serial.print("Your X and Y axis are set to:   ");
    //Serial.print(xIn);
    //Serial.print(", ");
    //Serial.println(yIn);
    //Serial.print("Throttle and Rudder: ");
    //Serial.print(throttleVal);
    //Serial.print(" ,");
    //Serial.println(rudderVal);
    //Serial.println();
  }  
}

//
// Read "sampleSize" samples and report the average
//
int ReadAxis(int axisPin)
{
  long reading = 0;
  analogRead(axisPin);
  delay(1);
  for (int i = 0; i < sampleSize; i++)
  {
    reading += analogRead(axisPin);
  }
  return reading/sampleSize;
}
 
//
// Find the extreme raw readings from each axis
//
void AutoCalibrate(int xRaw, int yRaw, int zRaw)
{
  //Serial.println("Calibrate");
  if (xRaw < xRawMin)
  {
    xRawMin = xRaw;
  }
  if (xRaw > xRawMax)
  {
    xRawMax = xRaw;
  }
  
  if (yRaw < yRawMin)
  {
    yRawMin = yRaw;
  }
  if (yRaw > yRawMax)
  {
    yRawMax = yRaw;
  }
 
  if (zRaw < zRawMin)
  {
    zRawMin = zRaw;
  }
  if (zRaw > zRawMax)
  {
    zRawMax = zRaw;
  }
}


  

