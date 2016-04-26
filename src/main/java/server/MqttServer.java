package server;

import constants.MqttParameters;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;
import utils.SslUtil;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MqttServer implements MqttCallback {

    private final MqttAsyncClient mqtt;

    private final MqttConnectOptions options;

    public MqttServer() throws MqttException {

        mqtt = new MqttAsyncClient(
                MqttParameters.MOSQUITTO_URL,
                MqttParameters.CLIENT_ID,
                new MqttDefaultFilePersistence("mqtt-persistence")
        );

        options = new MqttConnectOptions();

        options.setCleanSession(MqttParameters.CLEAN_SESSION);

        options.setKeepAliveInterval(MqttParameters.KEEP_ALIVE_INTERVAL);
        options.setConnectionTimeout(MqttParameters.CONNECTION_TIMEOUT);

        options.setMqttVersion(MqttParameters.MQTT_VERSION);

        options.setSocketFactory(SslUtil.getSocketFactory(
                MqttParameters.CA_CERTIFICATE,
                MqttParameters.CLIENT_CERTIFICATE,
                MqttParameters.CLIENT_KEY,
                MqttParameters.CLIENT_KEY_PASSWORD
        ));

        mqtt.setCallback(this);

        connect();
    }

    private void connect() throws MqttException {
        mqtt.connect(options).waitForCompletion();
        logi("Connected to MQTT Broker");

        mqtt.subscribe(MqttParameters.SUBSCRIBE_TOPIC, MqttParameters.QOS).waitForCompletion();
        logi("Subscribed");
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {

    }

    @Override
    public void connectionLost(Throwable cause) {
        logw("Connection to MQTT Broker lost", cause);

        while (true) {
            try {
                logi("Trying to reconnect...");
                connect();
                logi("Reconnected to MQTT Broker");
                return;
            } catch (MqttException e) {
                logw("Reconnect failed, trying again in 10 seconds...", e);
            }

            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                logs("Interrupted", e);
                e.printStackTrace();
            }
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
    }

    private static final Logger logger = Logger.getLogger(MqttServer.class.getName());

    private static void logi(String msg) {
        logger.info(msg);
    }

    private static void logw(String msg) {
        logger.warning(msg);
    }

    private static void logw(String msg, Throwable throwable) {
        logger.log(Level.WARNING, msg, throwable);
    }

    private static void logs(String msg) {
        logger.severe(msg);
    }

    private static void logs(String msg, Throwable throwable) {
        logger.log(Level.SEVERE, msg, throwable);
    }
}
