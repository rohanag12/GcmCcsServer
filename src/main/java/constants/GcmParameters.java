package constants;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

public final class GcmParameters {

    public static final String GCM_SERVER = "gcm.googleapis.com";

    public static final int GCM_PORT = 5235;

    public static final String PROJECT_ID;
    public static final String API_KEY;

    public static final String PHONE_REG_ID;

    static {

        Properties props = new Properties();

        try {
            props.load(new InputStreamReader(new FileInputStream("keys.conf")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        PROJECT_ID = props.getProperty("project_number", null);
        API_KEY = props.getProperty("api_key", null);
        PHONE_REG_ID = props.getProperty("reg_id", null);
    }
}
