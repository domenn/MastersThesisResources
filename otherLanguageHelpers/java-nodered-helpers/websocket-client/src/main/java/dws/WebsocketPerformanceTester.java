package dws;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;


public class WebsocketPerformanceTester {
    private static Random r = new Random();
    private static LocalDateTime[] _stamps = new LocalDateTime[2];
    private long speedModifier = 70;
    private String wsEndpoint= "ws://127.0.0.1:8080/example/javawebsocket";
    private int increaseDecreaseDiffTreshold = 100;
    private int increaseDecreaseDiffTresholdMicro = 4;

    private static void logAndSend(String message, final WebsocketClientEndpoint client) {
        String ts = getTimeStr(_stamps, 0);
        //   System.out.println("Sending >" + message + "< at " + ts);
        client.sendMessage(message);
    }

    /**
     * This is badly designed function that will generate timestamp, write it into other variable and return its string value.
     * Time value gets stored into stamps array so it can get reused.
     *
     * @param stamps index 0 is start time. Index 1 is end time.
     * @param index  caller must tell index where in the array to write.
     * @return string representation to write into console.
     */
    private static String getTimeStr(LocalDateTime[] stamps, int index) {
        stamps[index] = LocalDateTime.now();
        return stamps[index].format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));
    }


    void testWebsocketDelayNode() {

        final int[] counter = new int[]{0};
        final int[] numberReceived = new int[]{0};
        final LocalDateTime[] helperForRateCalcs = new LocalDateTime[2];
        try {
            // open websocket
            final WebsocketClientEndpoint clientEndPoint = new WebsocketClientEndpoint(new URI(wsEndpoint));
            final AtomicBoolean receiving = new AtomicBoolean();

            // add listener
            clientEndPoint.addMessageHandler(new WebsocketClientEndpoint.MessageHandler() {
                public void handleMessage(String message) {
                    System.out.println("Received at " + getTimeStr(_stamps, 1) + " ;: " + message);
                    long diff = ChronoUnit.MILLIS.between(_stamps[0], _stamps[1]);
                    // !! -> !! Time taken is broken for non sync type of sending.
                    // System.out.println("Time taken is " + diff);
                    if (receiving.get()) {
                        ++numberReceived[0];
                        getTimeStr(helperForRateCalcs, 1);
                        System.out.println("Rate: " + (numberReceived[0] / (ChronoUnit.MICROS.between(helperForRateCalcs[0], helperForRateCalcs[1]) / 1000000.0)) + " / s");
                    } else {
                        receiving.set(true);
                        getTimeStr(helperForRateCalcs, 0);
                    }
                    //  logAndSend("_" + counter[0]++ +": more_fromJava" + r.nextInt(798), clientEndPoint);
                }
            });
            for (int i = 0; i < 30000; ++i) {
                // send message to websocket
                logAndSend("_" + counter[0]++ + ": ws_msg_fromJava " + r.nextInt(798), clientEndPoint);
                Thread.sleep(360);
            }

            // wait 5 seconds for messages from websocket
            Thread.sleep(10000);

        } catch (InterruptedException ex) {
            System.err.println("InterruptedException exception: " + ex.getMessage());
        } catch (URISyntaxException ex) {
            System.err.println("URISyntaxException exception: " + ex.getMessage());
        }
    }


    private void testWebsocketTroughput() {
        try {
            // open websocket
            //final dws.WebsocketClientEndpoint clientEndPoint = new dws.WebsocketClientEndpoint(new URI("ws://127.0.0.1:1880/ws/delay"));
            final WebsocketClientEndpoint clientEndPoint = new WebsocketClientEndpoint(new URI(wsEndpoint));
            final long[] count = new long[]{0, 0, speedModifier};
            // count send, count receive, sleep modifier
            final double[] rates = new double[]{0, 0};
            // last rate send, last rate receive


            // add listener
            clientEndPoint.addMessageHandler(message -> {
                ++count[1];
            });

            // Create measure thread
            Thread reporter = new Thread(() -> {
                try {
                    // First wait 1 seconds to let systems launch fully.
                    Thread.sleep(1000);
                    // take current time
                    long startTime = System.currentTimeMillis();
                    long startMsg = count[0];

                    //write speed every 500 ms
                    while (true) {
                        Thread.sleep(500);
                        long tm = System.currentTimeMillis();
                        long msgs = count[0] - startMsg;

                        double speed = msgs / ((tm - startTime) * 0.001);
                        rates[0] = speed;
                        System.out.println("Rate: " + speed + " / s");
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            });

            // Create receive measure thread
            Thread reporterReceive = new Thread(() -> {
                try {
                    // First wait 1 seconds to let systems launch fully.
                    Thread.sleep(1000);
                    // take current time
                    long startTime = System.currentTimeMillis();
                    long startMsg = count[1];

                    //write speed every 500 ms
                    while (true) {
                        Thread.sleep(500);
                        long tm = System.currentTimeMillis();
                        long msgs = count[1] - startMsg;

                        double receiveSpeed = msgs / ((tm - startTime) * 0.001);
                        rates[1] = receiveSpeed;
                        double sendSpeed = rates[0];
                        System.out.println("Receive rate: " + receiveSpeed + " / s");
                        if (sendSpeed - increaseDecreaseDiffTreshold > receiveSpeed) {
                            System.out.println("Modifier: " + count[2] + " (decreased)");
                            count[2] -= 10;
                        } else if (sendSpeed < receiveSpeed - increaseDecreaseDiffTreshold) {
                            count[2] += 16;
                            System.out.println("Modifier: " + count[2] + " (increased)");
                        } else if (sendSpeed - increaseDecreaseDiffTresholdMicro > receiveSpeed) {
                            System.out.println("Modifier: " + count[2] + " (microDecreased)");
                            count[2] -= 1;
                        } else if (sendSpeed < receiveSpeed - increaseDecreaseDiffTresholdMicro) {
                            count[2] += 1;
                            System.out.println("Modifier: " + count[2] + " (microIncreased)");
                        } else {
                            System.out.println("Modifier: " + count[2] + " (unchanged)");
                        }
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            });

            reporter.setDaemon(true);
            reporter.start();
            reporterReceive.setDaemon(true);
            reporterReceive.start();
            int s = 0;
            while (true) {
                //logAndSend("_msg" + r.nextInt(798), clientEndPoint);
                clientEndPoint.sendMessage("_msg" + r.nextInt(798));
                ++count[0];
                if (++s > count[2]) {
                    s = 0;
                    Thread.sleep(1);
                }
            }

            // wait 5 seconds for messages from websocket
            //Thread.sleep(60000);
        } catch (InterruptedException ex) {
            System.err.println("InterruptedException exception: " + ex.getMessage());
        } catch (URISyntaxException ex) {
            System.err.println("URISyntaxException exception: " + ex.getMessage());
        }
    }

    //-w ws://127.0.0.1:1880/javawebsocket
    public static void main(String[] args) {
        args = new String("-w ws://127.0.0.1:1880/javawebsocket --starting-speed 3000").split(" ");
        Options options = new Options();

        options.addOption("w", "endpoint", true, "Full URL of websocket");
        options.addOption("n", "starting-speed", true, "Initial speed modifier parameter");
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd;
        try {
             cmd = parser.parse( options, args);
        } catch (ParseException e) {
            System.out.println("Unable to parse! Stopping");
            e.printStackTrace();
            return;
        }


        WebsocketPerformanceTester o = new WebsocketPerformanceTester();
        if(cmd.hasOption("w")){
            o.setEndpoint(cmd.getOptionValue("w"));
        }
        if(cmd.hasOption("n")){
            o.setSpeedParameter(Integer.valueOf(cmd.getOptionValue("n")));
        }

        o.testWebsocketTroughput();
    }

    private void setSpeedParameter(Integer n) {
        speedModifier = n;
    }

    private void setEndpoint(String w) {
        wsEndpoint = w;
    }

}


