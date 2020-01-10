package com.johnny.controller;

import com.johnny.dto.FirmResult;
import com.johnny.entity.Machine;
import com.johnny.udp.MessageSender;
import com.johnny.udp.cache.MsgCache;
import com.johnny.udp.cache.ServiceMessageCache;
import com.johnny.utils.FirmwareUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.*;

@Controller
@RequestMapping(value = "/firmware")
public class FirmwareController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(value = "/getIpAndPort")
    @ResponseBody
    public FirmResult getIpAndPort(HttpServletRequest req){
        String machineCode = req.getParameter("machineCode");
        logger.info("getIpAndPort()获取到机器码={}",machineCode);
        FirmResult fr;
        String ip = ServiceMessageCache.getIpByMac(machineCode);
        int port = ServiceMessageCache.getPort(Long.parseLong(machineCode));
        if(ip=="" || port==0){
            fr=new FirmResult(false,"未获取到该设备的IP和端口,请检查网络后在尝试");
            return fr;
        }
        Map<String,Object> m = new HashMap<String,Object>();
        m.put("machineIp",ip);
        m.put("machinePort",port);
        fr = new FirmResult(true,m);
        return fr;
    }

    @RequestMapping("/getMachineMode")
    @ResponseBody
    public FirmResult getMachineMode(HttpServletRequest req){
        String machineCode = req.getParameter("machineCode");
        logger.info("getIpAndPort()获取到机器码={}",machineCode);
        FirmResult fr;
        Map<String,Object> m = new HashMap<String,Object>();
        int machineMode = ServiceMessageCache.getModeByMachine(machineCode);
        m.put("machineMode",machineMode);
        fr = new FirmResult(true,m);
        return fr;
    }


    @RequestMapping(value = "/intoUpgradeMode")
    @ResponseBody
    public FirmResult intoUpgradeMode(HttpServletRequest req){
        String machineCode = req.getParameter("machineCode");
        logger.info("intoUpgradeMode()获取到机器码={}",machineCode);
        FirmResult fr;
        String ip = ServiceMessageCache.getIpByMac(machineCode);
        int port = ServiceMessageCache.getPort(Long.parseLong(machineCode));
        if(ip=="" || port==0){
            fr=new FirmResult(false,"未获取到该设备的IP和端口,请检查网络后在尝试");
            return fr;
        }
        //指定检测70指令的机器码
        MsgCache.machineCodeTo70 = machineCode;
        //发送0x75指令使得主机复位重启
        MessageSender.sendFirmwareUpgradeStart(machineCode,ip,port);
        fr = new FirmResult(true);
        return fr;
    }

    @RequestMapping(value = "/intoWorkMode")
    @ResponseBody
    public FirmResult intoWorkMode(HttpServletRequest req){
        String machineCode = req.getParameter("machineCode");
        logger.info("intoWorkMode()获取到机器码={}",machineCode);
        FirmResult fr;
        String ip = ServiceMessageCache.getIpByMac(machineCode);
        int port = ServiceMessageCache.getPort(Long.parseLong(machineCode));
        if(ip=="" || port==0){
            fr=new FirmResult(false,"未获取到该设备的IP和端口,请检查网络后在尝试");
            return fr;
        }
        //发送0x77指令使得主机复位重启
        MessageSender.sendMacToWork(machineCode,ip,port);
        fr = new FirmResult(true);
        return fr;
    }

    @RequestMapping(value = "/upload")
    @ResponseBody
    public FirmResult upload(HttpServletRequest req, MultipartFile firmwareFile, HttpSession session){
        String machineCode = req.getParameter("machineCode");
        logger.info("upload()获取到机器码={}",machineCode);
        FirmResult fr;
        String ip = ServiceMessageCache.getIpByMac(machineCode);
        int port = ServiceMessageCache.getPort(Long.parseLong(machineCode));
        /*if(ip=="" || port==0){
            fr=new FirmResult(false,"未获取到该设备的IP和端口,请检查网络后在尝试");
            return fr;
        }*/
        if(firmwareFile==null){
            fr=new FirmResult(false,"未检测到文件,请确认是否已上传文件");
            return fr;
        }
        //获取文件名称
        String fileName = firmwareFile.getOriginalFilename();
        if(fileName.endsWith("hex")){
            logger.info("已获取到上传文件={}",fileName);
            //保存固件到服务器
            String path="D:/uploadFirmware/"+new Date().getTime()+"_"+firmwareFile.getOriginalFilename();
            File newFile=new File(path);
            if (!newFile.getParentFile().exists()) {
                boolean result = newFile.getParentFile().mkdirs();
                if (!result) {
                    fr=new FirmResult(false,"保存文件时出现异常,请重新上传");
                    return fr;
                }
            }
            try {
                firmwareFile.transferTo(newFile);
                List list = FirmwareUtil.getLines(newFile);
                List list1 = FirmwareUtil.transToList(list);
                ServiceMessageCache.firmwareDataList = FirmwareUtil.TransToUdp(list1);
                //发送第一条固件升级指令 0x72
                String firstMessage = ServiceMessageCache.firmwareDataList.get(0);
                MsgCache.clearSend();
                MsgCache.successUpgrade = 0;
                MessageSender.sendFirstFirmwareUpgrade(machineCode,ip,port,firstMessage);
                Map<String,Object> m = new HashMap<>();
                m.put("firmwareCount",ServiceMessageCache.firmwareDataList.size());
                fr=new FirmResult(true,m);
                return fr;
            } catch (IOException e) {
                logger.error(e.getMessage());
                fr=new FirmResult(false,"保存文件时出现异常,请重新上传");
                return fr;
            }
        }else{
            fr=new FirmResult(false,"文件类型不合法,请上传hex类型文件");
            return fr;
        }
    }

    @RequestMapping(value = "/getProgressbar")
    @ResponseBody
    public FirmResult getProgressbar(){
        FirmResult fr;
        Map<String,Object> m = new HashMap<>();
        m.put("progressbarValue", MsgCache.sendSuccessCount);
        m.put("successUpgrade",MsgCache.successUpgrade);
        fr=new FirmResult(true,m);
        return fr;
    }

    @RequestMapping(value = "/uploadInitiative")
    @ResponseBody
    public FirmResult uploadInitiative(HttpServletRequest req, MultipartFile firmwareFile, HttpSession session){
        FirmResult fr;
        String machineCode = req.getParameter("machineCode");
        String sendInterval = req.getParameter("sendInterval");
        int sendTime = Integer.parseInt(sendInterval);
        if(sendTime>9999 || sendTime<1){
            fr=new FirmResult(false,"非法的时间数字,请填入1-9999之间的数字,单位毫秒");
            return fr;
        }
        logger.info("upload()获取到机器码={}",machineCode);
        String ip = ServiceMessageCache.getIpByMac(machineCode);
        int port = ServiceMessageCache.getPort(Long.parseLong(machineCode));
        /*if(ip=="" || port==0){
            fr=new FirmResult(false,"未获取到该设备的IP和端口,请检查网络后在尝试");
            return fr;
        }*/
        if(firmwareFile==null){
            fr=new FirmResult(false,"未检测到文件,请确认是否已上传文件");
            return fr;
        }
        //获取文件名称
        String fileName = firmwareFile.getOriginalFilename();
        if(fileName.endsWith("hex")){
            logger.info("已获取到上传文件={}",fileName);
            //保存固件到服务器
            String path="D:/uploadFirmware/"+new Date().getTime()+"_"+firmwareFile.getOriginalFilename();
            File newFile=new File(path);
            if (!newFile.getParentFile().exists()) {
                boolean result = newFile.getParentFile().mkdirs();
                if (!result) {
                    fr=new FirmResult(false,"保存文件时出现异常,请重新上传");
                    return fr;
                }
            }
            try {
                firmwareFile.transferTo(newFile);
                List list = FirmwareUtil.getLines(newFile);
                List list1 = FirmwareUtil.transToList(list);
                ServiceMessageCache.firmwareDataList = FirmwareUtil.TransToUdp(list1);
                //发送第一条固件升级指令 0x72
                String firstMessage = ServiceMessageCache.firmwareDataList.get(0);
                MsgCache.clearSend();
                MessageSender.sendFirstFirmwareUpgrade(machineCode,ip,port,firstMessage);
                Thread.sleep(sendTime);
                for(int i=1;i<ServiceMessageCache.firmwareDataList.size();i++){
                    if(MsgCache.stopFirmwareUpgrade == 1){
                        break;
                    }
                    String s = ServiceMessageCache.firmwareDataList.get(i);
                    //发送第二条固件升级数据
                    Thread.sleep(sendTime);
                    MessageSender.sendLogicTableRowDataPC(i+1,machineCode,ip,port,s);
                    //logger.info("***主动模式***已发送第"+(i+1)+"条固件数据={}",s);
                }
                MessageSender.sendFirmwareInfoToMac(machineCode,ip,port);
                Thread.sleep(sendTime);
                MessageSender.sendMacCrcCheck(machineCode,ip,port);
                Thread.sleep(sendTime);
                MessageSender.sendMacFirmwareEncryption(machineCode,ip,port);
                Thread.sleep(sendTime);
                MessageSender.sendMacFirmwareUpgradeEnd(machineCode,ip,port);
                MsgCache.clearSend();
                Map<String,Object> m = new HashMap<>();
                m.put("firmwareCount",ServiceMessageCache.firmwareDataList.size());
                fr=new FirmResult(true,m);
                MsgCache.clearSend();
                return fr;
            } catch (IOException | InterruptedException e) {
                logger.error(e.getMessage());
                fr=new FirmResult(false,"保存文件时出现异常,请重新上传");
                return fr;
            }
        }else{
            fr=new FirmResult(false,"文件类型不合法,请上传hex类型文件");
            return fr;
        }

    }

    @RequestMapping(value = "/stopFirmwareUpgrade")
    @ResponseBody
    public FirmResult stopFirmwareUpgrade(){
        MsgCache.stopFirmwareUpgrade = 1;
        FirmResult fr;
        fr = new FirmResult(true);
        return fr;
    }

    @RequestMapping(value = "/machineList")
    @ResponseBody
    public Map getMachineList(@RequestParam(value="page",required=false)String page,
                                     @RequestParam(value="rows",required=false)String rows){
        Map m1 = new HashMap();
        List<Machine> list = new ArrayList<>();
        for(Map.Entry m:ServiceMessageCache.MapIPPort.entrySet()){
            String machineCode = String.valueOf(m.getKey());
            String machineIpAndPort = (String)m.getValue();
            String[] ipAndPortList = machineIpAndPort.split("#");
            String ip = ipAndPortList[0];
            String port = ipAndPortList[1];
            list.add(new Machine(0L,Long.valueOf(machineCode),0,ip,port,null));
        }
        m1.put("rows",list);
        m1.put("total",0);
        return m1;
    }
}
