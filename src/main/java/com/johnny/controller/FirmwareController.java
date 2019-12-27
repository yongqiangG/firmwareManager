package com.johnny.controller;

import com.johnny.dto.FirmResult;
import com.johnny.udp.MessageSender;
import com.johnny.udp.cache.ServiceMessageCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
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
    public FirmResult FirmResult(HttpServletRequest req){
        String machineCode = req.getParameter("machineCode");
        logger.info("getIpAndPort()获取到机器码={}",machineCode);
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
}
