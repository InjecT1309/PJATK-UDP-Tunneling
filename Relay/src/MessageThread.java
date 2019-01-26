import java.net.Socket;

public class MessageThread extends Thread {
    Relay parent;
    Socket agent_socket;

    public MessageThread(Relay parent, Socket agent_socket) {
        this.parent = parent;
        this.agent_socket = agent_socket;
    }

    @Override
    public void run() {
        
    }
}
