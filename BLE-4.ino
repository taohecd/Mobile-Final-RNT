#include <SimpleDHT.h>
#include <BluetoothSerial.h>
BluetoothSerial BT;
const int pinDHT11 = 4; 
const int Led = 23;

String myRecv = "";

SimpleDHT11 dht11(pinDHT11);

// menuconfig
#if !defined(CONFIG_BT_ENABLED) || !defined(CONFIG_BLUEDROID_ENABLED)
#error Bluetooth is not enabled! Please run 'make menuconfig' to and enable it
#endif

void setup() {
  Serial.begin(115200);

  pinMode(Led, OUTPUT);
  
  BT.begin("ESP32-Final");
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
  BT.print((int)temperature);
  BT.print(" *C,");
  BT.print((int)humidity);
  BT.println(" H");
  delay(1000); 

  //Recieve the information when it is sending
  while (BT.available()){
    
    char myString = BT.read();
    myRecv = myRecv + myString;
    delay(1);
  }

  //Switch the leds by sending various messages
  if (myRecv == "on"){
    digitalWrite(Led, HIGH);
  }

  if (myRecv == "off"){
    digitalWrite(Led, LOW);
  }

  //Clear the myString
  myRecv = "";
  
}
