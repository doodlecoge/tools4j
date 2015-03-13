package wang.huaichao.net;

import java.io.ByteArrayOutputStream;
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
    private int timeout = 30000;


    public UdpEchoServer(int port) {
        try {
            server = new DatagramSocket(port);
            server.setSoTimeout(timeout);
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
                String msg = new String(recvPacket.getData(), 0, recvPacket.getLength());
                System.out.println(recvPacket.getAddress().getHostAddress() + ": " + msg);
                _echo(recvPacket);
            } catch (IOException e) {
            }
        }
    }

    private void _echo(DatagramPacket packet) throws IOException {
        int port = packet.getPort();
        InetAddress addr = packet.getAddress();
        DatagramPacket sendPacket =
                new DatagramPacket(packet.getData(), packet.getLength(), addr, port);
        server.send(sendPacket);
    }

    public static void main(String[] args) throws IOException {
        int port = 0;
        if (args.length == 1) {
            port = Integer.valueOf(args[0]);
        } else {
            System.out.print("please enter port to listen on: ");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int b;
            while (true) {
                b = System.in.read();
                if (b == '\n') break;
                baos.write(b);
            }
            port = Integer.valueOf(baos.toString());
        }

        System.out.println("starting UDP echo server on port: " + port);
        new UdpEchoServer(port).start();
    }
}
