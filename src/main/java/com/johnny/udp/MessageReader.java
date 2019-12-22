package com.johnny.udp;

public class MessageReader {
    public static synchronized void read(String msg,UdpServerThread thread) {
        if(msg.length()<8) return;
        String topIndex=msg.substring(0,8);
        //TODO 对udp包进行过滤判断
        //read(thread,msg);
    }


}
