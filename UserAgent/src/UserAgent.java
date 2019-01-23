import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.Scanner;

public class UserAgent {
    Scanner input;
    InetSocketAddress relay_address;

    DatagramSocket listen_socket;
    ServerSocket send_socket;

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

    private void sendMessage() {
        InetSocketAddress receiver_address = getReceiversAddress();
        System.out.println("Enter your message:");
        String message = input.nextLine();

        System.out.println("Message sent");
    }

    public UserAgent(int tcp_port, int udp_port) throws IOException {
        input = new Scanner(System.in);

        readRelayAddress();

        send_socket = new ServerSocket(tcp_port);
        listen_socket = new DatagramSocket(udp_port);

        while(true) {
            sendMessage();
        }
    }

    public static void main(String[] args) {
        try {
            new UserAgent(5080, 5081);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
