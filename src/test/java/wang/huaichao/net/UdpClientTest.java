package wang.huaichao.net;

import wang.huaichao.text.Converter;

import java.io.IOException;
import java.net.InetAddress;

/**
 * Created by Administrator on 2015/3/10.
 */
public class UdpClientTest {
    public static void main(String[] args) throws IOException {
//        UdpClient client = new UdpClient("numb.viagenie.ca", 3478);
//        String allocate = "000300202112a4423e4977024c01024032275268001900041100000080220009696365346a2e6f726700000080280004ad991fa5";
//        String binding = "000100182112a442414977024c016f97701cdaa080220009696365346a2e6f726700000080280004e896545a";
//
//        byte[] bytes = Converter.hexStringToByteArray(allocate);
//        client.send(bytes, bytes.length);
//        client.recv();

        String send = "echoaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
        UdpClient client = new UdpClient(InetAddress.getByName("127.0.0.1"), 9988);
        client.send(send.getBytes(), send.length());

        byte[] buff = new byte[1024];
        client.recv(buff);

        System.out.println(new String(buff));

    }
}
