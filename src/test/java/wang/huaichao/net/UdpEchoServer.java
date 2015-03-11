package wang.huaichao.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * Created by Administrator on 2015/3/10.
 */
public class UdpEchoServer {
    private static final int buffSize = 1024;
    private DatagramSocket server;


    public UdpEchoServer(int port) {
        try {
            server = new DatagramSocket(port);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }

    }

    public void start() {
        byte[] buff = new byte[buffSize];
        DatagramPacket recvPacket = new DatagramPacket(buff, buff.length);

        while (true) {
            try {
                server.receive(recvPacket);
                System.out.println("recv: > " + new String(recvPacket.getData()));
                _echo(recvPacket);
            } catch (IOException e) {
            }
        }
    }

    private void _echo(DatagramPacket packet) throws IOException {
        int port = packet.getPort();
        InetAddress addr = packet.getAddress();
        byte[] data = packet.getData();

        DatagramPacket sendPacket =
                new DatagramPacket(data, data.length, addr, port);
        server.send(sendPacket);
    }

    public static void main(String[] args) throws IOException {
        new UdpEchoServer(8888).start();
    }
}
