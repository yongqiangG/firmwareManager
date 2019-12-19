package com.johnny.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class UdpServer {

    private static InetSocketAddress socketAddress = null; // 服务监听个地址
    public static DatagramSocket datagramSocket = null; // 连接对象
    public static int threadCnt = 0;

    /**
     * 初始化连接
     * @throws SocketException
     */
    public static void init(){
        try {
            socketAddress = new InetSocketAddress(Config.UDP_SERVER_IP, Config.UDP_SERVER_PORT);
            if(datagramSocket == null){
                datagramSocket = new DatagramSocket(null);
                datagramSocket.setReuseAddress(true);
                datagramSocket.bind(new InetSocketAddress(Config.UDP_SERVER_PORT));
            }
            System.out.println("[UDP服务端已经启动]");
        } catch (Exception e) {
            datagramSocket = null;
            System.err.println("[UDP服务端启动失败]");
            e.printStackTrace();
        }
    }

    /* 初始代码*/
    public static DatagramPacket receive(DatagramPacket packet) throws Exception {

        try {
            datagramSocket.receive(packet);
            new UdpServerThread(packet,datagramSocket).start();
            return packet;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
