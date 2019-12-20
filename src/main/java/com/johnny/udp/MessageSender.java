package com.johnny.udp;

import com.johnny.utils.CRC16;
import com.johnny.utils.HexUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageSender {
    private static Logger logger = LoggerFactory.getLogger(MessageSender.class);

    public final static String PROGRAM_CODE = "ffffffff"; //工程号

    public final static String TARGET_MODULE_CODE = "00";//目标模块

    public final static String TARGET_MAC_CODE = "01";

    public final static String SOURCE_MAC_CODE = "00";

    public final static String ASK = "01";

    public final static String EMPTY_SEQ = "0000";//无需响应的指令

    private static String getHeader(int seq,String macCode,int cmd){
        String seqStr = seq <= 0 ? EMPTY_SEQ : HexUtil.toHexString(seq, 4);

        String header = Config.UDP_HEADER_CODE //TXPARK
                +TARGET_MAC_CODE//目标设备
                +SOURCE_MAC_CODE//原设备
                +seqStr//序列号
                +PROGRAM_CODE//工程号
                +TARGET_MODULE_CODE//目标模块
                +HexUtil.toHexString(cmd,4)//指令编码
                +ASK//ASK
                +HexUtil.toHexString(Long.parseLong(macCode),8);//机器码
        return header;
    }

    private static String toMsg(String header,String content){
        StringBuffer result = new StringBuffer();
        int contentLength = content.length() / 2 + 1; //存储长度的1位byte
        int total = header.length() / 2  + contentLength  + 2  + 2;// crc两位byte  头长度2位byte
        String totalStr = HexUtil.toHexString(total, 2);
        result.append(totalStr+totalStr);
        result.append(header);//头部 0-23
        result.append(HexUtil.toHexString(contentLength, 2));//记录长度
        result.append(content);
        int crc = CRC16.ccr16(result.toString());
        result.append(HexUtil.toHexString(crc,	 4));
        return result.toString();
    }

    /**
     * 发送消息
     */
    public static void sendMsg(String ip,String msg){
        try {
            UdpClientSocket client = new UdpClientSocket();
            client.send(ip, Config.UDP_CLIENT_PORT,Msg.toByteArr(msg));
            client.close();
        } catch (Exception e) {
            System.out.println("指令发送失败    IP"+ ip +" 信息内容："+ msg);
            e.printStackTrace();
        }
    }

    public static void sendUnresponseMsg(Msg msg,String ip,int port){
        UdpClientSocket client = null;
        try {
            client = new UdpClientSocket();
            client.send(ip, Config.UDP_CLIENT_PORT,msg.toByte());
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            if(client != null){
                client.close();
            }
        }
    }

    /**
     * 获取固件版本号
     */
    public static String getPanelStatusMsg(String macCode){
        String header = getHeader(0,macCode,Code.GET_MAC_VERSION);
        StringBuffer sb = new StringBuffer("");
        String content =sb.toString();
        content = toMsg(header,content);
        return content;
    }
    /**
     * 获取固件版本号
     */
    public static void sendPanelStatusMsg(String macCode,String ip){
        String msg = getPanelStatusMsg(macCode);
        System.out.println(msg);
        try {
            MessageSender.sendMsg(ip, msg);
            logger.info("获取固件版本={}"+msg);
        } catch (Exception e) {
            logger.error("获取固件版本={}"+e.getMessage());
        }
    }
}
