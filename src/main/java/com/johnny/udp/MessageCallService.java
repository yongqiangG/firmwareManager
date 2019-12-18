package com.johnny.udp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//服务端数据监听
public class MessageCallService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public synchronized void read(String msg, UdpClientSocket client) {

        //提取指令编号
        int cmd = getCommand(msg);

        switch (cmd) {
            //硬件复位启动握手置零
            case Code.HARDWARE_RESET:
                hardWareReset(msg,client);
                break;
            //固件升级指令,0x72
            case Code.UPDATE_HARDWARE_BY_PACKAGE:
                responseUpdatePackage(msg, client);
                break;
            //固件升级开始指令
            case Code.UPDATE_HARDWARE_START:
                startUpdateHardWare(msg, client);
                break;
            //固件升级结束指令
            case Code.UPDATE_HARDWARE_END:
                endUpdateHardware(msg, client);
                break;
            default:
                logger.info("未处理的udp指令: " + msg);
                break;
        }
    }

    //硬件重启复位指令
    private synchronized void hardWareReset(String msg,UdpClientSocket client){

        String ip = null;
        String responseMsg = null;
        MessageSender.sendMsg(ip,msg);
    }
    //固件升级指令响应,0x72
    private synchronized void responseUpdatePackage(String msg, UdpClientSocket client) {
        String macCode = getMacCode(msg);
        if(isSuccess(msg)){
            //设备回复成功

        }
    }
    //固件升级结束指令通知设备
    private synchronized void startUpdateHardWare(String msg, UdpClientSocket client) {

    }
    //固件升级结束指令通知设备
    private synchronized void endUpdateHardware(String msg, UdpClientSocket client) {

    }

    /**
     * 17-18字节
     * 获取指令编码,返回值为10进制数字
     */
    private static int getCommand(String msg) {
        String cmd = msg.substring(34, 38);
        int intCmd = Integer.parseInt(cmd, 16);
        return intCmd;
    }
    /**
     * 20-23字节
     * 获取设备机器码
     */
    private static String getMacCode(String msg){
        String code =  msg.substring(40,48);
        return  Long.parseLong(code,16)+"";
    }
    /**
     * 判断0x72指令回复是否成功
     */
    private static boolean isSuccess(String msg){
        String flag = msg.substring(50,60);
        if("5245414459".equals(flag)){
            return true;
        }
        return false;
    }
}
