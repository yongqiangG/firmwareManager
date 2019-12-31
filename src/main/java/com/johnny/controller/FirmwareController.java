package com.johnny.controller;

import com.johnny.dto.FirmResult;
import com.johnny.udp.MessageSender;
import com.johnny.udp.cache.MsgCache;
import com.johnny.udp.cache.ServiceMessageCache;
import com.johnny.utils.FirmwareUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                MessageSender.sendFirstFirmwareUpgrade(machineCode,ip,port,firstMessage);
                logger.info("已发送第一条升级指令={}",firstMessage);
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
        fr=new FirmResult(true,m);
        return fr;
    }
}
