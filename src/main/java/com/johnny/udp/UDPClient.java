package com.johnny.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class UDPClient {

    public static void main(String[] args) throws Exception {
        DatagramSocket socket = new DatagramSocket();
        Scanner s = new Scanner(System.in);
        //循环发送数据
        while(true){
            String str = s.nextLine();
            if("end".equals(str)){
                break;
            }
            byte[] bytes = str.getBytes();
            InetAddress address = InetAddress.getByName("127.0.0.1");
            int port = 3339;
            DatagramPacket packet = new DatagramPacket(bytes,bytes.length,address,port);
            socket.send(packet);
        }

    }

}
