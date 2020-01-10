package com.johnny.udp.cache;

import com.johnny.udp.Msg;
import com.johnny.udp.ResponseMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MsgCache {
    public static String OldLogicMsg="";
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    /**
     * 等待终端响应服务器的消息列表
     * key : 终端编码 + "_" + 报文号
     */
    public static final Map<String, Msg> unResponseMsg = new ConcurrentHashMap<String,Msg>();

    /**
     * 服务端已响应终端消息
     * key : 终端编码 + "_" + 报文号
     */
    public static final  Map<String, ResponseMsg> responseMsg = new ConcurrentHashMap<String, ResponseMsg>();
    public static void removeMsg(String macCode,int seq,int cmd){

        Msg msg = unResponseMsg.get(macCode + "_" + seq);

        if(msg == null){
            return;
        }

        if(msg.cmd == cmd){
            msg.success = true;
//			msg.deleteTime = new Date().getTime() + 120 * 1000;	//2分钟后删除

            msg.deleteTime = new Date().getTime() + 1000;	//20170824 修改为 1s 删除

        }
    }

    public static void remove(Msg msg){
        unResponseMsg.remove(msg.macCode + "_" + msg.seq);
    }

    public static void putMsg(String macCode,int seq,Msg msg){
        unResponseMsg.put(macCode + "_" + seq,msg);
    }

    /*以下变量用来存储发送的数据 总数 成功 失败 进度条 信息*/
    /**
     * 是否强制终止发送 1强制终止 2正常终止 0开始
     */
    public static int sendToStop=0;

    public static long upMsgTime =0;

    /**
     * 总发送数量
     */
    public static int sendCount=0;

    /**
     * 现在发送主机的序号
     */
    public static int sendCurrentIndex=0;
    /**
     * 发送成功数量
     */
    public static int sendSuccessCount=0;
    /**
     * 发送失败数量
     */
    public static int sendFailCount=0;
    /**
     * 发送进度
     */
    public static int sendProgressBar =0;
    /**
     * 发送未读取的信息
     */
    public static Map<Integer,String> sendMsg = new ConcurrentHashMap<Integer,String>();

    /**
     * 发送的房号
     */
    public static String sendRoomNo="";
    /**
     * 总的输入的序号 一路两输入
     */
    public static int sendInputNo=0;
    /**
     * 输入的模块
     */
    public static Long sendModuleNo=0l;
    /**
     * 记录房型需要发送的模块 输入数量 模块名称 当前房间已经处理的模块
     */
    public static Map<Long,Integer> sendAddInput = new ConcurrentHashMap<Long, Integer>();
    public static Map<Long, String> sendModuleName = new ConcurrentHashMap<Long,String>();
    public static List<Long> sendedModule = new ArrayList<Long>();

    public static long LogicTableVersion=0;
    /**
     * 主机主动发送的时候 使用的当前Index， 具体模块中的索引
     */
    public static int sendPCCurrentIndex=0;
    public static void ThreadSleep(long time)
    {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 需要发送的信息最大索引 从0开始
     */
    public static int sendMsgIndex=0;
    /**
     * 已经发送的信息索引 从0开始
     */
    public static int sendedIndex=0;
    /**
     * 开始进度条 0默认值/结束 1开始 2数据异常 3报错终止 4房间异常
     */
    public static int progressState = 0;

    public static void AddSendMsg(String msg)
    {
        if(sendMsgIndex==0)
        {
            sendMsg = new ConcurrentHashMap<Integer,String>();
        }
        sendMsg.put(sendMsgIndex++, msg);
        //System.out.println(sendMsgIndex + "  "+ msg);
    }

    public static void AddSendMsg(String msg,int bar)
    {
        AddSendMsg(msg);
        if(sendProgressBar>bar)return;
        sendProgressBar = bar;
    }

    /**
     * 发送的所有输入口数量
     */
    public static int sendInputAllCount=0;

    /**
     * 0被动发送 1主动发送
     */
    public static int sendType=0;
    /***
     * 0 开始  1结束
     */
    public static int isEnd = 0;
    /**
     * 发送的固件校验值,十进制
     */
    public static Long firmwareCRC32=0L;

    /**
     * 发送的固件起始地址
     */
    public static String firmwareStartAddr="00800008";
    /**
     * 发送的固件起始地址
     */
    public static String firmwareStartAddrInfo="e0ff0300";

    /**
     * 发送固件数据长度,十进制
     */
    public static int firmwareLength = 0;

    /**
     * 停止固件升级
     */
    public static int stopFirmwareUpgrade = 0;

    /**
     * 单独监听70指令的设备
     */
    public static String machineCodeTo70 = "";

    /**
     * 升级完成标识位 升级成功:1
     */
    public static int successUpgrade = 0;


    public static void clearSend(){
        stopFirmwareUpgrade = 0;
        sendToStop=0;
        sendCount=0;
        sendSuccessCount=0;
        sendFailCount=0;
        sendProgressBar=0;
        sendMsg.clear();
        sendRoomNo="";
        //sendInputNoModule=0;
        sendInputNo=0;
        sendModuleNo=0l;
        if(sendAddInput!=null){
            sendAddInput.clear();
            sendModuleName.clear();
            sendedModule.clear();
        }
        sendMsgIndex=0;
        sendedIndex=0;
        progressState=0;
        sendInputAllCount=0;
        sendCurrentIndex=0;
    }
}
