import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Relay {
    private enum ERequestType { UNHANDLED, CONNECT, MESSAGE, DISCONNECT }

    static Scanner input = new Scanner(System.in);

    ServerSocket listen_socket;


    private ERequestType getRequestType(String request) {
        String command = request.split(" ")[0];
        if      (command.equals("C"))   return ERequestType.CONNECT;
        else if (command.equals("M"))   return ERequestType.MESSAGE;
        else if (command.equals("D"))   return ERequestType.DISCONNECT;
        else                            return ERequestType.UNHANDLED;
    }

    private void listenForRequests() throws IOException {
        while(true) {
            Socket agent_socket = listen_socket.accept();
            BufferedReader read = new BufferedReader(new InputStreamReader(agent_socket.getInputStream()));

            String request = read.readLine();
            System.out.println(request);

            switch(getRequestType(request)) {
                case CONNECT:
                    new ConnectThread(this).start();
                    break;
                case MESSAGE:
                    new MessageThread(this, agent_socket).start();
                    break;
                case DISCONNECT:
                    new DisconnectThread(this).start();
                    break;
                case UNHANDLED:
                default:
                    System.out.println("Unhandled request type");
            }
        }
    }

    public Relay(int port) {
        try {
            listen_socket = new ServerSocket(port);
            listenForRequests();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        System.out.println("Enter the port to listen on");
        new Relay(input.nextInt());
    }
}
