package com.johnny.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UdpServerThread extends Thread{

    private DatagramPacket packet = null;
    private DatagramSocket datagramSocket = null;
    private byte[] responseBuffer = null;
    /**
     * 客户端UDP端口
     */
    public int port = 0;
    /**
     * 客户端ip
     */
    public String orgHost = null;

    public UdpServerThread(DatagramPacket packet, DatagramSocket datagramSocket) {
        this.packet = packet;
        this.datagramSocket = datagramSocket;
        responseBuffer = new byte[Config.UDP_RESPONSE_CACHE_LEN];
        port = packet.getPort();
        orgHost = packet.getAddress().getHostAddress();
    }

    public void run() {
        try {
            String msg = read();
            //System.out.println("UPD读取线程 msg:--->"+msg);
            MessageReader.read(msg, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取正文
     *
     * @return
     */
    public String read() {
        byte[] data = packet.getData();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < packet.getLength(); i++) {
            String hex = Integer.toHexString(data[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex);
        }
        return sb.toString();
    }
}
