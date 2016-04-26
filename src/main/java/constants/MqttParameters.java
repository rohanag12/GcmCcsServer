package constants;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

@SuppressWarnings("SpellCheckingInspection")
public class MqttParameters {

    public static final boolean CLEAN_SESSION = true;

    public static final int CONNECTION_TIMEOUT = 30;
    public static final int KEEP_ALIVE_INTERVAL = 60;

    public static final int MQTT_VERSION = MqttConnectOptions.MQTT_VERSION_3_1_1;

    public static final String MOSQUITTO_URL = "tcp://127.0.0.1:1883";

    public static final String CLIENT_ID = "remote-control-server";

    public static final String SUBSCRIBE_TOPIC = "remote/control";

    public static final String PUBLISH_TOPIC = "";
    public static final boolean RETAINED = false;

    public static final int QOS = 1;

    // ToDo
    public static final String CA_CERTIFICATE = "";
    public static final String CLIENT_CERTIFICATE = "";
    public static final String CLIENT_KEY = "";
    public static final String CLIENT_KEY_PASSWORD = "";
}
