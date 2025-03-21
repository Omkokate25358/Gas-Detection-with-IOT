#include <ESP8266WiFi.h>
#include <Servo.h> // Servo library

#include <FirebaseESP8266.h>

// WiFi Credentials
#define WIFI_SSID "YOUR_WIFI_NAME"
#define WIFI_PASSWORD "YOUR_WIFI_PASSWORD"
#define FIREBASE_HOST "YOUR_FIREBASE_PATH
#define FIREBASE_AUTH "FIREBASE_SECRET_KEY

// Firebase & WiFi objects
FirebaseData firebaseData;
FirebaseConfig firebaseConfig;
FirebaseAuth firebaseAuth;

// Pin definitions
#define MQ2_SENSOR_PIN A0   // MQ-2 sensor analog pin
#define RED_LED_PIN D2      // Red LED pin
#define GREEN_LED_PIN D3    // Green LED pin
#define BUZZER_PIN D4       // Buzzer pin
#define SERVO_PIN D5        // Servo control pin

int threshold = 300;        // Gas detection threshold
unsigned long gasStartTime = 0; // Track when gas is first detected
bool gasDetected = false;

Servo servo;  // Servo object

void connectWiFi() {
    Serial.print("Connecting to WiFi: ");
    Serial.println(WIFI_SSID);

    WiFi.begin(WIFI_SSID, WIFI_PASSWORD);

    int retries = 0;
    while (WiFi.status() != WL_CONNECTED && retries < 30) {
        delay(500);
        Serial.print(".");
        retries++;
    }

    if (WiFi.status() != WL_CONNECTED) {
        Serial.println("\nFailed to connect to WiFi. Restarting...");
        ESP.restart();
    }

    Serial.println("\nWiFi Connected! IP Address: " + WiFi.localIP().toString());
}

void setup() {
    Serial.begin(9600);
    //connectWiFi();
   // initializeFirebase();

    pinMode(MQ2_SENSOR_PIN, INPUT);
    pinMode(RED_LED_PIN, OUTPUT);
    pinMode(GREEN_LED_PIN, OUTPUT);
    pinMode(BUZZER_PIN, OUTPUT);

    digitalWrite(RED_LED_PIN, LOW);
    digitalWrite(GREEN_LED_PIN, HIGH);
    digitalWrite(BUZZER_PIN, LOW);

    servo.attach(SERVO_PIN);
    servo.write(0); // Initial servo position at 0 degrees
}




void loop() {
    

    delay(2000);  // Delay for stability
}