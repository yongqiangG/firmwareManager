package com.johnny.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * 服务器端类
 */
public class UDPServer {
    //参数:本地监听端口号
    public static void serverStartListen(String macCode,int port) throws IOException {
        DatagramSocket socket = new DatagramSocket(port);
        byte[] data = new byte[Config.UDP_RECEIVE_LEN];
        DatagramPacket packet = new DatagramPacket(data, data.length);
        System.out.println("-----服务端已启动,正在监听端口"+port+"的数据-----");
        while(true){
            socket.receive(packet);
            String hexString = UDPServerThread.bytesToHex(data);
            int cmd = UDPServerThread.getCommand(hexString);
            if(cmd==0x70){
                UDPServerThread thread = new UDPServerThread(data,socket, packet,macCode);
                thread.start();
            }


        }
    }

    public static void main(String[] args) throws IOException {
        String macCode = null;
        DatagramSocket socket = new DatagramSocket(3339);
        byte[] data = new byte[Config.UDP_RECEIVE_LEN];
        DatagramPacket packet = new DatagramPacket(data, data.length);
        System.out.println("-----服务端已启动-----");
        //循环监听
        while(true){
            socket.receive(packet);
            UDPServerThread thread = new UDPServerThread(data,socket, packet,macCode);
            thread.start();
        }

        //4.關閉資源
        //socket.close();
    }


}