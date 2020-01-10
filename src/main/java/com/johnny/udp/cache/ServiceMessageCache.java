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
    public static Logger logger = LoggerFactory.getLogger(ServiceMessageCache.class);
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
     * mac与模式映射关系, 烧写模式 1, 应用模式 2, 等待模式 0
     * 1.接收到7f指令表示该mac处于烧写模式
     * 2.接收到0x30,0x88表示该mac处于应用模式
     * 3.其余等待模式
     */
    public static Map<Long, Integer> MacModeMap = new ConcurrentHashMap<Long,Integer>();
    /**
     * 更新mac与模式对应关系
     */
    public static void refreshMacMode(String msg,int mode){
        if(msg.length()<48)return;
        String code =  msg.substring(40,48);
        Long mac =  Long.parseLong(code,16);
        if(MacModeMap.containsKey(mac)){
            MacModeMap.remove(mac);
            MacModeMap.put(mac,mode);
            logger.info("更新硬件模式,机器码:"+mac+",Mode:"+mode);
        }else{
            //设备默认为等待状态
            MacModeMap.put(mac,0);
            logger.info("新增硬件模式,机器码:"+mac+",默认等待模式,等待硬件信息");
        }
    }
    /**
     * 更新mac与ip端口对应关系
     */
    public static void RefreshIPPort(String msg,String ip,int port){
        if(msg.length()<48)return;
        String code =  msg.substring(40,48);
        Long mac =  Long.parseLong(code,16);
        int cmd = getCommand(msg);
        if(MapIPPort.containsKey(mac)){
            MapIPPort.remove(mac);
            MapIPPort.put(mac, ip+"#"+port);
            logger.info("***更新设备Ip端口信息***机器码:"+mac+",Ip:"+ip+",port:"+port);
            if(cmd==0x7f){
                MacModeMap.remove(mac);
                MacModeMap.put(mac,1);
                logger.info("更新硬件模式,机器码:"+mac+",Mode:烧写模式");
            }else if(cmd==0x30 || cmd==0x88){
                MacModeMap.remove(mac);
                MacModeMap.put(mac,2);
                logger.info("更新硬件模式,机器码:"+mac+",Mode:应用模式");
            }
        }else{
            MapIPPort.put(mac, ip+"#"+port);
            logger.info("---新增设备Ip端口信息---机器码:"+mac+",Ip:"+ip+",port:"+port);
            MacModeMap.put(mac,0);
            logger.info("新增硬件模式,机器码:"+mac+",默认为等待模式,直到接收到指定硬件信息");
        }
    }
    /**
     * 获取硬件模式
     */
    public static int getModeByMachine(String machineCode){
        Long mac = Long.parseLong(machineCode);
        if(MacModeMap.containsKey(mac)){
            int mode = MacModeMap.get(mac);
            return mode;
        }else{
            return -1;
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
