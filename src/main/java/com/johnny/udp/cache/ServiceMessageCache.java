package com.johnny.udp.cache;

import com.johnny.udp.Code;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        if(msg.length()>40){
            String cmd =  msg.substring(34,38);
            int intCmd = Integer.parseInt(cmd, 16);
            return intCmd;
        }
        return -1;
    }
    /**
     * 初始化MAC对应IP
     */
    public static void InitMacIP()
    {
        Map map = new HashMap();
        map.put("macCode","");
    }
    /**
     * 初始化1
     */
    public static long currentSave_Index=1;
    /**
     * mac与ip端口映射关系
     */
    public static Map<Long, String> MapIPPort = new ConcurrentHashMap<Long, String>();
    /**
     * 更新mac与ip端口对应关系
     */
    public static void RefreshIPPort(String msg,String ip,int port){
        if(msg.length()<48)return;
        String code =  msg.substring(40,48);
        Long mac =  Long.parseLong(code,16);
        if(MapIPPort.containsKey(mac)){
            MapIPPort.remove(mac);
            MapIPPort.put(mac, ip+"#"+port);
            System.out.println("更新设备信息,mac:"+mac+",ip:"+ip+",port:"+port);
        }else{
            MapIPPort.put(mac, ip+"#"+port);
            System.out.println("新增设备信息,mac:"+mac+",ip:"+ip+",port:"+port);
        }
    }

    /**
     * 获取IP
     * String ip = ServiceMessageCache.getIpByMac(map.get("machineCode").toString());
     */
    public static String getIpByMac(String mac){
        Long long1 = Long.parseLong(mac);
        return getIp(long1);
    }
    public static String getIp(Long mac){
        if(MapIPPort.containsKey(mac)){
            String ipport = MapIPPort.get(mac);
            if(ipport.indexOf("#")>0)
            {
                return ipport.split("#")[0];
            }
        }
        return "";
    }
    /**
     * 获取port
     * int port = ServiceMessageCache.getPort(Long.parseLong(map.get("machineCode").toString()));
     */
    public static int getPort(long mac){
        if(MapIPPort.containsKey(mac)){
            String ipport = MapIPPort.get(mac);
            if(ipport.indexOf("#")>0)
            {
                return Integer.parseInt(ipport.split("#")[1]);
            }
        }
        return 0;
    }
    /**
     * 固件升级数据部分
     */
    public static List<String> firmwareDataList = new ArrayList<>();
}
