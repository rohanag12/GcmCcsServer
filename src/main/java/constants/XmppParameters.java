package constants;

import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;
import server.CcsServer.ServerType;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

public final class XmppParameters {

    public static final String GCM_SERVER = "gcm.googleapis.com";
    public static final String LOCAL_SERVER = "127.0.0.1";

    public static final int PORT = 5235;

    public static final String CATEGORY;

    public static final String PROJECT_ID;
    public static final String API_KEY;

    public static final String PHONE_REG_ID;

    public static String getServer(ServerType type) {
        switch (type) {
            case GCM:
                return GCM_SERVER;
            case LOCAL:
                return LOCAL_SERVER;
            default:
                return null;
        }
    }

    public static DomainBareJid getDomain(ServerType type) throws XmppStringprepException {
        switch (type) {
            case GCM:
                return JidCreate.domainBareFrom(GCM_SERVER);
            case LOCAL:
                return JidCreate.domainBareFrom(LOCAL_SERVER);
            default:
                return null;
        }
    }

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
        CATEGORY = props.getProperty("category", null);
    }
}
