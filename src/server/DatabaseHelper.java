package server;

import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;

import java.io.File;
import java.io.IOException;

import static org.iq80.leveldb.impl.Iq80DBFactory.factory;

public class DatabaseHelper {
    private static final String DATABASE_NAME = "database";

    public static boolean loginSuccessful(String username, String password) throws IOException {
        Options options = new Options();
        options.createIfMissing(true);
        DB db = factory.open(new File(DATABASE_NAME), options);
        String value = asString(db.get(bytes(username)));
        db.close();

        return !password.isEmpty() && password.compareTo(value) == 0;
    }

    public static boolean signUp(String username, String password) throws IOException {
        Options options = new Options();
        options.createIfMissing(true);
        DB db = factory.open(new File("database"), options);

        String value = asString(db.get(bytes(username)));

        if (!value.isEmpty()) { //user already registered
            return false;
        }

        db.put(bytes(username), bytes(password));
        db.close();
        return true;
    }

    public static boolean submitScore(String username, String score) throws IOException {
        Options options = new Options();
        options.createIfMissing(true);
        DB db = factory.open(new File("database"), options);

        String value = asString(db.get(bytes("scores")));

        org.json.JSONObject scoreJSON;
        if (!value.isEmpty()) { //user score
            scoreJSON = new org.json.JSONObject(value);
        } else {
            scoreJSON = new org.json.JSONObject();
        }

        scoreJSON.put(username, score);

        System.out.println("All Scores: \n" + scoreJSON.toString());

        db.put(bytes("scores"), bytes(scoreJSON.toString()));
        db.close();
        return true;
    }

    public static String getAllScoreAsJsonString() throws IOException {
        Options options = new Options();
        options.createIfMissing(true);
        DB db = factory.open(new File("database"), options);

        String value = asString(db.get(bytes("scores")));
        db.close();
        org.json.JSONObject scoreJSON;
        if (!value.isEmpty()) { //user score
            scoreJSON = new org.json.JSONObject(value);
        } else {
            scoreJSON = new org.json.JSONObject();
        }

        System.out.println("All Scores: \n" + scoreJSON.toString());

        return scoreJSON.toString();
    }

    private static String asString(byte[] bytes) {
        if (bytes != null) {
            return new String(bytes);
        } else {
            return "";
        }
    }

    private static byte[] bytes(String string) {
        return string.getBytes();
    }
}
