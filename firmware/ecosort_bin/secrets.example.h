// secrets.example.h — copiați ca secrets.h (nu se urcă în Git)
#pragma once
#define WIFI_SSID    "numele-retelei"
#define WIFI_PASS    "parola-wifi"          // doar 2,4 GHz
#define MQTT_HOST    "192.168.0.10"         // IP-ul calculatorului server din aceeași rețea
#define MQTT_USER    "esp32"
#define MQTT_PASS    "parola-mqtt-esp"      // ECOSORT_MQTT_ESP_PASSWORD din .env
#define DEVICE_CODE  "BIN-C1-01"
#define CHANNEL      1
#define CAL_FACTOR   97.62f