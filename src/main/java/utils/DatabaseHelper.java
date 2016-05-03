package utils;

import org.lightcouch.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseHelper {

    private static final Map<String, DatabaseHelper> CONNECTIONS = new HashMap<>();

    private CouchDbClient dbClient;

    public static DatabaseHelper getInstanceFor(String dbName) {

        DatabaseHelper instance;

        if (CONNECTIONS.containsKey(dbName)) {
            instance = CONNECTIONS.get(dbName);
        } else {
            instance = new DatabaseHelper(dbName);
            CONNECTIONS.put(dbName, instance);
        }

        return instance;
    }

    private DatabaseHelper(String dbName) {
        dbClient = new CouchDbClient(
                dbName,
                false,
                "http",
                "127.0.0.1",
                5984,
                null,
                null
        );
    }

    public boolean save(Document doc) {
        try {
            Response response = dbClient.save(doc);
            if (response.getError() != null && response.getError().length() > 0) {
                logs("Error saving document: " + response.getReason());
                return false;
            } else {
                doc.setRevision(response.getRev());
                return true;
            }
        } catch (DocumentConflictException e) {
            logw("Conflict while saving document", e);
            return false;
        }
    }

    public boolean update(Document doc) {
        try {
            Response response = dbClient.update(doc);
            if (response.getError() != null && response.getError().length() > 0) {
                logs("Error updating document: " + response.getReason());
                return false;
            } else {
                doc.setRevision(response.getRev());
                return true;
            }
        } catch (DocumentConflictException e) {
            logw("Conflict while updating document", e);
            return false;
        }
    }

    public boolean remove(Document doc) {
        try {
            Response response = dbClient.remove(doc);
            if (response.getError() != null && response.getError().length() > 0) {
                logs("Error removing document: " + response.getReason());
                return false;
            } else {
                return true;
            }
        } catch (NoDocumentException e) {
            logw("Document not found", e);
            return false;
        }
    }

    public List<Document> read() {
        return dbClient.view("_all_docs").includeDocs(true).query(Document.class);
    }

    private static final Logger logger = Logger.getLogger(DatabaseHelper.class.getSimpleName());

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
