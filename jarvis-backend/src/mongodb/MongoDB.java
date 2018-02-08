package mongodb;

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import org.bson.Document;
import util.Config;

import java.util.ArrayList;
import java.util.List;

public class MongoDB {
    public static String test() {
        List<MongoCredential> creds = new ArrayList<MongoCredential>();
        //creds.add(MongoCredential.createCredential("user", "test", "pass1231".toCharArray()));

        MongoClientOptions.Builder optionsBuilder = MongoClientOptions.builder();

        optionsBuilder.connectTimeout(5000);
        optionsBuilder.socketTimeout(5000);
        optionsBuilder.serverSelectionTimeout(5000);

        MongoClientOptions options = optionsBuilder.build();

        MongoClient m = new MongoClient(new ServerAddress(Config.JARVIS_DOMAIN , 27017), creds, options);

        try {
            MongoDatabase db = m.getDatabase("test");
            db.createCollection("testcol");
            MongoCollection<Document> col = db.getCollection("testcol");
            col.insertOne(new Document().append("k", "v"));
            return db.getName();
            //return m.getAddress().toString();
        } catch (Exception e) {
            throw e;
            //return e.getStackTrace().toString();
        }

        //return "";
    }
}
