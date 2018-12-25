
package d.nrtest.httpclient;

import d.nrtest.nrcommon.PerformanceMeter;
import d.nrtest.nrcommon.SimpleCommandLineResolver;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Domen Mori 18. 10. 2018.
 */
public class HttpSpammerTest {

    private static int sleepAt = 300000;

    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String urlRed = "http://127.0.0.1:43219/benchmark"; // NodeRED
    private static final String urlNode = "http://127.0.0.1:43220"; // NodeJS
    private static final String urlJ = "http://127.0.0.1:43221"; // JAVA
    private static URL urlObj;
    private static int endCnt;

    private static void configureUrl(String url) {
        try {
            urlObj = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    // HTTP GET request
    private static void sendGet() throws Exception {

        HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

        //add request header
        //     con.setRequestProperty("User-Agent", USER_AGENT);


        int responseCode = con.getResponseCode();
        // REQUEST IS NOW COMPLETE
        System.out.println("\nSending 'GET' request to URL : " + urlObj);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        System.out.println(response.toString());

    }

    private static void sendGetFast() throws Exception {
        // Thread.sleep(1);
        HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();
        con.setRequestMethod("GET");
        int responseCode = con.getResponseCode();
    }

    public static void main(String[] args) {
        SimpleCommandLineResolver resolver = new SimpleCommandLineResolver(args);
        if (resolver.hasAny("--help")) {
            writeHelp();
            return;
        }
        decideUrl(resolver);
        endCnt = resolver.getInt(5200, "--end_cnt", "-e", "--endcnt");
        sleepAt = resolver.getInt(sleepAt, "--sleep", "--sleep_at", "--sleepAt", "-z");
        if (!resolver.hasAny("--quiet", "-q")) {
            System.out.println("EndCnt: " + endCnt + "\nSleepAt: " + sleepAt + "\nUsing url: " + urlObj.toString());
        }
        try {
            runTheSpinner();
        } catch (Exception x) {
            System.err.println("Exception happened, finishing....");
        }

//        URL url = new URL("http://example.com");
//        HttpURLConnection con = (HttpURLConnection) url.openConnection();
//        con.setRequestMethod("GET");
    }

    private static void decideUrl(SimpleCommandLineResolver resolver) {
        if (resolver.hasAny("java", "--java", "-j")) {
            configureUrl(urlJ);
        } else if (resolver.hasAny("node", "-njs", "--node")) {
            configureUrl(urlNode);
        } else {
            configureUrl(urlRed);
        }
        String custom = resolver.getString("--ip", "--url");
        if (custom != null) {
            configureUrl(custom);
        }
    }

    private static void runTheSpinner() {
        new Thread(new Runnable() {
            int counter = 0;

            @Override
            public void run() {
                PerformanceMeter performanceMeter = new PerformanceMeter(endCnt);
                while (true) {
                    try {
                        sendGetFast();
                        if(counter++ > sleepAt){
                            Thread.sleep(1);
                           //  System.out.println("Sleeping at counter: " + counter);
                            counter = 0;
                        }
                        performanceMeter.notifyEvent();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).run();
    }

    private static void writeHelp() {
        System.out.println("\t--end_cnt | --endcnt | -e <integer> :: when to calculate requests number.\n\t--sleep | --sleep_at | --sleepAt | -z <integer> :: after this many req, sleep for 1 ms. To not overload OS socket limit.\n\t" +
                "--java | java | -j for java localhost url:port" +
                "\n\t--node | -njs | node for nodeJS localhost URL|port" +
                "\n\t--ip | --url <url> for custom ip and port (URL). This overrides all." +
                "\n\t--nclients | -n <integer> Multithread bashing." +
                "\n\tDefault IP is nodeRED");
    }
}
