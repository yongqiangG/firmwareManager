package com.johnny.udp.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.johnny.udp.Code;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceMessageCache {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    /**
     * 等待处理的服务器服务信息列表
     * key : 自增长的长整形
     * value ：带处理的报文号
     */
    public static final Map<Long,String> unSaveServiceMsg = new ConcurrentHashMap<Long,String>();

    /**
     * 新增的最大值
     */
    public static long serviceIndex_Add = 0;

    /**
     * 需要转移到此处理机制的 Code直接添加即可
     * 在MessageCallService中需要添加相应的处理方法，不然会被遗漏处理
     * @param msg
     */
    public static void AddServiceMsg(String msg)
    {
        int cmd = getCommand(msg);
        switch (cmd) {
            case Code.GET_MAC_VERSION:
            case Code.MAC_CRC_CHECK:
            case Code.MAC_FIRMWARE_ENCRYPTION:
            case Code.MAC_FIRMWARE_UPGRADE:
            case Code.MAC_FIRMWARE_UPGRADE_END:
            case Code.MAC_FIRMWARE_UPGRADE_START:
            case Code.MAC_READY_SHAKE:
            case Code.MAC_RESET_SHAKE:
            case Code.MAC_TO_WORK:
            case Code.MAC_CODE_MODIFY:
                serviceIndex_Add = serviceIndex_Add+1;
                unSaveServiceMsg.put(serviceIndex_Add,msg);
                break;
            default:
                break;
        }
    }

    //返回16进制指令数字
    private static int getCommand(String msg){
        String cmd =  msg.substring(34,38);
        int intCmd = Integer.parseInt(cmd, 16);
        return intCmd;
    }
}
