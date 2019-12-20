package com.johnny.udp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageCallService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public synchronized void read(String msg,UdpClientSocket client,Long index) {
        //LogAPI.GetInstance().Info(index + "监测数据:" +msg);
        if (!checkMsg(msg)) {
            logger.info("消息格式不正确:{}"+msg);
            return;
        }
        int cmd = getCommand(msg);

        switch (cmd) {
            case Code.GET_MAC_VERSION:
                getMacVersion(msg);
                break;
            case Code.MAC_CRC_CHECK:
                sendMacCRCCheck(msg);
                break;
            case Code.MAC_FIRMWARE_ENCRYPTION:
                sendMacFirmwareEncryption(msg);
                break;
            case Code.MAC_FIRMWARE_UPGRADE:
                sendMacFirmwareUpgrade(msg);
                break;
            case Code.MAC_FIRMWARE_UPGRADE_END:
                sendMacFirmwareUpgradeEnd(msg);
                break;
            case Code.MAC_FIRMWARE_UPGRADE_START:
                sendMacFirmwareUpgradeStart(msg);
                break;
            case Code.MAC_READY_SHAKE:
                getMacReadyShake(msg);
                break;
            case Code.MAC_RESET_SHAKE:
                sendMacResetShake(msg);
                break;
            case Code.MAC_TO_WORK:
                sendMacToWork(msg);
                break;
            case Code.MAC_CODE_MODIFY:
                sendMacCodeModify(msg);
                break;
            default:
                logger.info("未处理的数据={}"+msg);
                break;
        }
    }

    private void sendMacCodeModify(String msg) {

    }

    private void sendMacToWork(String msg) {

    }

    private void sendMacResetShake(String msg) {

    }

    private void getMacReadyShake(String msg) {

    }

    private void sendMacFirmwareUpgradeStart(String msg) {

    }

    private void sendMacFirmwareUpgradeEnd(String msg) {

    }

    private void sendMacFirmwareUpgrade(String msg) {

    }

    private void sendMacCRCCheck(String msg) {

    }

    private void sendMacFirmwareEncryption(String msg) {

    }

    private void getMacVersion(String msg) {

    }


    private boolean checkMsg(String msg){
        if(msg.length() < 50){
            return false;
        }
        String txpark = msg.substring(4,16);
        return Config.UDP_HEADER_CODE.equals(txpark);
    }

    /**
     * 17-18
     * 获取指令编码
     * @return  //返回值为10进制数
     */
    private  static int getCommand(String msg){
        String cmd =  msg.substring(34,38);
        int intCmd = Integer.parseInt(cmd, 16);
        //LogAPI.GetInstance().Info("获取到的16进制："+cmd+"  10进制："+intCmd);
        return intCmd;
    }
}
