#include <Wire.h>
#include <WiFi.h>
#include <PubSubClient.h>
#include <Preferences.h>
#include <Adafruit_GFX.h>
#include <Adafruit_SSD1306.h>
#include <HX711_ADC.h>
#include "Adafruit_VL53L1X.h"
#include "secrets.h"   // WIFI_*, MQTT_*, DEVICE_CODE, CHANNEL, CAL_FACTOR

#define SDA_PIN 8
#define SCL_PIN 9
#define OLED_ADDR 0x3C

const int HX711_dout = 4;
const int HX711_sck  = 5;

Adafruit_SSD1306 display(128, 64, &Wire, -1);
HX711_ADC LoadCell(HX711_dout, HX711_sck);
Adafruit_VL53L1X vl53 = Adafruit_VL53L1X();
Preferences prefs;

WiFiClient wifi;
PubSubClient mqtt(wifi);
String topicReading, topicStatus;
uint32_t seq = 0;
const unsigned long INTERVAL_PUBLICARE_MS = 5000;
unsigned long tPublicare = 0, tMqtt = 0, tWifi = 0;

// --- filtrare: valori stabile pe o fereastra de 3 s (12 x 250 ms) ---
const int N = 12;
const float PRAG_W_G = 20;                       // variatie maxima admisa a greutatii in fereastra
const int PRAG_D_MM = 25;                        // variatie maxima admisa a distantei
const unsigned long MAX_FARA_STABIL_MS = 120000; // peste 2 min fara valori stabile: nu mai publica
float bufW[N]; int bufD[N];
int idx = 0, nCitiri = 0;
bool stabil = false;
float wStabil = 0; int dStabil = -1;
unsigned long tUltimaStabila = 0;                // 0 = inca nicio valoare stabila
bool tareInCurs = false;

float greutate = 0;
int distanta = -1;
unsigned long tAfisare = 0;

void mesaj(const char* text) {
  display.clearDisplay();
  display.setTextSize(1);
  display.setCursor(0, 0);
  display.println(text);
  display.display();
}

void actualizeazaStabilitate(float g, int d) {
  bufW[idx] = g; bufD[idx] = d;
  idx = (idx + 1) % N;
  if (nCitiri < N) nCitiri++;
  if (nCitiri < N) { stabil = false; return; }

  float wMin = bufW[0], wMax = bufW[0], suma = 0;
  int dMin = 32767, dMax = -32768, valide = 0;
  long sumaD = 0;
  for (int i = 0; i < N; i++) {
    if (bufW[i] < wMin) wMin = bufW[i];
    if (bufW[i] > wMax) wMax = bufW[i];
    suma += bufW[i];
    if (bufD[i] >= 0) {
      valide++; sumaD += bufD[i];
      if (bufD[i] < dMin) dMin = bufD[i];
      if (bufD[i] > dMax) dMax = bufD[i];
    }
  }
  stabil = (wMax - wMin <= PRAG_W_G) && valide >= N / 2 && (dMax - dMin <= PRAG_D_MM);
  if (stabil) {
    wStabil = suma / N;
    dStabil = (int)(sumaD / valide);
    tUltimaStabila = millis();
  }
}

void mentineConexiuni() {
  if (WiFi.status() != WL_CONNECTED) {
    if (millis() - tWifi >= 10000) { tWifi = millis(); WiFi.reconnect(); }
    return;
  }
  if (!mqtt.connected() && millis() - tMqtt >= 5000) {
    tMqtt = millis();
    if (mqtt.connect(DEVICE_CODE, MQTT_USER, MQTT_PASS, topicStatus.c_str(), 1, true, "offline")) {
      mqtt.publish(topicStatus.c_str(), "online", true);
      Serial.println("MQTT conectat");
    } else {
      Serial.printf("MQTT esuat, state=%d\n", mqtt.state());
    }
  }
  mqtt.loop();
}

void publicaMasuratoare(float greutateG, int distantaMm) {
  if (!mqtt.connected()) return;
  if (greutateG < 0) greutateG = 0;
  char json[96];
  snprintf(json, sizeof(json), "{\"weight_g\":%.0f,\"distance_mm\":%d,\"seq\":%lu}",
           greutateG, distantaMm, (unsigned long)seq++);
  mqtt.publish(topicReading.c_str(), json);
}

