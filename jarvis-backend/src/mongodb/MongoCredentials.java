package mongodb;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class MongoCredentials {
    private String mUser;
    private String mDatabase;
    private char[] mPassword;

    public MongoCredentials(String user, String database, String password) {
        mUser = user;
        mDatabase = database;
        mPassword = password.toCharArray();
    }

    public MongoCredentials(String filePath) throws FileNotFoundException {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String user = br.readLine();
            if(user == null) {
                throw new FileNotFoundException("Mongo credentials file is invalid (see example).");
            }
            String database = br.readLine();
            if(database == null) {
                throw new FileNotFoundException("Mongo credentials file is invalid (see example).");
            }
            String password = br.readLine();
            if(password == null) {
                throw new FileNotFoundException("Mongo credentials file is invalid (see example).");
            }

            mUser = user;
            mDatabase = database;
            mPassword = password.toCharArray();
        } catch (IOException e) {
            throw new FileNotFoundException("Mongo credentials file was not found or is inaccessible.");
        }
    }

    public String getUser() {
        return mUser;
    }

    public String getDatabase() {
        return mDatabase;
    }

    public char[] getPassword() {
        return mPassword;
    }
}
