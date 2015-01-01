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

    public static void signUp(String username, String password) throws IOException {
        Options options = new Options();
        options.createIfMissing(true);
        DB db = factory.open(new File("database"), options);
        db.put(bytes(username), bytes(password));
        db.close();
    }

    private static String asString(byte[] bytes) {
        if (bytes!=null) {
            return new String(bytes);
        } else {
            return "";
        }
    }

    private static byte[] bytes(String string) {
        return string.getBytes();
    }
}
