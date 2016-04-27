package devices;

import com.google.gson.annotations.Expose;

import java.util.logging.Logger;

class Fan extends Device {

    private final int max_speed;

    @Expose
    private int speed = 0;

    Fan(String name, int max_speed) {
        super(name);
        this.max_speed = max_speed;
    }

    void increaseSpeed() {
        if (speed < max_speed) {
            speed++;
        }

        if (speed == max_speed) {
            logi("Speed is maximum");
            // ToDo: Send a ping to inform the app that speed is maximum, no more increasing it
        }
    }

    void decreaseSpeed() {
        if (speed > 0) {
            speed--;
        }

        if (speed == 0) {
            logi("Speed is minimum");
            // ToDo: Send a ping to inform the app that speed is minimum, no more decreasing it
        }
    }

    @Override
    public String toString() {
        return gson.toJson(this);
    }

    private static final Logger logger = Logger.getLogger(Fan.class.getName());

    private static void logi(String msg) {
        logger.info(msg);
    }

    private static void logw(String msg) {
        logger.warning(msg);
    }

    private static void logs(String msg) {
        logger.severe(msg);
    }
}
