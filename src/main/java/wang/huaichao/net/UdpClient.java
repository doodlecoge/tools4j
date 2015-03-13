package wang.huaichao.net;

import java.io.IOException;
import java.net.*;

/**
 * Created by Administrator on 2015/3/10.
 */
public class UdpClient {
    private DatagramSocket client;
    private InetAddress serverAddr;
    private int serverPort;
    private int timeout = 30000;

    public UdpClient() {
        try {
            client = new DatagramSocket();
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    public UdpClient(String addr, int port) {
        try {
            this.serverAddr = InetAddress.getByName(addr);
            this.serverPort = port;
            client = new DatagramSocket();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public UdpClient(InetAddress addr, int port) {
        try {
            this.serverAddr = addr;
            this.serverPort = port;
            client = new DatagramSocket();
            client.setSoTimeout(timeout);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public void send(byte[] data) throws IOException {
        send(data, data.length);
    }

    public void send(byte[] data, int len) throws IOException {
        send(data, len, this.serverAddr, this.serverPort);
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

    public DatagramPacket recv() throws IOException {
        DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
        client.receive(packet);
        return packet;
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 3) {
            System.out.println("usage: java UdpClient <ip> <port> <message>");
            return;
        }

        UdpClient client = new UdpClient(InetAddress.getByName(args[0]), Integer.valueOf(args[1]));
        client.send(args[2].getBytes(), args[2].length());

        byte[] buff = new byte[1024];
        client.recv(buff);

        System.out.println(new String(buff));
    }
}
