package com.johnny.udp;

import java.util.Date;

public class Msg {

    public Msg(String macCode,int seq,int cmd,String msg){
        this.macCode = macCode;
        this.seq = seq;
        this.lastSendTime = new Date().getTime();
        this.cmd = cmd;
        this.msg = msg;
    }
    public boolean success = false;
    public boolean offline = false;//终端不在线
    public int seq;
    public int cmd; //命令编号
    public long lastSendTime;//最后发送时间
    public String macCode; //机器编码
    public String msg;
    public long deleteTime = -1;//负数表不删除    执行成功后，移除该消息缓存的时间
    public int sendTimes; //发送次数


    public byte[] toByte(){
        return toByteArr(this.msg);
    }
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
