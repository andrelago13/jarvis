package mongodb;

import com.mongodb.*;
import com.mongodb.client.MongoDatabase;
import res.Config;

import java.util.ArrayList;
import java.util.List;

public class MongoDB {
    public static final int MONGO_PORT = 27017;

    private static String test() {
        String result = "1";
        try {
            MongoClient m = buildClient();
            result = "7";
            try {
                MongoDatabase db = m.getDatabase("test");
                result = "8";
                //db.createCollection("testcol");
                db.listCollectionNames().first();
                result = "9";
                m.close();
                //MongoCollection<Document> col = db.getCollection("testcol");
                result = "10";
                //col.insertOne(new Document().append("k", "v"));
                return db.getName();
                //return m.getAddress().toString();
            } catch (Exception e) {
                throw e;
                //return e.getStackTrace().toString();
            }
        } catch (Exception e) {
            result += " " + e.getMessage();
        }
        return result;
    }

    public static boolean hasConnection() {
        try {
            MongoClient client = buildClient();
            MongoDatabase db = client.getDatabase("jarvis");
            db.listCollectionNames().first();
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    private static MongoClient buildClient() {
        List<MongoCredential> creds = getCredentials();

        MongoClientOptions.Builder optionsBuilder = MongoClientOptions.builder();
        optionsBuilder.connectTimeout(Config.MONGO_TIMEOUT_MS);
        optionsBuilder.socketTimeout(Config.MONGO_TIMEOUT_MS);
        optionsBuilder.serverSelectionTimeout(Config.MONGO_TIMEOUT_MS);
        MongoClientOptions options = optionsBuilder.build();

        return new MongoClient(new ServerAddress(Config.JARVIS_DOMAIN , MONGO_PORT), creds, options);
    }

    private static List<MongoCredential> getCredentials() {
        MongoCredentials storedCredentials = new MongoCredentials(Config.MONGO_USER,
                Config.MONGO_DATABASE, Config.MONGO_PASSWORD);

        List<MongoCredential> creds = new ArrayList<>();
        creds.add(MongoCredential.createCredential(storedCredentials.getUser(), storedCredentials.getDatabase(),
                storedCredentials.getPassword()));
        return creds;
    }
}
