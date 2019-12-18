package com.johnny.udp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//发送数据到设备
public class MessageSender {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    //发送0x72固件升级置零
    public static void sendHardWareUpdatePackage(UdpClientSocket client,String macCode){

    }

    //每次发送0x72的udp包,动态生成
    public static String hardWarePackage(){

        return null;
    }

    //发送数据
    public static void sendMsg(String ip, String msg){
        try {
            UdpClientSocket client = new UdpClientSocket();
            client.send(ip, Config.UDP_CLIENT_PORT,Msg.toByteArr(msg));
            client.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
