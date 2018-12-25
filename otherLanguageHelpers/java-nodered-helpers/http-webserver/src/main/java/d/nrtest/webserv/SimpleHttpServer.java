package d.nrtest.webserv;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import d.nrtest.nrcommon.PerformanceMeter;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SimpleHttpServer{

    private static byte[] response;
    private static PerformanceMeter meter;

    public static void main(String[] args) throws Exception {

        try {
            response = Files.readAllBytes(Paths.get("../../nodered_resources/d_resources/benchmarkHtmlExample.html"));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        HttpServer server = HttpServer.create(new InetSocketAddress(43221), 0);
        server.createContext("/jbenchmark", new MyHandler());
        server.setExecutor(null); // creates a default executor
        meter = new PerformanceMeter(60000);
        server.start();
    }

    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            meter.notifyEvent();
            t.sendResponseHeaders(200, 0);
            OutputStream os = t.getResponseBody();
            os.write(response);
            os.close();

        }
    }
}