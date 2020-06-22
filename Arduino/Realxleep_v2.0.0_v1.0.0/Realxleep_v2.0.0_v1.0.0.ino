#include <CircularBuffer.h>
#include <MAX30100.h>
#include <MAX30100_BeatDetector.h>
#include <MAX30100_Filters.h>
#include <MAX30100_PulseOximeter.h>
#include <MAX30100_Registers.h>
#include <MAX30100_SpO2Calculator.h>

#include <DHT.h>

//DHT22
#include "DHT.h"
#define DHTPIN 4    //센서가 연결된 디지털핀
#define DHTTYPE DHT22   // DHT 22  (AM2302), AM2321
DHT dht(DHTPIN, DHTTYPE);

//DFBOBOT_CO2
#define         MG_PIN                       (A1)     
#define         BOOL_PIN                     (2)
#define         DC_GAIN                      (8.5)
#define         READ_SAMPLE_INTERVAL         (50)    
#define         READ_SAMPLE_TIMES            (5)
#define         ZERO_POINT_VOLTAGE           (0.298) 
#define         REACTION_VOLTGAE             (0.282)
float           CO2Curve[3]  =  {2.602,ZERO_POINT_VOLTAGE,(REACTION_VOLTGAE/(2.602-3))};

//MAX9814------------------------------------
const int sampleWindow = 50; // Sample window width in mS (50 mS = 20Hz)
unsigned int sample;


void setup() {
  Serial.begin(9600);
  //Serial.println("데이터의 순서는 !조도,소리,온도,습도,이산화탄소 이다.");
  
  dht.begin();

  pinMode(BOOL_PIN, INPUT);       
  digitalWrite(BOOL_PIN, HIGH);     
}
 
void loop() {
  delay(1000);
  Serial.print("!");

  //KY-018-------------------------------------------------
  int Lux = analogRead(A2);
  Serial.print(Lux);
  Serial.print(",");

  //MAX9814-------------------------------------------------
  unsigned long startMillis= millis();  // Start of sample window
  unsigned int peakToPeak = 0;   // peak-to-peak level
  unsigned int signalMax = 0;
  unsigned int signalMin = 1024;
  while (millis() - startMillis < sampleWindow)
  {
     sample = analogRead(0);
     if (sample < 1024)  // toss out spurious readings
     {
        if (sample > signalMax)
        {
           signalMax = sample;  // save just the max levels
        }
        else if (sample < signalMin)
        {
           signalMin = sample;  // saminve just the  levels
        }
     }
  }
  peakToPeak = signalMax - signalMin;  // max - min = peak-peak amplitude
  double sound = (peakToPeak * 5.0) / 1024;  // convert to volts
  Serial.print(sound);
  Serial.print(",");

  //DHT22---------------------------------------------------
  float h = dht.readHumidity(); //습도값을 읽어옴.
  float t = dht.readTemperature();//온도값을 읽어옴
  if (isnan(h) || isnan(t) ) {
    Serial.println("Failed to read from DHT sensor!");
    return;
  }
  Serial.print(t);
  Serial.print(",");
  Serial.print(h);
  Serial.print(",");


  //DFROBOT_CO2----------------------------------------
  int percentage;
  float volts;
  volts = MGRead(MG_PIN);
  percentage = MGGetPercentage(volts,CO2Curve);
  if (percentage == -1) {
      Serial.println( "0" );
  } else {
      Serial.println(percentage);
  }

}


float MGRead(int mg_pin)
{
    int i;
    float v=0;
    for (i=0;i<READ_SAMPLE_TIMES;i++) {
        v += analogRead(mg_pin);
        delay(READ_SAMPLE_INTERVAL);
    }
    v = (v/READ_SAMPLE_TIMES) *5/1024 ;
    return v;
}
int  MGGetPercentage(float volts, float *pcurve)
{
   if ((volts/DC_GAIN )>=ZERO_POINT_VOLTAGE) {
      return -1;
   } else {
      return pow(10, ((volts/DC_GAIN)-pcurve[1])/pcurve[2]+pcurve[0]);
   }
}
