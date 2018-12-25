package d.nrtest.nrcommon;

import java.util.Random;

/**
 * @author Domen Mori 30/04/2018.
 */
public class MessageProvider {
    private static final String[] commands = {"set", "add", "check"};
    private static final String[] fields = {"primary", "secondary", "fallback", "counter", "accumulator"};
    private static final String[] values = {"different", "knowledgeable", "damage multi word",
            "wandering", "previous thing", "volcano", "unequaled those woords don't connect at all"};

    private static final int MAX_NUMBER = 300;
    private String respondDestination;

    private Random random = new Random();

    public MessageProvider(String respondTopic) {
        respondDestination = respondTopic;
    }

    public MessageProvider() {
        this("default_response_name");
    }

    private String getMessage() {
        // First decide if we take number or text. 30% probability for number, otherwise text.
        boolean number = random.nextInt(100)<=30;
        return commands[random.nextInt(commands.length)] + " " + fields[random.nextInt(fields.length)]
                + " " + (number?String.valueOf(random.nextInt(MAX_NUMBER*2)-MAX_NUMBER):values[random.nextInt(values.length)]);
    }

    public String getMessageAsJson() {
       // return "{\"payload\":\"" + getMessage() + "\",\"respond_to\":\"" + respondDestination + "\"}";
        return "{\"payload\":\"IamJava\",\"respond_to\":\"" + respondDestination + "\"}";
    }


}
