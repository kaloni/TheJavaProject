package util;


//import com.sun.xml.internal.xsom.impl.util.Uri;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class Network {

    //A helper to get current LAN IP address
    public static String getMyIp() {
        String result;
        try {
            result = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            result = "127.0.0.1";
            e.printStackTrace();
        }
        return result;
    }


    public static URI loginUriCreator(String server, int port, String username, String password) throws URISyntaxException {
        List<BasicNameValuePair> params = new ArrayList<>();

        if (username != null && !username.isEmpty()) {
            params.add(new BasicNameValuePair("username", username));
        }

        if (password != null && !password.isEmpty()) {
            params.add(new BasicNameValuePair("password", password));
        }

        return URIUtils.createURI("http", server, port, "/login",
                URLEncodedUtils.format(params, "UTF-8"), null);
    }

    public static URI signUpUriCreator(String server, int port, String username, String password) throws URISyntaxException {
        List<BasicNameValuePair> params = new ArrayList<>();

        if (username != null && !username.isEmpty()) {
            params.add(new BasicNameValuePair("username", username));
        }

        if (password != null && !password.isEmpty()) {
            params.add(new BasicNameValuePair("password", password));
        }

        return URIUtils.createURI("http", server, port, "/signup",
                URLEncodedUtils.format(params, "UTF-8"), null);
    }

    /**
     * Submit user's score to server. return true if success
     * @param username User's username
     * @param score User's score
     * @param serverIP Server IP
     * @param serverPort Server Port
     * @return Return true if success, false otherwise
     */
    public static boolean sendUserScoreToServer(String username, String score, String serverIP, int serverPort) throws URISyntaxException, IOException {
        List<BasicNameValuePair> params = new ArrayList<>();

        if (username != null && !username.isEmpty()) {
            params.add(new BasicNameValuePair("username", username));
        }

        if (score != null && !score.isEmpty()) {
            params.add(new BasicNameValuePair("score", score));
        }

        URI uri = URIUtils.createURI("http", serverIP, serverPort, "/submitScore", URLEncodedUtils.format(params, "UTF-8"), null);

        URL url = uri.toURL();
        URLConnection conn = url.openConnection();
        InputStream is = conn.getInputStream();

        StringWriter writer = new StringWriter();
        IOUtils.copy(is, writer);
        String response = writer.toString();

        JSONObject jsonObject = new JSONObject(response);
        System.out.println(response);

        return jsonObject.getString("response").compareTo("OK") == 0;
    }

    public static String getAllScoresFromServer(String serverIP, int serverPort) throws URISyntaxException, IOException {

        URI uri = URIUtils.createURI("http", serverIP, serverPort, "/getScores", null, null);

        URL url = uri.toURL();
        URLConnection conn = url.openConnection();
        InputStream is = conn.getInputStream();

        StringWriter writer = new StringWriter();
        IOUtils.copy(is, writer);
        String response = writer.toString();

        JSONObject jsonObject = new JSONObject(response);
        System.out.println(response);

        return jsonObject.toString();
    }
}
