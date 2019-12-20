package com.johnny.udp;

import java.util.Date;

public class ResponseMsg {
    public ResponseMsg(String macCode,int cmd,String msg,int seq){
        this.cmd = cmd;
        this.macCode = macCode;
        this.msg = msg;
        this.lastSendTime = new Date().getTime();
        this.seq = seq;
    }

    public int sendTimes = 0;
    public int cmd;
    public String macCode;
    public int seq;
    public long lastSendTime;
    public String msg;
}
