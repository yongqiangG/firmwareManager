package com.johnny.udp;

public class MessageReader {
    public static synchronized void read(String msg,UdpServerThread thread) {
        if(msg.length()<8) return;
        String topIndex=msg.substring(0,8);
        if(topIndex.toLowerCase().equals("ffffffff"))
        {
            System.out.println("小可控制"+msg);
            //read(thread,msg);
        }
    }


}
