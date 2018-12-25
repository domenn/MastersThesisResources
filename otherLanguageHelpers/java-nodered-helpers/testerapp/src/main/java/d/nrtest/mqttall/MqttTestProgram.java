package d.nrtest.mqttall;

import d.nrtest.nrcommon.MessageProvider;
import d.nrtest.nrcommon.PerformanceMeter;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.concurrent.CountDownLatch;

/**
 * @author Domen Mori 28/04/2018.
 */
public class MqttTestProgram {

    private static CountDownLatch countDownLatch;

    private static final String MQTT_TOPIC_RESPOND_TO = "javamqtttest";
    private static final String MQTT_TOPIC_RESPOND_TO_V2 = "javamqtttestV2";
    private static final int MY_END_CNT = 210;
    private static MessageProvider m;
    private static PerformanceMeter pm;

    private static void mqttTrasher() throws MqttException {
        MqttClient client = new MqttClient("tcp://localhost:1883", MqttClient.generateClientId());
        client.connect();
        for (int i = 0; i < 10000; ++i) {
            System.out.println(i);
            MqttMessage message = new MqttMessage();
            message.setPayload(("set java 3" + String.valueOf(i)).getBytes());
            client.publish("nodered", message);
        }
        client.disconnect();
    }

    @Deprecated
    private static void subscribeMqtt(MqttClient client, String replyTopic) throws MqttException {

        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable throwable) {

            }

            @Override
            public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                String response = mqttMessage.toString();
                if (!response.startsWith("MqResponse")) {
                    System.err.println("HUGE PROBLEM! RETURNED THING IS NOT CORRECT!");
                }
                countDownLatch.countDown();
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
        client.subscribe(replyTopic, 2);
    }

    private static void subscribeMqttV2(MqttClient client, String replyTopic) throws MqttException {

        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable throwable) {

            }

            @Override
            public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                String response = mqttMessage.toString();
                if (!response.startsWith("MqResponse")) {
                    System.err.println("HUGE PROBLEM! RETURNED THING IS NOT CORRECT!");
                }
                sendMqttMessage(client);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
        client.subscribe(replyTopic, 2);
    }

    //private static MqttMessage message = new MqttMessage();

    private static void sendMqttMessage(MqttClient client) throws MqttException, InterruptedException {
        MqttMessage message = new MqttMessage();
        message.setPayload((m.getMessageAsJson()).getBytes());
        client.publish("nodeserver", message);
        pm.notifyEvent();
    }


    /**
     * First version of function. Should not change anymore .. FROZEN. Use v2 instead. Keeping in for archives.
     */
    @Deprecated
    private static void mqttSpinner() throws MqttException {
        MqttClient client = new MqttClient("tcp://localhost:1883", MqttClient.generateClientId());
        client.connect();
        subscribeMqtt(client, MQTT_TOPIC_RESPOND_TO);
        pm = new PerformanceMeter(450);
        m = new MessageProvider(MQTT_TOPIC_RESPOND_TO);
        try {
            // Step 1: set latch to 1 ... then publish
            // Step 2: send msg
            // Step 3: await latch .. once it is unlocked, response was OK.
            while (true) {
                countDownLatch = new CountDownLatch(1);
                sendMqttMessage(client);
                countDownLatch.await();
            }
        } catch (Exception ignored) {

        } finally {
            client.disconnect();
        }
    }

    private static void mqttSpinnerV2() throws MqttException {
        MqttClient client = new MqttClient("tcp://localhost:1883", MqttClient.generateClientId());
        client.connect();
        subscribeMqttV2(client, MQTT_TOPIC_RESPOND_TO_V2);
        pm = new PerformanceMeter(MY_END_CNT);
        m = new MessageProvider(MQTT_TOPIC_RESPOND_TO_V2);
        try {
            // Step 1: send first message, then the procedure will loop itself. Use latch to wait forever.
            countDownLatch = new CountDownLatch(1);
            sendMqttMessage(client);
            countDownLatch.await();
        } catch (Exception ignored) {

        } finally {
            client.disconnect();
        }
    }

    public static void main(String[] args) throws MqttException {
        //    mqttTrasher();
      //   mqttSpinner();
         mqttSpinnerV2();
      //  new MqttTestProgram().waitMethod();
    }

//    private synchronized void waitMethod() {
private void waitMethod() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    try {
                        Thread.sleep(4000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).run();

//        while (true) {
//            System.out.println("always running program ==> " + Calendar.getInstance().getTime());
//            try {
//                wait(2000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
    }
}
