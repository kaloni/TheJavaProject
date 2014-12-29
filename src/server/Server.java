package server;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import sun.net.www.protocol.http.HttpURLConnection;
import util.Parser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.Executors;

public class Server {

    public static final int DEFAULT_SERVER_PORT = 8000;
    private HttpServer server;

    public Server() throws IOException {
        server = HttpServer.create(new InetSocketAddress(DEFAULT_SERVER_PORT), 0);
        server.createContext("/login", new LoginHandler());
        server.createContext("/signup", new SignUpHandler());
        server.setExecutor(Executors.newCachedThreadPool());
    }

    public void start() {
        System.out.println("Server: Start");
        server.start();
    }


    static class LoginHandler implements HttpHandler {
        public void handle(HttpExchange httpExchange) throws IOException {

            String requestMethod = httpExchange.getRequestMethod();
            try {
                OutputStream responseBody = httpExchange.getResponseBody();

                if (requestMethod.equalsIgnoreCase("GET")) {
                    Map<String, String> query = Parser.splitQuery(httpExchange.getRequestURI().getRawQuery());
                    String response;

                    if (query.containsKey("username") && query.containsKey("password")) {
                        response = "Login: OK";
                        //todo verify username & password
                    } else {
                        response = "Missing params";
                    }

                    httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.getBytes().length);
                    responseBody.write(response.getBytes());

                } else {
                    httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 1);
                }


                responseBody.close();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    static class SignUpHandler implements HttpHandler {
        public void handle(HttpExchange httpExchange) throws IOException {

            String requestMethod = httpExchange.getRequestMethod();
            try {
                OutputStream responseBody = httpExchange.getResponseBody();

                if (requestMethod.equalsIgnoreCase("GET")) {
                    Map<String, String> query = Parser.splitQuery(httpExchange.getRequestURI().getRawQuery());
                    String response;

                    if (query.containsKey("username") && query.containsKey("password")) {
                        response = "Sign Up: OK";
                        //todo sign up processing. record the username & password to a file
                    } else {
                        response = "Missing params";
                    }

                    httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.getBytes().length);
                    responseBody.write(response.getBytes());

                } else {
                    httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 1);
                }


                responseBody.close();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

//    static class MyHandler implements HttpHandler {
//        public void handle(HttpExchange httpExchange) throws IOException {
//
//            String requestMethod = httpExchange.getRequestMethod();
//            try {
//                if (requestMethod.equalsIgnoreCase("POST")) {
//                    Headers responseHeaders = httpExchange.getResponseHeaders();
//                    responseHeaders.set("Content-Type", "text/plain");
//
//                    InputStream bodyStream = httpExchange.getRequestBody();
//                    //requestBody = URLDecoder.decode(requestBody,"UTF-8");
//                } else if (requestMethod.equalsIgnoreCase("GET")) {
//                    String query = httpExchange.getRequestURI().getRawQuery();
//                }
//
//                httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 1);
//                OutputStream responseBody = httpExchange.getResponseBody();
//                responseBody.write("1".getBytes());
//                responseBody.close();
//
//            } catch(Exception ex) {
//                ex.printStackTrace();
//            }
//        }
//    }

}
