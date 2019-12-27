package com.johnny.udp;

import com.johnny.udp.cache.ServiceMessageCache;

import java.net.DatagramPacket;
import java.util.Observable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UDPMainThread extends Observable implements Runnable {
    public static int thrCnt=0;
    // 此方法一经调用，立马可以通知观察者，在本例中是监听线程
    public void doBusiness() {
        if (true) {
            super.setChanged();
        }
        notifyObservers();
    }

    public void run() {
        UdpServer.init();
        // 初始线程池
        // 创建一个可重用固定线程数的线程池
        ExecutorService pool = Executors.newFixedThreadPool(Config.MAX_POOL_SIZE);
        byte[] buffer = new byte[Config.UDP_RECEIVE_CACHE_LEN];
        while(true){
            try {
                // 缓冲区
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                UdpServer.receive(packet,pool);
                read(packet);
            } catch (Exception e) {
                e.printStackTrace();
                doBusiness();
                break;
            }
        }
    }
    public void read(DatagramPacket packet) {
        int port = packet.getPort();
        String orgHost = packet.getAddress().getHostAddress();
        byte[] data = packet.getData();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < packet.getLength(); i++) {
            String hex = Integer.toHexString(data[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex);
        }
        //System.out.println("新的机制:"+ sb.toString());
        ServiceMessageCache.AddServiceMsg(sb.toString());
        ServiceMessageCache.RefreshIPPort(sb.toString(), orgHost, port);
        //ServiceMessageCache.Msg2IPPort(sb,)
    }
}
