package server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import constants.XmppParameters;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.ReconnectionManager.ReconnectionPolicy;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.gcm.packet.GcmPacketExtension;
import utils.XmppHelper;

import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Smack implementation of a server for GCM XMPP Cloud Connection Server.
 */
@SuppressWarnings("WeakerAccess")
public class CcsServer {

    private static final Logger logger = Logger.getLogger(CcsServer.class.getSimpleName());

    private XMPPTCPConnection connection;

    private final ServerType type;

    public CcsServer(ServerType type) {
        this.type = type;
    }

    /**
     * Indicates whether the connection is in draining state, which means that it
     * will not accept any new downstream messages.
     */
    protected volatile boolean connectionDraining = false;

    /**
     * Sends a downstream message to GCM.
     *
     * @return true if the message has been successfully sent.
     */
    public boolean sendDownstreamMessage(String jsonRequest)
            throws NotConnectedException, InterruptedException {

        if (!connectionDraining) {
            send(jsonRequest);
            return true;
        }

        logger.info("Dropping downstream message since the connection is draining");
        return false;
    }

    /**
     * Sends a packet with contents provided.
     */
    protected void send(String jsonRequest) throws NotConnectedException, InterruptedException {

        Stanza request = new Message();
        request.addExtension(new GcmPacketExtension(jsonRequest));

        connection.sendStanza(request);
    }

