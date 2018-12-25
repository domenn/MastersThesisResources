import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

@ServerEndpoint(value = "/javawebsocket")
public class WebsocketTestingServer {

    @OnOpen
    public void onOpen(Session session) {
        System.out.printf("Session opened, id: %s%n", session.getId());
//        try {
//            session.getBasicRemote().sendText("Hi there, we are successfully connected.");
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        //System.out.printf("Message received. Session id: %s Message: %s%n",
        //        session.getId(), message);
        try {
         //   session.getBasicRemote().sendText(message + "java ack");

            session.getBasicRemote().sendText(message );
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @OnError
    public void onError(Throwable e) {
        e.printStackTrace();
    }

    @OnClose
    public void onClose(Session session) {
        System.out.printf("Session closed with id: %s%n", session.getId());
    }
}