void setup() {
  Serial.begin(115200);
  delay(1500);
  Wire.begin(SDA_PIN, SCL_PIN);

  if (!display.begin(SSD1306_SWITCHCAPVCC, OLED_ADDR, true, false)) {
    Serial.println("Ecran negasit");
    while (1) delay(10);
  }
  display.setTextColor(SSD1306_WHITE);
  mesaj("Pornire...");

  if (!vl53.begin(0x29, &Wire) || !vl53.startRanging()) {
    mesaj("Eroare VL53L1X");
    while (1) delay(10);
  }
  vl53.setTimingBudget(50);

  // Tara: daca exista una salvata, o refolosim; doar la prima rulare se face tara.
  prefs.begin("ecosort", false);
  bool areTara = prefs.isKey("tara");
  LoadCell.begin();
  LoadCell.setReverseOutput();
  LoadCell.start(2000, !areTara);
  if (LoadCell.getTareTimeoutFlag() || LoadCell.getSignalTimeoutFlag()) {
    mesaj("Eroare HX711");
    while (1) delay(10);
  }
  if (areTara) LoadCell.setTareOffset(prefs.getLong("tara", 0));
  else prefs.putLong("tara", LoadCell.getTareOffset());
  LoadCell.setCalFactor(CAL_FACTOR);

  topicReading = String("ecosort/") + DEVICE_CODE + "/" + CHANNEL + "/reading";
  topicStatus  = String("ecosort/") + DEVICE_CODE + "/status";
  WiFi.mode(WIFI_STA);
  WiFi.begin(WIFI_SSID, WIFI_PASS);
  mqtt.setServer(MQTT_HOST, 1883);
  mqtt.setKeepAlive(30);
  mqtt.setSocketTimeout(2);

  Wire.setClock(400000);
  mesaj("Gata. 't' = tara");
}

void loop() {
  LoadCell.update();

  // Comanda 't' din Serial Monitor: tara noua (capac inchis, cos gol), salvata in memorie.
  if (Serial.available() && Serial.read() == 't') {
    LoadCell.tareNoDelay();
    tareInCurs = true;
    Serial.println("Tara in curs...");
  }
  if (tareInCurs && LoadCell.getTareStatus()) {
    tareInCurs = false;
    prefs.putLong("tara", LoadCell.getTareOffset());
    nCitiri = 0; tUltimaStabila = 0;
    Serial.println("Tara salvata");
  }

  if (vl53.dataReady()) {
    distanta = vl53.distance();
    vl53.clearInterrupt();
  }

  mentineConexiuni();

  if (millis() - tAfisare >= 250) {
    tAfisare = millis();
    float g = LoadCell.getData();
    if (fabs(g) < 1.0) g = 0;
    greutate = g;
    actualizeazaStabilitate(greutate, distanta);

    display.clearDisplay();
    display.setTextSize(1);
    display.setCursor(0, 0);
    display.println("Greutate");
    display.setCursor(56, 0);
    display.print(stabil ? " " : "~");          // ~ = valori in miscare, nu se trimit
    display.setCursor(92, 0);
    display.print(mqtt.connected() ? "MQTT" : (WiFi.status() == WL_CONNECTED ? "WiFi" : "----"));
    display.setTextSize(2);
    display.setCursor(0, 12);
    display.printf("%.0f g", greutate);

    display.setTextSize(1);
    display.setCursor(0, 36);
    display.println("Distanta");
    display.setTextSize(2);
    display.setCursor(0, 48);
    if (distanta < 0) display.print("---");
    else display.printf("%d mm", distanta);
    display.display();

    Serial.printf("g=%.1f  d=%d mm  %s\n", greutate, distanta, stabil ? "stabil" : "~");
  }

  if (millis() - tPublicare >= INTERVAL_PUBLICARE_MS) {
    tPublicare = millis();
    if (tUltimaStabila != 0 && millis() - tUltimaStabila < MAX_FARA_STABIL_MS) {
      publicaMasuratoare(wStabil, dStabil);
    }
  }
}