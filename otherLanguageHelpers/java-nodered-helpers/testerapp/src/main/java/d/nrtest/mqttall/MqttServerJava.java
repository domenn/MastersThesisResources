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
public class MqttServerJava {

    private static CountDownLatch countDownLatch;

    private static final String JSON_IMPORTANT_PART = "respond_to\":\"";
    private static final int JSON_IMPORTANT_PART_LEN = JSON_IMPORTANT_PART.length();
    private static final String JSON_IMPORTANT_PART_END = "\"";

    private static final String MQTT_TOPIC_RESPOND_TO_V2 = "javamqtts";
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

    private  static String getReturnTopic(String json){
        int importantItemIndex = json.indexOf(JSON_IMPORTANT_PART)+JSON_IMPORTANT_PART_LEN;
        return json.substring(importantItemIndex,
                json.indexOf(JSON_IMPORTANT_PART_END, importantItemIndex));
    }

    private static void subscribeMqttV2(MqttClient client, String replyTopic) throws MqttException {

        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable throwable) {

            }

            @Override
            public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                String returningTo = getReturnTopic(mqttMessage.toString());
                sendMqttMessage(client, returningTo);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
        client.subscribe(replyTopic, 2);
    }

    //private static MqttMessage message = new MqttMessage();

    private static void sendMqttMessage(MqttClient client, String topic) throws MqttException, InterruptedException {
        MqttMessage message = new MqttMessage();
        client.publish(topic, "MqResponse from Javay :)".getBytes(), 2, false);
        pm.notifyEvent();
    }

    private static void server() throws MqttException {
        MqttClient client = new MqttClient("tcp://localhost:1883", MqttClient.generateClientId());
        client.connect();
        subscribeMqttV2(client, MQTT_TOPIC_RESPOND_TO_V2);
        pm = new PerformanceMeter(MY_END_CNT);
        m = new MessageProvider(MQTT_TOPIC_RESPOND_TO_V2);
        countDownLatch = new CountDownLatch(1);
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws MqttException {
        //    mqttTrasher();
      //   mqttSpinner();
         server();
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
