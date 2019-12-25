package com.johnny.udp;

import com.johnny.udp.cache.MsgCache;
import com.johnny.utils.FirmwareUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Date;
import java.util.List;

public class MessageCallService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public synchronized void read(String msg, UdpClientSocket client, Long index) {
        //LogAPI.GetInstance().Info(index + "监测数据:" +msg);
        if (!checkMsg(msg)) {
            logger.info("消息格式不正确={}", msg);
            return;
        }
        int cmd = getCommand(msg);
        logger.info("硬件指令={}", cmd);
        switch (cmd) {
            case Code.GET_MAC_VERSION:
                //0x71
                getMacVersion(msg);
                break;
            case Code.MAC_CRC_CHECK:
                //0x73
                sendMacCRCCheck(msg);
                break;
            case Code.MAC_FIRMWARE_ENCRYPTION:
                //0x74
                sendMacFirmwareEncryption(msg);
                break;
            case Code.MAC_FIRMWARE_UPGRADE:
                ////0x72
                sendMacFirmwareUpgrade(msg);
                break;
            case Code.MAC_FIRMWARE_UPGRADE_END:
                //0x76
                sendMacFirmwareUpgradeEnd(msg);
                break;
            case Code.MAC_FIRMWARE_UPGRADE_START:
                //0x75
                sendMacFirmwareUpgradeStart(msg);
                break;
            case Code.MAC_READY_SHAKE:
                //0x7F
                getMacReadyShake(msg);
                break;
            case Code.MAC_RESET_SHAKE:
                //0x70
                sendMacResetShake(msg, client);
                break;
            case Code.MAC_TO_WORK:
                //0x71
                sendMacToWork(msg);
                break;
            case Code.MAC_CODE_MODIFY:
                //0x79
                sendMacCodeModify(msg);
                break;
            default:
                logger.info("未处理的数据={}", msg);
                break;
        }
    }

    private void sendMacCodeModify(String msg) {

    }

    private void sendMacToWork(String msg) {

    }

    private void sendMacResetShake(String msg, UdpClientSocket client) {
        //接收到主机复位重启的0x70指令
        //获取设备机器码
        String macCode = getMacCode(msg);
    }

    private void getMacReadyShake(String msg) {

    }

    private void sendMacFirmwareUpgradeStart(String msg) {

    }

    private void sendMacFirmwareUpgradeEnd(String msg) {

    }

    private void sendMacFirmwareUpgrade(String msg) {
        logger.info("接收到72返回,将标志位置1");
        MsgCache.sendPCCurrentIndex = 1;
        new LogicMsgPCSenderThread(msg).start();
    }

    private void sendMacCRCCheck(String msg) {

    }

    private void sendMacFirmwareEncryption(String msg) {

    }

    private void getMacVersion(String msg) {

    }


    private boolean checkMsg(String msg) {
        if (msg.length() < 50) {
            return false;
        }
        String txpark = msg.substring(4, 16);
        return Config.UDP_HEADER_CODE.equals(txpark);
    }

    /**
     * 17-18
     * 获取指令编码
     *
     * @return //返回值为10进制数
     */
    private static int getCommand(String msg) {
        String cmd = msg.substring(34, 38);
        int intCmd = Integer.parseInt(cmd, 16);
        //LogAPI.GetInstance().Info("获取到的16进制："+cmd+"  10进制："+intCmd);
        return intCmd;
    }

    /**
     * 20-23
     * 获取机器码
     */
    private static String getMacCode(String msg) {
        String code = msg.substring(40, 48);
        return Long.parseLong(code, 16) + "";
    }


    private static String getMsgContent(String msg) {
        return msg.substring(50, msg.length() - 4);
    }
}

/**
 * 主动发送固件升级,判断RCU主机是否在规定时间内回复  2019-12-25
 *
 * @author Administrator Johnny
 */
class LogicMsgPCSenderThread extends Thread {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private String msg;

    public LogicMsgPCSenderThread(String msg) {
        this.msg = msg;
    }


    /**
     * 判断RCU回复 每50毫秒监测一次，监测到之前持续1秒钟
     *
     * @return
     */
    private static boolean checkRCUback() {
        int waitCount = 0;
        while (true) {
            if (waitCount == 4) return false;
            if (MsgCache.sendPCCurrentIndex == 1) {
                return true;
            }
            MsgCache.upMsgTime = new Date().getTime();
            MsgCache.ThreadSleep(1000);//每1000毫秒监测是否恢复信息啦
            waitCount++;
        }
    }

    public void run() {
        String macCode = Long.parseLong(msg.substring(40, 48), 16) + "";
        String ip="192.168.1.50";
        int port=3341;
        List list = FirmwareUtil.getLines(new File("E:\\johnny\\TK3100.hex"));
        List list1 = FirmwareUtil.transToList(list);
        List<String> list2 = FirmwareUtil.TransToUdp(list1);
        int sendErrorCount = 0;
        for(int i=0;i<list2.size();i++){
            String s = list2.get(i);
            while(true){
                if(sendErrorCount>3){
                    //重发四次无响应
                    return;
                }
                MessageSender.sendLogicTableRowDataPC(macCode,ip,port,s);
                MsgCache.sendPCCurrentIndex=1;
                logger.info("已发送第"+i+"条固件数据,将标志位置0:"+s);
                if (checkRCUback()) {
                    break;
                }
                sendErrorCount++;
            }
        }
        //发送结束指令
        MessageSender.sendFirmwareUpgradeEnd(macCode,ip);
    }
}
