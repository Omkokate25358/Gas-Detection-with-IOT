#include <ESP8266WiFi.h>
#include <Servo.h> // Servo library

#include <FirebaseESP8266.h>

// WiFi Credentials
#define WIFI_SSID "Localhost"
#define WIFI_PASSWORD "127.0.0.1"
#define FIREBASE_HOST "gas-detection-b2ad3-default-rtdb.firebaseio.com"
#define FIREBASE_AUTH "B4CDZvc1xZEnmd6yMBZucdNmHtTwYK4qw81HSi7d"

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

void initializeFirebase() {
    firebaseConfig.host = FIREBASE_HOST;
    firebaseConfig.signer.tokens.legacy_token = FIREBASE_AUTH;
    Firebase.begin(&firebaseConfig, &firebaseAuth);
    Firebase.reconnectWiFi(true);
    Serial.println("Firebase Initialized!");
}


void setup() {
    Serial.begin(9600);
    connectWiFi();
    initializeFirebase();

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
    int gasValue = analogRead(MQ2_SENSOR_PIN); // Read MQ-2 sensor value
    Serial.print("Gas Value: ");
    Serial.println(gasValue);

    if (Firebase.setInt(firebaseData, "/sensor/randomValue", gasValue)) {
        Serial.println("Data uploaded successfully!");
    } else {
        Serial.print("Firebase Error: ");
        Serial.println(firebaseData.errorReason());
    }

    if (gasValue > threshold) {
        if (!gasDetected) {
            gasDetected = true;
            gasStartTime = millis();  // Record the start time
        }

        // If gas persists for 5 seconds
        if (millis() - gasStartTime >= 5000) {
            servo.write(90);         // Rotate servo to 90 degrees
        }

        // Gas detected - Alert system
        digitalWrite(RED_LED_PIN, HIGH);   // Red LED ON
        digitalWrite(GREEN_LED_PIN, LOW);  // Green LED OFF
        digitalWrite(BUZZER_PIN, HIGH);    // Buzzer ON
    } else {
        gasDetected = false;
        servo.write(0);                   // Reset servo to 0 degrees
        digitalWrite(RED_LED_PIN, LOW);   // Red LED OFF
        digitalWrite(GREEN_LED_PIN, HIGH); // Green LED ON
        digitalWrite(BUZZER_PIN, LOW);    // Buzzer OFF
    }

    delay(2000);  // Delay for stability
}