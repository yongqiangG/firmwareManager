package com.johnny.udp;

public class Msg {

    public static byte[] toByteArr(String msg){
        int len = msg.length()/2;
        byte[] data = new byte[len];
        for (int i = 0; i < len; i++) {
            String n = msg.substring(i*2,i*2+2);
            int num = Integer.parseInt(n,16);
            data[i] = (byte)num;
        }
        return data;
    }
}
