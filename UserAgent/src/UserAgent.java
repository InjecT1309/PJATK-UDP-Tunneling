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

    private InetSocketAddress readAddress(String message) {
        String[] raw_relay_address;

        System.out.println(message);
        do {
            raw_relay_address = input.nextLine().split(":");
        } while(raw_relay_address.length != 2);

        return new InetSocketAddress(raw_relay_address[0], Integer.parseInt(raw_relay_address[1]));
    }

    private void connect() {
        if(relay_connection_socket != null && !relay_connection_socket.isClosed()) {
            System.out.println("Close your previous connection first");
            return;
        }

        InetSocketAddress receiver_address = readAddress("Enter the receiver address [ip:port]");
        String message = "C " + receiver_address.toString().replace("/", ""); //connect

        try {
            relay_connection_socket = new Socket(relay_address.getAddress(), relay_address.getPort());
            PrintWriter write = new PrintWriter(relay_connection_socket.getOutputStream());

            write.println(message);
            System.out.println("Sent: " + message);
            write.flush();

        } catch (IOException e) {
            System.out.println("Relay refused the connection");
            e.printStackTrace();
        }
    }

    private void sendMessage() {
        if(relay_connection_socket == null || relay_connection_socket.isClosed()) {
            System.out.println("Open a connection first");
            return;
        }

        System.out.println("Enter your message:");
        String message = "M "; //message
        message += input.nextLine();
        try {
            PrintWriter write = new PrintWriter(relay_connection_socket.getOutputStream());
            BufferedReader read = new BufferedReader(new InputStreamReader(relay_connection_socket.getInputStream()));

            write.println(message);
            System.out.println("Sent: " + message);
            write.flush();

            String answer = read.readLine();

            System.out.println("Received: " + answer);
        } catch (IOException e) {
            System.out.println("Relay refused the connection");
            e.printStackTrace();
        }
    }

    public void disconnect() {
        if(relay_connection_socket == null || relay_connection_socket.isClosed()) {
            System.out.println("Open a connection first");
            return;
        }

        String message = "D"; //disconnect
        try {
            PrintWriter write = new PrintWriter(relay_connection_socket.getOutputStream());

            write.println(message);
            System.out.println("Sent: " + message);
            write.flush();

            relay_connection_socket.close();
        } catch (IOException e) {
            System.out.println("Relay refused the connection");
            e.printStackTrace();
        }
    }

    public void setRelayAddress() {
        relay_address = readAddress("Enter the relay address [ip:port]");
    }

    public boolean displayActionMenu() {
        System.out.println("What action would you like to preform? ");
        System.out.println("[1] - connect");
        System.out.println("[2] - send message");
        System.out.println("[3] - disconnect");
        System.out.println("[4] - set relay address");
        System.out.println("[0] - exit");

        int action = input.nextInt();
        input.nextLine(); //skip the \n sign not picked up by nextInt()

        switch(action) {
            case 1:
                connect();
                return true;
            case 2:
                sendMessage();
                return true;
            case 3:
                disconnect();
                return true;
            case 4:
                setRelayAddress();
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
        input.nextLine(); //skip the \n sign not picked up by nextInt()

        System.out.println("Answering on port: " + udp_port);

        try {
            answer_thread = new AgentAnswerThread(udp_port);
            answer_thread.start();
        } catch (SocketException e) {
            e.printStackTrace();
        }

        while(displayActionMenu());
    }

    public static void main(String[] args) {
        System.out.println("Enter the port to listen on");
        new UserAgent(input.nextInt());
    }
}
