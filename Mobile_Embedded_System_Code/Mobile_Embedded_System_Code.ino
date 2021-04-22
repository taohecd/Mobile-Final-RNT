#include "BluetoothSerial.h"
#include "DHT.h"

#if !defined(CONFIG_BT_ENABLED) || !defined(CONFIG_BLUEDROID_ENABLED)
#error Bluetooth is not enabled! Please run `make menuconfig` to and enable it
#endif
#define DHTPIN 4 

BluetoothSerial SerialBT;
const int LEDpin = 32;

#define DHTTYPE DHT11
DHT dht11(DHTPIN, DHTTYPE);

void setup() {
  Serial.begin(115200);
  SerialBT.begin("ESP32test"); //Bluetooth device name
  Serial.println("The device started, now you can pair it with bluetooth!");
  pinMode(LEDpin, OUTPUT);
  dht.begin();
}
String incomingBluetooth = "";
int thresholdValue = 100;
int onOff = 0;
bool ALARM_ON = false;
long timer1 = millis();
void loop() {
  float h = dht.readHumidity();
  float t = dht.readTemperature();
  if (millis() - timer2 > 5000) {
    timer2 = millis();
    SerialBT.print("!");
    SerialBT.print(t);
    SerialBT.print("C,");
    SerialBT.print(h);
    SerialBT.print("%");
  } 
  if (SerialBT.available()) {
    incomingBluetooth = SerialBT.readString();
    thresholdValue = incomingBluetooth.toInt();
  } 
  if (t > thresholdValue) {
    ALARM_ON = true;
  } else {
    ALARM_ON = false;
    digitalWrite(LEDpin, LOW);
  }
  if (ALARM_ON and millis() - timer1 > 1000) {
    timer1 = millis();
    onOff = onOff > 0 ? 0 : 1;
    onOff > 0 ? digitalWrite(LEDpin, LOW) : digitalWrite(LEDpin, HIGH); 
  }
  delay(20);
}
