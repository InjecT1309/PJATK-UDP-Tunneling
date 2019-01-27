import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Relay {
    static Scanner input = new Scanner(System.in);
    ServerSocket listen_socket;

    private void listenForRequests() throws IOException {
        while(true) {
            Socket agent_socket = listen_socket.accept();
            BufferedReader read = new BufferedReader(new InputStreamReader(agent_socket.getInputStream()));

            String request = read.readLine();
            System.out.println("Received: " + request);

            if(ERequestType.getRequestType(request) == ERequestType.CONNECT) {
                String[] raw_receiver_address = request.replace("C ", "").split(":");
                InetSocketAddress receiver_address = new InetSocketAddress(raw_receiver_address[0], Integer.parseInt(raw_receiver_address[1]));
                new ConnectThread(agent_socket, receiver_address).start();
            } else {
                System.out.println("Bad request");
            }
        }
    }

    public Relay(int port) {
        try {
            listen_socket = new ServerSocket(port);
            System.out.println("Listening on port " + port);
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
