package com.johnny.udp;

import com.johnny.udp.cache.MsgCache;
import com.johnny.utils.CRC16;
import com.johnny.utils.HexUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class MessageSender {
    private static Logger logger = LoggerFactory.getLogger(MessageSender.class);

    public final static String PROGRAM_CODE = "ffffffff"; //工程号

    public final static String TARGET_MODULE_CODE = "00";//目标模块

    public final static String TARGET_MAC_CODE = "01";

    public final static String SOURCE_MAC_CODE = "00";

    public final static String ASK = "01";

    public final static String EMPTY_SEQ = "0000";//无需响应的指令

    private static String getHeader(int seq, String macCode, int cmd) {
        String seqStr = seq <= 0 ? EMPTY_SEQ : HexUtil.toHexString(seq, 4);

        String header = Config.UDP_HEADER_CODE //TXPARK
                + TARGET_MAC_CODE//目标设备
                + SOURCE_MAC_CODE//原设备
                + seqStr//序列号
                + PROGRAM_CODE//工程号
                + TARGET_MODULE_CODE//目标模块
                + HexUtil.toHexString(cmd, 4)//指令编码
                + ASK//ASK
                + HexUtil.toHexString(Long.parseLong(macCode), 8);//机器码
        return header;
    }

    private static String toMsg(String header, String content) {
        StringBuffer result = new StringBuffer();
        int contentLength = content.length() / 2 + 1; //存储长度的1位byte
        int total = header.length() / 2 + contentLength + 2 + 2;// crc两位byte  头长度2位byte
        String totalStr = HexUtil.toHexString(total, 4);
        result.append(totalStr + totalStr);
        result.append(header);//头部 0-23
        result.append(HexUtil.toHexString(contentLength, 4));//记录长度
        result.append(content);
        int crc = CRC16.ccr16(result.toString());
        result.append(HexUtil.toHexString(crc, 4));
        return result.toString();
    }

    /**
     * 发送消息
     */
    public static void sendMsg(String ip, int port, String msg) {
        try {
            UdpClientSocket client = new UdpClientSocket();
            client.send(ip, port, Msg.toByteArr(msg));
            client.close();
        } catch (Exception e) {
            System.out.println("指令发送失败    IP" + ip + " 信息内容：" + msg);
            e.printStackTrace();
        }
    }

    /**
     * 发送消息，失败重发
     *
     * @param ip
     * @param msg
     * @param macCode
     * @param seq
     * @param cmd
     */
    public static void sendMsg(String ip, int port,String msg, String macCode, int seq, int cmd) {
        if (seq > 0) {
            Msg m = new Msg(macCode, seq, cmd, msg);
            MsgCache.putMsg(macCode, seq, m);
        }
        sendMsg(ip, port,msg);
    }

    public static void sendUnresponseMsg(Msg msg, String ip, int port) {
        UdpClientSocket client = null;
        try {
            client = new UdpClientSocket();
            client.send(ip, Config.UDP_CLIENT_PORT, msg.toByte());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }

    /**
     *          HexUtil示例:
     *          设备重连间隔
     *  		sb.append(HexUtil.toHexString(30,2));
     *   		服务器ip
     *   		sb.append(HexUtil.toIpHexStr(ip));
     *   		默认端口
     *   		sb.append(HexUtil.toHexString(3341,4));
     */

    /**
     * 发送获取固件版本号
     * 0x71
     */
    public static void sendPanelStatusMsg(String macCode, String ip,int port) {
        String header = getHeader(0, macCode, Code.GET_MAC_VERSION);
        StringBuffer sb = new StringBuffer("");
        String content = sb.toString();
        content = toMsg(header, content);
        try {
            MessageSender.sendMsg(ip, port,content);
            logger.info("获取固件版本={}", content);
        } catch (Exception e) {
            logger.error("发送0x71指令出错了={}", e.getMessage());
        }
    }

    /**
     * 发送使设备绑定到服务端
     * 0x01
     */
    public static void sendBindDevice(String macCode, String deviceIp, String deviceBroadcast) {
        String header = getHeader(0, macCode, Code.IP_BIND);
        StringBuffer sb = new StringBuffer("");
        //设备指定ip
        sb.append(HexUtil.toIpHexStr(deviceIp));
        //设备指定网关
        sb.append(HexUtil.toIpHexStr(deviceBroadcast));
        String content = sb.toString();
        content = toMsg(header, content);
        //广播ip
        String broadcastIp = deviceBroadcast.substring(0, deviceBroadcast.lastIndexOf(".")) + ".255";
        try {
            MessageSender.sendMsg(broadcastIp, 3341,content);
            logger.info("绑定ip={}", content);
        } catch (Exception e) {
            logger.error("发送0x01指令出错了={}", e.getMessage());
        }
    }

    /**
     * 回复设备重启0x70,使之进入烧写模式
     * 0x70
     */
    public static void sendMacResetShake(String macCode, String ip,int port) {
        String header = getHeader(0, macCode, Code.MAC_RESET_SHAKE);
        StringBuffer sb = new StringBuffer("");
        String content = sb.toString();
        content = toMsg(header, content);
        try {
            MessageSender.sendMsg(ip, port,content);
            logger.info("回复硬件重启复位={}", content);
        } catch (Exception e) {
            logger.error("发送0x70指令出错了={}", e.getMessage());
        }
    }

    /**
     * 固件升级启动,是硬件重启复位进入烧写模式
     * 0x75
     */
    public static void sendFirmwareUpgradeStart(String macCode, String ip,int port) {
        String header = getHeader(0, macCode, Code.MAC_FIRMWARE_UPGRADE_START);
        StringBuffer sb = new StringBuffer("");
        String content = sb.toString();
        content = toMsg(header, content);
        try {
            MessageSender.sendMsg(ip, port,content);
            logger.info("固件升级启动,使硬件进入重启复位={}", content);
        } catch (Exception e) {
            logger.error("发送0x75指令出错了={}", e.getMessage());
        }
    }

    /**
     * 固件升级结束指令
     * 0x76
     */
    public static void sendFirmwareUpgradeEnd(String macCode, String ip,int port) {
        String header = getHeader(0, macCode, Code.MAC_FIRMWARE_UPGRADE_END);
        StringBuffer sb = new StringBuffer("");
        String content = sb.toString();
        content = toMsg(header, content);
        try {
            MessageSender.sendMsg(ip, port,content);
            logger.info("固件升级结束发送指令={}", content);
        } catch (Exception e) {
            logger.error("发送0x76指令出错了={}", e.getMessage());
        }
    }

    /**
     * 发送使硬件进入应用模式
     * 0x77
     */
    public static void sendMacToWork(String macCode, String ip,int port) {
        String header = getHeader(0, macCode, Code.MAC_TO_WORK);
        StringBuffer sb = new StringBuffer("");
        String content = sb.toString();
        content = toMsg(header, content);
        try {
            MessageSender.sendMsg(ip, port,content);
            logger.info("已发送硬件进入应用模式指令={}", content);
        } catch (Exception e) {
            logger.error("发送0x77指令出错了={}", e.getMessage());
        }
    }

    /**
     * 更改硬件机器码
     * 0x79
     */
    public static void sendMacToWork(String macCode, String ip, String destMacCode,int port) {
        String header = getHeader(0, macCode, Code.MAC_CODE_MODIFY);
        StringBuffer sb = new StringBuffer("");
        sb.append(HexUtil.toIpHexStr(destMacCode));
        String content = sb.toString();
        content = toMsg(header, content);
        try {
            MessageSender.sendMsg(ip, port,content);
            logger.info("已发送指令={}", content);
        } catch (Exception e) {
            logger.error("发送0x79指令出错了={}", e.getMessage());
        }
    }

    /**
     * 固件升级
     * 0x72
     */
    public static void sendLogicTableRowDataPC(int seq,String macCode, String ip, int port, String s) {
        String header = getHeader(seq, macCode, Code.MAC_FIRMWARE_UPGRADE);
        StringBuffer sb = new StringBuffer("");
        //固件数据部分
        sb.append(s);
        String content = new String(sb);
        content = toMsg(header, content);
        sendMsg(ip, port,content, macCode, 0, Code.MAC_FIRMWARE_UPGRADE);
    }
    /**
     * 发送第一条固件升级指令
     * 0x72
     */
    public static void sendFirstFirmwareUpgrade(String macCode, String ip, int port, String s){
        String header = getHeader(0, macCode, Code.MAC_FIRMWARE_UPGRADE);
        StringBuffer sb = new StringBuffer("");
        //固件数据部分
        sb.append(s);
        String content = new String(sb);
        content = toMsg(header, content);
        sendMsg(ip, port,content, macCode, 0, Code.MAC_FIRMWARE_UPGRADE);
    }


}
