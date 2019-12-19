package com.johnny.udp;

import com.johnny.udp.cache.ServiceMessageCache;

import java.net.DatagramPacket;

public class MainThread extends Thread {
    public void run(){
        try {
            UdpServer.init();

            while(true){
                try {
                    // 缓冲区
                    byte[] buffer = new byte[Config.UDP_RECEIVE_CACHE_LEN];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    UdpServer.receive(packet);
                    read(packet);

                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 单独处理消息服务
     * @param packet
     * @return
     */
    public String read(DatagramPacket packet) {
        //System.out.println("旧机制");
        byte[] data = packet.getData();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < packet.getLength(); i++) {
            String hex = Integer.toHexString(data[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex);
        }
        ServiceMessageCache.AddServiceMsg(sb.toString());
        return sb.toString();
    }
}
