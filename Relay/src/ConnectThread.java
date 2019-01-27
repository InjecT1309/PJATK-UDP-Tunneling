import java.io.*;
import java.net.*;

public class ConnectThread extends Thread {
    Socket agent_socket;
    InetSocketAddress receiver_address;

    public ConnectThread(Socket agent_socket, InetSocketAddress receiver_address) {
        this.agent_socket = agent_socket;
        this.receiver_address = receiver_address;
    }

    public String forwardMessage(String message) throws IOException {
        DatagramSocket receiver_socket = new DatagramSocket();
        DatagramPacket message_packet = new DatagramPacket(message.getBytes(), message.length(), receiver_address.getAddress(), receiver_address.getPort());
        receiver_socket.send(message_packet);

        byte[] datagram_bytes = new byte[2048];
        DatagramPacket answer_packet = new DatagramPacket(datagram_bytes, datagram_bytes.length);
        receiver_socket.receive(answer_packet);

        return new String(answer_packet.getData());
    }

    @Override
    public void run() {
        try {
            BufferedReader read = new BufferedReader(new InputStreamReader(agent_socket.getInputStream()));
            PrintWriter write = new PrintWriter(agent_socket.getOutputStream());
            String request;

            while((request = read.readLine()) != null) {
                switch(ERequestType.getRequestType(request)) {
                    case MESSAGE:
                        System.out.println("Processing: " + request);
                        String response = forwardMessage(request.replace("M ", ""));
                        System.out.println("Forwarding: " + response);
                        write.println(response);
                        write.flush();
                        break;
                    case DISCONNECT:
                        System.out.println("Processing: " + request);
                        read.close();
                        write.close();
                        agent_socket.close();
                        return;
                    case CONNECT:
                    case UNHANDLED:
                    default:
                        System.out.println("Bad request");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
