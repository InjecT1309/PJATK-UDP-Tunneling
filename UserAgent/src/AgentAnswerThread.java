import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class AgentAnswerThread extends Thread {
    DatagramSocket listen_socket;

    public AgentAnswerThread(int udp_port) throws SocketException {
        listen_socket = new DatagramSocket(udp_port);
    }

    @Override
    public void run() {
        while (true) {
            try {
                byte packet_content[] = new byte[0];
                DatagramPacket packet = new DatagramPacket(packet_content, packet_content.length);
                listen_socket.receive(packet);

                String answer_text = "Port " + listen_socket.getLocalPort() + " have received your message";
                DatagramPacket answer = new DatagramPacket(answer_text.getBytes(), answer_text.length(), packet.getAddress(), packet.getPort());
                listen_socket.send(answer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
