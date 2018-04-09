package mongodb;

import static jarvis.actions.CommandBuilder.KEY_COMMAND;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import jarvis.actions.CommandBuilder;
import jarvis.actions.command.definitions.Command;
import jarvis.controllers.ThingParser;
import jarvis.controllers.definitions.Thing;
import jarvis.events.definitions.EventHandler;
import jarvis.events.util.LoggedEventHandler;
import jarvis.util.AdminAlertUtil;
import jarvis.util.JarvisException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.bson.Document;
import org.json.JSONObject;
import res.Config;

public class MongoDB {

  public static boolean isInitialized() {
    return dbExists() && thingsCollectionExists();
  }

  public static boolean dbExists() {
    MongoClient m = null;
    try {
      m = buildClient();
      Iterable<String> dbs = m.listDatabaseNames();
      for (String db : dbs) {
        if (db.equals(Config.MONGO_JARVIS_DB)) {
          return true;
        }
      }
    } catch (Exception e) {
      AdminAlertUtil.alertUnexpectedException(e);
      e.printStackTrace();
    } finally {
      if (m != null) {
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
      for (String col : collections) {
        if (col.equals(Config.MONGO_THINGS_COLLECTION)) {
          return true;
        }
      }
    } catch (Exception e) {
      AdminAlertUtil.alertUnexpectedException(e);
      e.printStackTrace();
    } finally {
      if (m != null) {
        m.close();
      }
    }
    return false;
  }

  private static MongoDatabase getJarvisDatabase(MongoClient client) {
    return client.getDatabase(Config.MONGO_JARVIS_DB);
  }

  private static MongoCollection getCollection(MongoClient client, String collection) {
    MongoDatabase database = getJarvisDatabase(client);
    return database.getCollection(collection);
  }

  private static MongoCollection getThingsCollection(MongoClient client) {
    return getCollection(client, Config.MONGO_THINGS_COLLECTION);
  }

  private static MongoCollection getCommandsCollection(MongoClient client) {
    return getCollection(client, Config.MONGO_COMMANDS_COLLECTION);
  }

  private static MongoCollection getUserCommandsCollection(MongoClient client) {
    return getCollection(client, Config.MONGO_USER_COMMANDS_COLLECTION);
  }

  private static MongoCollection getActiveRulesCollection(MongoClient client) {
    return getCollection(client, Config.MONGO_ACTIVE_RULES_COLLECTION);
  }

  private static MongoCollection getEventHistoryCollection(MongoClient client) {
    return getCollection(client, Config.MONGO_EVENT_HISTORY_COLLECTION);
  }

  public static boolean initialize(List<Thing> defaultThings) {
    MongoClient m = null;
    try {
      m = buildClient();
      MongoDatabase jarvisDb = m.getDatabase(Config.MONGO_JARVIS_DB);
      jarvisDb.createCollection(Config.MONGO_THINGS_COLLECTION);
      MongoCollection col = jarvisDb.getCollection(Config.MONGO_THINGS_COLLECTION);

      for (Thing t : defaultThings) {
        Document doc = Document.parse(t.toString());
        col.insertOne(doc);
      }

      MongoCollection userCommands = jarvisDb.getCollection(Config.MONGO_USER_COMMANDS_COLLECTION);
      userCommands.drop();
    } catch (Exception e) {
      AdminAlertUtil.alertUnexpectedException(e);
      e.printStackTrace();
      return false;
    } finally {
      if (m != null) {
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

  //////////////////////////////////////
  //////////////// THINGS //////////////
  //////////////////////////////////////

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
      if (m != null) {
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
      if (m != null) {
        m.close();
      }
    }
    return things;
  }

  public static List<Thing> getThingsWithNameLike(String name) {
    List<Thing> things = new ArrayList<>();

    MongoClient m = null;
    try {
      m = buildClient();
      MongoCollection col = getThingsCollection(m);

      BasicDBObject query = new BasicDBObject();
      query.put(Thing.NAME_KEY, java.util.regex.Pattern.compile(name));

      FindIterable<Document> documents = col.find(query);
      things = getThingsFromDocuments(documents);
    } catch (Exception e) {
      AdminAlertUtil.alertUnexpectedException(e);
      e.printStackTrace();
    } finally {
      if (m != null) {
        m.close();
      }
    }
    return things;
  }

  private static List<Thing> getThingsFromDocuments(FindIterable<Document> documents) {
    List<Thing> things = new ArrayList<>();
    for (Document doc : documents) {
      Optional<Thing> thing = ThingParser.parseThingFromJson(doc.toJson());
      if (thing.isPresent()) {
        things.add(thing.get());
      }
    }
    return things;
  }

  //////////////////////////////////////
  ////////////// COMMANDS //////////////
  //////////////////////////////////////

  public static Optional<Command> getCommand(long id) {
    Command result = null;

    MongoClient m = null;
    try {
      m = buildClient();
      MongoCollection col = getCommandsCollection(m);
      FindIterable<Document> documents = col.find(Filters.eq("command.id", id));
      List<Command> commands = getCommandsFromDocument(documents);
      if (commands.size() == 1) {
        result = commands.get(0);
      }
    } catch (Exception e) {
      AdminAlertUtil.alertUnexpectedException(e);
      e.printStackTrace();
    } finally {
      if (m != null) {
        m.close();
      }
    }

    if (result == null) {
      return Optional.empty();
    }
    return Optional.of(result);
  }

  private static List<Command> getCommandsFromDocument(FindIterable<Document> documents) {
    List<Command> commands = new ArrayList<>();
    for (Document doc : documents) {
      JSONObject commandInfo = new JSONObject(doc.toJson());
      if (!commandInfo.has(KEY_COMMAND)) {
        continue;
      }
      Command c = CommandBuilder.buildFromJSON(commandInfo.getJSONObject(KEY_COMMAND));
      if (c != null) {
        commands.add(c);
      }
    }
    return commands;
  }

  public static boolean logCommand(JSONObject command) {
    return insertOne(command, Config.MONGO_COMMANDS_COLLECTION);
  }

  //////////////////////////////////////
  ////////////// EVENTS ////////////////
  //////////////////////////////////////

  public static boolean logEventHandled(JSONObject command) {
    return insertOne(command, Config.MONGO_EVENT_HISTORY_COLLECTION);
  }

  public static List<LoggedEventHandler> getLatestNEventsHandled(int n) {
    List<LoggedEventHandler> eventHandlers = new ArrayList<>();

    MongoClient m = null;
    try {
      m = buildClient();
      MongoCollection col = getEventHistoryCollection(m);

      FindIterable<Document> documents = col.find().sort(new Document("timestamp", -1)).limit(n);
      eventHandlers = getEventHandlersFromDocument(documents);
    } catch (Exception e) {
      AdminAlertUtil.alertUnexpectedException(e);
      e.printStackTrace();
    } finally {
      if (m != null) {
        m.close();
      }
    }
    return eventHandlers;
  }

  private static List<LoggedEventHandler> getEventHandlersFromDocument(FindIterable<Document> documents) {
    List<LoggedEventHandler> events = new ArrayList<>();
    for (Document doc : documents) {
      JSONObject eventHandlerInfo = new JSONObject(doc.toJson());
      try {
        events.add(new LoggedEventHandler(eventHandlerInfo));
      } catch (JarvisException e1) {
        e1.printStackTrace();
      }
    }
    return events;
  }

  //////////////////////////////////////
  //////////// USER COMMANDS ///////////
  //////////////////////////////////////

  public static boolean logUserCommand(JSONObject command) {
    return insertOne(command, Config.MONGO_USER_COMMANDS_COLLECTION);
  }

  public static List<Command> getLatestNUserCommands(int n) {
    List<Command> commands = new ArrayList<>();

    MongoClient m = null;
    try {
      m = buildClient();
      MongoCollection col = getUserCommandsCollection(m);

      FindIterable<Document> documents = col.find().sort(new Document("timestamp", -1)).limit(n);
      commands = getCommandsFromDocument(documents);
    } catch (Exception e) {
      AdminAlertUtil.alertUnexpectedException(e);
      e.printStackTrace();
    } finally {
      if (m != null) {
        m.close();
      }
    }
    return commands;
  }

  public static Optional<Command> getUserCommand(long id) {
    Command result = null;

    MongoClient m = null;
    try {
      m = buildClient();
      MongoCollection col = getUserCommandsCollection(m);
      FindIterable<Document> documents = col.find(Filters.eq("command.id", id));
      List<Command> commands = getCommandsFromDocument(documents);
      if (commands.size() == 1) {
        result = commands.get(0);
      }
    } catch (Exception e) {
      AdminAlertUtil.alertUnexpectedException(e);
      e.printStackTrace();
    } finally {
      if (m != null) {
        m.close();
      }
    }

    if (result == null) {
      return Optional.empty();
    }
    return Optional.of(result);
  }

  //////////////////////////////////////
  //////////// ACTIVE RULES ////////////
  //////////////////////////////////////

  public static boolean logActiveRule(JSONObject rule) {
    return insertOne(rule, Config.MONGO_ACTIVE_RULES_COLLECTION);
  }

  public static boolean deleteActiveRule(long id) {
    MongoClient m = null;
    try {
      m = buildClient();
      MongoCollection col = getActiveRulesCollection(m);

      BasicDBObject document = new BasicDBObject();
      document.put("command.id", id);
      DeleteResult res = col.deleteOne(document);
      return res.getDeletedCount() == 1;
    } catch (Exception e) {
      AdminAlertUtil.alertUnexpectedException(e);
      e.printStackTrace();
    } finally {
      if (m != null) {
        m.close();
      }
    }
    return false;
  }

  public static boolean deleteActiveRules() {
    MongoClient m = null;
    try {
      m = buildClient();
      MongoCollection col = getActiveRulesCollection(m);
      col.drop();
      return true;
    } catch (Exception e) {
      AdminAlertUtil.alertUnexpectedException(e);
      e.printStackTrace();
    } finally {
      if (m != null) {
        m.close();
      }
    }
    return false;
  }

  //////////////////////////////////////
  //////////////// OTHERS //////////////
  //////////////////////////////////////

  private static boolean insertOne(JSONObject obj, String colName) {
    MongoClient m = null;
    try {
      m = buildClient();
      MongoCollection col = getCollection(m, colName);
      col.insertOne(Document.parse(obj.toString()));
    } catch (Exception e) {
      AdminAlertUtil.alertUnexpectedException(e);
      e.printStackTrace();
      return false;
    } finally {
      if (m != null) {
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

    return new MongoClient(new ServerAddress(Config.JARVIS_DOMAIN, Config.MONGO_PORT), creds,
        options);
  }

  private static List<MongoCredential> getCredentials() {
    MongoCredentials storedCredentials = new MongoCredentials(Config.MONGO_USER,
        Config.MONGO_AUTH_DB, Config.MONGO_PASSWORD);

    List<MongoCredential> creds = new ArrayList<>();
    creds.add(MongoCredential
        .createCredential(storedCredentials.getUser(), storedCredentials.getDatabase(),
            storedCredentials.getPassword()));
    return creds;
  }
}
