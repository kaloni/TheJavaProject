package util;


import com.sun.xml.internal.xsom.impl.util.Uri;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
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
        List<BasicNameValuePair> qparams = new ArrayList<>();

        if (username != null && !username.isEmpty()) {
            qparams.add(new BasicNameValuePair("username", username));
        }

        if (password != null && !password.isEmpty()) {
            qparams.add(new BasicNameValuePair("password", password));
        }

        return URIUtils.createURI("http", server, port, "/login",
                URLEncodedUtils.format(qparams, "UTF-8"), null);
    }

    public static URI signUpUriCreator(String server, int port, String username, String password) throws URISyntaxException {
        return loginUriCreator(server, port, username, password);
    }
}
