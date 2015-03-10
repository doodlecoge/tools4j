package wang.huaichao.net;

import java.io.IOException;
import java.net.*;

/**
 * Created by Administrator on 2015/3/10.
 */
public class UdpClient {
    private DatagramSocket client;

    public UdpClient() {
        try {
            client = new DatagramSocket();
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }


    public void send(byte[] data, int len, InetAddress addr, int port) throws IOException {
        DatagramPacket packet = new DatagramPacket(data, len, addr, port);
        client.send(packet);
    }

    public DatagramPacket recv(byte[] buff) throws IOException {
        DatagramPacket packet = new DatagramPacket(buff, buff.length);
        client.receive(packet);
        return packet;
    }
}
