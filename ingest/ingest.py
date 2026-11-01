import json
import logging
import os
import re

import paho.mqtt.client as mqtt
import pymysql

log = logging.getLogger("ingest")
TOPIC = re.compile(r"^ecosort/([A-Za-z0-9_-]{1,40})/([1-3])/reading$")
EROARE_BUSINESS = 1644          # SIGNAL SQLSTATE '45000' din procedurile stocate

db = None


def conexiune():
    return pymysql.connect(
        host=os.environ["DB_HOST"], user=os.environ["DB_USER"], password=os.environ["DB_PASSWORD"],
        database=os.environ.get("DB_NAME", "ecosort"), charset="utf8mb4",
        autocommit=True, connect_timeout=5)


def salveaza(cod, canal, greutate, distanta):
    """Apelează procedura; la o eroare de rețea sau MySQL redeschide conexiunea și mai încearcă o dată."""
    global db
    for _ in range(2):
        try:
            if db is None:
                db = conexiune()
            with db.cursor() as c:
                c.callproc("sp_masuratoare_inregistreaza", (cod, canal, greutate, distanta, None))
            return
        except pymysql.MySQLError as e:
            if e.args and e.args[0] == EROARE_BUSINESS:
                log.warning("Respins: %s (%s, canal %s)", e.args[1], cod, canal)
                return
            log.error("Eroare MySQL: %s", e)
            try:
                db.close()
            except Exception:
                pass
            db = None


def la_mesaj(client, userdata, msg):
    potrivire = TOPIC.match(msg.topic)
    if not potrivire:
        return
    try:
        date = json.loads(msg.payload)
        greutate = round(float(date["weight_g"]))
        distanta = date.get("distance_mm")
        distanta = None if distanta is None or int(distanta) < 0 else int(distanta)
    except (ValueError, KeyError, TypeError):
        log.warning("Mesaj invalid pe %s: %r", msg.topic, msg.payload[:100])
        return
    salveaza(potrivire.group(1), int(potrivire.group(2)), greutate, distanta)


def la_conectare(client, userdata, flags, reason_code, properties):
    log.info("MQTT conectat: %s", reason_code)
    client.subscribe("ecosort/+/+/reading", qos=1)    # la reconectare ne abonăm din nou


if __name__ == "__main__":
    logging.basicConfig(level=logging.INFO, format="%(asctime)s %(levelname)s %(message)s")
    client = mqtt.Client(mqtt.CallbackAPIVersion.VERSION2, client_id="ecosort-ingest")
    client.username_pw_set(os.environ["MQTT_USER"], os.environ["MQTT_PASSWORD"])
    client.on_connect = la_conectare
    client.on_message = la_mesaj
    client.reconnect_delay_set(1, 30)
    client.connect(os.environ["MQTT_HOST"], 1883, keepalive=30)
    client.loop_forever()