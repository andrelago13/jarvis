package mongodb;

import com.mongodb.*;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.util.JSON;
import jarvis.actions.CommandBuilder;
import jarvis.actions.command.definitions.Command;
import jarvis.controllers.OnOffLight;
import jarvis.controllers.ThingParser;
import jarvis.controllers.definitions.Thing;
import jarvis.util.AdminAlertUtil;
import org.bson.Document;
import org.json.JSONObject;
import res.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static jarvis.actions.CommandBuilder.KEY_COMMAND;

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

    public static boolean isInitialized() {
        return dbExists() && thingsCollectionExists();
    }

    public static boolean dbExists() {
        MongoClient m = null;
        try {
            m = buildClient();
            Iterable<String> dbs = m.listDatabaseNames();
            for(String db : dbs) {
                if(db.equals(Config.MONGO_JARVIS_DB)) {
                    return true;
                }
            }
        } catch (Exception e) {
            AdminAlertUtil.alertUnexpectedException(e);
            e.printStackTrace();
        } finally {
            if(m != null) {
                m.close();
            }
        }
        return false;
    }

    public static boolean thingsCollectionExists() {
        MongoClient m = null;
        try {
            m = buildClient();
            MongoDatabase jarvisDb = m.getDatabase(Config.MONGO_JARVIS_DB);
            Iterable<String> collections = jarvisDb.listCollectionNames();
            for(String col : collections) {
                if(col.equals(Config.MONGO_THINGS_COLLECTION)) {
                    return true;
                }
            }
        } catch (Exception e) {
            AdminAlertUtil.alertUnexpectedException(e);
            e.printStackTrace();
        } finally {
            if(m != null) {
                m.close();
            }
        }
        return false;
    }

    private static MongoDatabase getJarvisDatabase(MongoClient client) {
        return client.getDatabase(Config.MONGO_JARVIS_DB);
    }

    private static MongoCollection getThingsCollection(MongoClient client) {
        MongoDatabase database = getJarvisDatabase(client);
        return database.getCollection(Config.MONGO_THINGS_COLLECTION);
    }

    private static MongoCollection getCommandsCollection(MongoClient client) {
        MongoDatabase database = getJarvisDatabase(client);
        return database.getCollection(Config.MONGO_COMMANDS_COLLECTION);
    }

    private static MongoCollection getRulesCollection(MongoClient client) {
        MongoDatabase database = getJarvisDatabase(client);
        return database.getCollection(Config.MONGO_RULES_COLLECTION);
    }

    public static boolean initialize(List<Thing> defaultThings) {
        MongoClient m = null;
        try {
            m = buildClient();
            MongoDatabase jarvisDb = m.getDatabase(Config.MONGO_JARVIS_DB);
            jarvisDb.createCollection(Config.MONGO_THINGS_COLLECTION);
            MongoCollection col = jarvisDb.getCollection(Config.MONGO_THINGS_COLLECTION);

            for(Thing t : defaultThings) {
                Document doc = Document.parse(t.toString());
                col.insertOne(doc);
            }
        } catch (Exception e) {
            AdminAlertUtil.alertUnexpectedException(e);
            e.printStackTrace();
            return false;
        } finally {
            if(m != null) {
                m.close();
            }
        }
        return true;
    }

    public static boolean hasConnection() {
        try {
            MongoClient client = buildClient();
            MongoDatabase db = getJarvisDatabase(client);
            db.listCollectionNames().first();
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public static List<Thing> getThings() {
        List<Thing> things = new ArrayList<>();

        MongoClient m = null;
        try {
            m = buildClient();
            MongoCollection col = getThingsCollection(m);

            FindIterable<Document> documents = col.find();
            things = getThingsFromDocuments(documents);
        } catch (Exception e) {
            AdminAlertUtil.alertUnexpectedException(e);
            e.printStackTrace();
        } finally {
            if(m != null) {
                m.close();
            }
        }
        return things;
    }

    public static List<Thing> getThingsByName(String name) {
        List<Thing> things = new ArrayList<>();

        MongoClient m = null;
        try {
            m = buildClient();
            MongoCollection col = getThingsCollection(m);

            BasicDBObject whereQuery = new BasicDBObject();
            whereQuery.put(Thing.NAME_KEY, name);

            FindIterable<Document> documents = col.find(whereQuery);
            things = getThingsFromDocuments(documents);
        } catch (Exception e) {
            AdminAlertUtil.alertUnexpectedException(e);
            e.printStackTrace();
        } finally {
            if(m != null) {
                m.close();
            }
        }
        return things;
    }

    public static List<Command> getLatestNCommands(int n) {
        List<Command> commands = new ArrayList<>();

        MongoClient m = null;
        try {
            m = buildClient();
            MongoCollection col = getCommandsCollection(m);

            FindIterable<Document> documents = col.find().sort(new Document("timestamp", -1)).limit(n);
            commands = getCommandsFromDocument(documents);
        } catch (Exception e) {
            AdminAlertUtil.alertUnexpectedException(e);
            e.printStackTrace();
        } finally {
            if(m != null) {
                m.close();
            }
        }
        return commands;
    }

    public static List<Command> getRules() {
        List<Command> commands = new ArrayList<>();

        MongoClient m = null;
        try {
            m = buildClient();
            MongoCollection col = getRulesCollection(m);

            FindIterable<Document> documents = col.find().sort(new Document("timestamp", -1));
            commands = getCommandsFromDocument(documents);
        } catch (Exception e) {
            AdminAlertUtil.alertUnexpectedException(e);
            e.printStackTrace();
        } finally {
            if(m != null) {
                m.close();
            }
        }
        return commands;
    }

    public static List<Thing> getThingsWithNameLike(String name) {
        List<Thing> things = new ArrayList<>();

        MongoClient m = null;
        try {
            m = buildClient();
            MongoCollection col = getThingsCollection(m);

            BasicDBObject query = new BasicDBObject();
            query.put(Thing.NAME_KEY,  java.util.regex.Pattern.compile(name));

            FindIterable<Document> documents = col.find(query);
            things = getThingsFromDocuments(documents);
        } catch (Exception e) {
            AdminAlertUtil.alertUnexpectedException(e);
            e.printStackTrace();
        } finally {
            if(m != null) {
                m.close();
            }
        }
        return things;
    }

    private static List<Thing> getThingsFromDocuments(FindIterable<Document> documents) {
        List<Thing> things = new ArrayList<>();
        for(Document doc : documents) {
            Optional<Thing> thing = ThingParser.parseThingFromJson(doc.toJson());
            if(thing.isPresent()) {
                things.add(thing.get());
            }
        }
        return things;
    }

    private static List<Command> getCommandsFromDocument(FindIterable<Document> documents) {
        List<Command> commands = new ArrayList<>();
        for(Document doc : documents) {
            JSONObject commandInfo = new JSONObject(doc.toJson());
            if(!commandInfo.has(KEY_COMMAND)) {
                continue;
            }
            Command c = CommandBuilder.buildFromJSON(commandInfo.getJSONObject(KEY_COMMAND));
            if(c != null) {
                commands.add(c);
            }
        }
        return commands;
    }

    public static boolean logCommand(JSONObject command) {
        MongoClient m = null;
        try {
            m = buildClient();
            MongoCollection col = getCommandsCollection(m);
            col.insertOne(Document.parse(command.toString()));
        } catch (Exception e) {
            AdminAlertUtil.alertUnexpectedException(e);
            e.printStackTrace();
            return false;
        } finally {
            if(m != null) {
                m.close();
            }
        }
        return true;
    }

    public static boolean logRule(JSONObject rule) {
        MongoClient m = null;
        try {
            m = buildClient();
            MongoCollection col = getRulesCollection(m);
            col.insertOne(Document.parse(rule.toString()));
        } catch (Exception e) {
            AdminAlertUtil.alertUnexpectedException(e);
            e.printStackTrace();
            return false;
        } finally {
            if(m != null) {
                m.close();
            }
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
                Config.MONGO_AUTH_DB, Config.MONGO_PASSWORD);

        List<MongoCredential> creds = new ArrayList<>();
        creds.add(MongoCredential.createCredential(storedCredentials.getUser(), storedCredentials.getDatabase(),
                storedCredentials.getPassword()));
        return creds;
    }
}