    /**
     * Handles an upstream data message from a device application.
     * <p>
     * <p>This sample echo server sends an echo message back to the device.
     * Subclasses should override this method to properly process upstream messages.
     */
    protected void handleUpstreamMessage(JsonObject json) {
        // PackageName of the application that sent this message.
        String category = json.get("category").getAsString();
        String from = json.get("from").getAsString();
        @SuppressWarnings("unchecked")
        Map<String, String> payload = (Map<String, String>) json.get("data");
        payload.put("ECHO", "Application: " + category);

        // Send an ECHO response back
        String echo = XmppHelper.createJsonMessage(from, XmppHelper.nextMessageId(), payload,
                "echo:CollapseKey", null, false);

        try {
            sendDownstreamMessage(echo);
        } catch (NotConnectedException e) {
            logger.log(Level.WARNING, "Not connected anymore, echo message is not sent", e);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles an ACK.
     * <p>
     * <p>Logs a INFO message, but subclasses could override it to
     * properly handle ACKs.
     */
    protected void handleAckReceipt(JsonObject json) {
        String messageId = json.get("message_id").getAsString();
        String from = json.get("from").getAsString();
        logger.log(Level.INFO, "handleAckReceipt() from: " + from + ",messageId: " + messageId);
    }

    /**
     * Handles a NACK.
     * <p>
     * <p>Logs a INFO message, but subclasses could override it to
     * properly handle NACKs.
     */
    protected void handleNackReceipt(JsonObject json) {
        String messageId = json.get("message_id").getAsString();
        String from = json.get("from").getAsString();
        logger.log(Level.INFO, "handleNackReceipt() from: " + from + ",messageId: " + messageId);
    }

    protected void handleControlMessage(JsonObject json) {
        logger.log(Level.INFO, "handleControlMessage(): " + json);
        String controlType = json.get("control_type").getAsString();
        if ("CONNECTION_DRAINING".equals(controlType)) {
            connectionDraining = true;
        } else {
            logger.log(Level.INFO, "Unrecognized control type: %s. This could happen if new features are " + "added to the CCS protocol.",
                    controlType);
        }
    }

    /**
     * Connects to GCM Cloud Connection Server using the supplied credentials.
     *
     * @param senderId Your GCM project number
     * @param apiKey   API Key of your project
     */
    public void connect(String senderId, String apiKey)
            throws XMPPException, IOException, SmackException, InterruptedException {

        XMPPTCPConnectionConfiguration config =
                XMPPTCPConnectionConfiguration.builder()
                        .setXmppDomain(XmppParameters.getDomain(type))
                        .setHost(XmppParameters.getServer(type))
                        .setPort(XmppParameters.PORT)
                        .setCompressionEnabled(false)
                        .setConnectTimeout(30000)
                        .setSecurityMode(SecurityMode.ifpossible)
                        .setSendPresence(false)
                        .setSocketFactory(SSLSocketFactory.getDefault())
                        .build();

        connection = new XMPPTCPConnection(config);

        ReconnectionManager.getInstanceFor(connection).setReconnectionPolicy(ReconnectionPolicy.RANDOM_INCREASING_DELAY);
        //disable Roster as I don't think this is supported by GCM
        Roster.getInstanceFor(connection).setRosterLoadedAtLogin(false);

        connection.addConnectionListener(connectionStatusLogger);
        // Handle incoming packets
        connection.addAsyncStanzaListener(incomingStanzaListener, stanzaFilter);
        // Log all outgoing packets
        connection.addPacketInterceptor(outgoingStanzaInterceptor, stanzaFilter);

        logger.info("Connecting...");
        connection.connect();
        connection.login(senderId + "@gcm.googleapis.com", apiKey);
    }

    private final StanzaFilter stanzaFilter = new StanzaFilter() {

        @Override
        public boolean accept(Stanza stanza) {

            if (stanza.getClass() == Stanza.class)
                return true;
            else {
                if (stanza.getTo() != null)
                    if (stanza.getTo().toString().startsWith(XmppParameters.PROJECT_ID))
                        return true;
            }

            return false;
        }
    };

    private final StanzaListener incomingStanzaListener = new StanzaListener() {

        @Override
        public void processPacket(Stanza packet) {

            logger.log(Level.INFO, "Received: " + packet.toXML());

            GcmPacketExtension gcmPacketExtension = GcmPacketExtension.from(packet);

            String jsonString = gcmPacketExtension.getJson();

            try {

                JsonObject json = new Gson().fromJson(jsonString, JsonObject.class);

                // present for "ack"/"nack", null otherwise
                String messageType = json.get("message_type").getAsString();

                if (messageType == null) {
                    // Normal upstream data message
                    handleUpstreamMessage(json);

                    // Send ACK to CCS
                    String messageId = json.get("message_id").getAsString();
                    String from = json.get("from").getAsString();
                    String ack = XmppHelper.createJsonAck(from, messageId);
                    send(ack);
                } else if ("ack".equals(messageType)) {
                    // Process Ack
                    handleAckReceipt(json);
                } else if ("nack".equals(messageType)) {
                    // Process Nack
                    handleNackReceipt(json);
                } else if ("control".equals(messageType)) {
                    // Process control message
                    handleControlMessage(json);
                } else {
                    logger.log(Level.WARNING, "Unrecognized message type (%s)", messageType);
                }
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Failed to process packet", e);
            }
        }
    };

    private final StanzaListener outgoingStanzaInterceptor = new StanzaListener() {
        @Override
        public void processPacket(Stanza packet) {
            logger.log(Level.INFO, "Sent: {0}", packet.toXML());
        }
    };

    private static final ConnectionListener connectionStatusLogger = new ConnectionListener() {

        @Override
        public void connected(XMPPConnection xmppConnection) {
            logger.info("Connected");
        }

        @Override
        public void reconnectionSuccessful() {
            logger.info("Reconnected");
        }

        @Override
        public void reconnectionFailed(Exception e) {
            logger.log(Level.WARNING, "Reconnection failed.. ", e);
        }

        @Override
        public void reconnectingIn(int seconds) {
            logger.log(Level.INFO, "Reconnecting in %d secs", seconds);
        }

        @Override
        public void connectionClosedOnError(Exception e) {
            logger.log(Level.SEVERE, "Connection closed on error.. ", e);
        }

        @Override
        public void connectionClosed() {
            logger.info("Connection closed");
        }

        @Override
        public void authenticated(XMPPConnection arg0, boolean arg1) {
            logger.info("authenticated");
        }
    };

    public enum ServerType {
        GCM, LOCAL
    }
}
