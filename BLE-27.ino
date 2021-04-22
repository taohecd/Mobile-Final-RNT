#include <SimpleDHT.h>
#include <BluetoothSerial.h>
BluetoothSerial BT;
int pinDHT11 = 4; 
int pinLed =32;

float alarmTemp = 20.0;

SimpleDHT11 dht11(pinDHT11);



// menuconfig
#if !defined(CONFIG_BT_ENABLED) || !defined(CONFIG_BLUEDROID_ENABLED)
#error Bluetooth is not enabled! Please run 'make menuconfig' to and enable it
#endif

void setup() {
  Serial.begin(115200);
  pinMode(pinLed, OUTPUT);
  BT.begin("ESP32-BNT");
}

void loop() {
  // start working...
  Serial.println("=================================");
  Serial.println("ESP32 DHT11...");
  // read without samples.
  byte temperature = 0;
  byte humidity = 0;
  int err = SimpleDHTErrSuccess;
  if ((err = dht11.read(&temperature, &humidity, NULL)) != SimpleDHTErrSuccess) {
    Serial.print("Read DHT11 failed, err="); Serial.println(err); delay(1000);
    return;
  }
  //Transmit the temperature and humidity to the Bluetooth device
  BT.print((float)temperature);
  BT.print(" *C,");
  BT.print("!");
  
  BT.print((float)humidity);
  BT.println(" H");
  BT.print("!");

  Serial.print("Temperature: ");
  Serial.print((float)temperature);
  Serial.println(" *C,");

  Serial.print("Humidity: ");
  Serial.print((float)humidity);
  Serial.println(" %");

  if (temperature > alarmTemp){
    digitalWrite(pinLed, HIGH);
    delay(500);
    digitalWrite(pinLed, LOW);
  }
  
  delay(1000); 
}
