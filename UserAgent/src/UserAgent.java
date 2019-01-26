import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

public class UserAgent {
    static Scanner input = new Scanner(System.in);;
    InetSocketAddress relay_address;
    Socket relay_connection_socket;

    AgentAnswerThread answer_thread;

    private void readRelayAddress() {
        String[] raw_relay_address;
        do {
            System.out.println("Enter the relay address [ip:port]");
            raw_relay_address = input.nextLine().split(":");
        } while (raw_relay_address.length != 2);

        relay_address = new InetSocketAddress(raw_relay_address[0], Integer.parseInt(raw_relay_address[1]));
    }

    private InetSocketAddress getReceiversAddress() {
        String[] raw_receiver_address;
        do {
            System.out.println("Enter the receiver address [ip:port]");
            raw_receiver_address = input.nextLine().split(":");
        } while (raw_receiver_address.length != 2);

        return new InetSocketAddress(raw_receiver_address[0], Integer.parseInt(raw_receiver_address[1]));
    }

    private void connect() {
        if(relay_connection_socket != null && !relay_connection_socket.isClosed()) {
            System.out.println("Close your previous connection first");
        }

        InetSocketAddress receiver_address = getReceiversAddress();
        String message = "C " + receiver_address.toString().replace("/", ""); //connect

        try {
            relay_connection_socket = new Socket(relay_address.getAddress(), relay_address.getPort());
            PrintWriter write = new PrintWriter(relay_connection_socket.getOutputStream());

            write.println(message);
            System.out.println("Sent: " + message);
            write.close();
        } catch (IOException e) {
            System.out.println("Relay refused the connection");
        }
    }

    private void sendMessage() {
        System.out.println("Enter your message:");
        String message = "M "; //message
        message += input.nextLine();

        try {
            Socket socket = new Socket(relay_address.getAddress(), relay_address.getPort());
            PrintWriter write = new PrintWriter(socket.getOutputStream());
            BufferedReader read = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            write.println(message);
            System.out.println("Sent: " + message);
            write.close();

            String answer = "";
            String tmp;
            while ((tmp = read.readLine()) != null) {
                answer += tmp;
            }

            System.out.println("Received: " + answer);
        } catch (IOException e) {
            System.out.println("Relay refused the connection");
        }
    }

    public void disconnect() {
        String message = "D"; //disconnect

        try {
            Socket socket = new Socket(relay_address.getAddress(), relay_address.getPort());
            PrintWriter write = new PrintWriter(socket.getOutputStream());

            write.println(message);
            System.out.println("Sent: " + message);
            write.close();

            socket.close();
        } catch (IOException e) {
            System.out.println("Relay refused the connection");
        }
    }

    public boolean displayActionMenu() {
        System.out.println("What action would you like to preform? ");
        System.out.println("[1] - connect");
        System.out.println("[2] - send message");
        System.out.println("[3] - disconnect");
        System.out.println("[0] - exit");

        switch(input.nextInt()) {
            case 1:
                connect();
                return true;
            case 2:
                sendMessage();
                return true;
            case 3:
                disconnect();
                return true;
            case 0:
                answer_thread.stop();
                return false;
            default:
                System.out.println("Unrecognized action");
                return true;
        }
    }

    public UserAgent(int udp_port) {
        System.out.println("Answering on port: " + udp_port);

        try {
            answer_thread = new AgentAnswerThread(udp_port);
            answer_thread.start();
        } catch (SocketException e) {
            e.printStackTrace();
        }

        readRelayAddress();

        while(displayActionMenu());
    }

    public static void main(String[] args) {
        System.out.println("Enter the port to listen on");
        new UserAgent(input.nextInt());
    }
}
