package com.johnny.controller;

import com.johnny.udp.UDPServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/firmware")
public class IndexController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(value = "/index")
    public String index(){
        //TODO
        return "index";
    }

    @RequestMapping(value = "/macReset")
    @ResponseBody
    public Map macReset(HttpServletRequest request) {
        Map<String,Object> map = new HashMap<String,Object>();
        String macCode = request.getParameter("macCode");
        String port = request.getParameter("port");
        logger.info("macCode:{}",macCode);
        System.out.println("macCode:" + macCode);
        try {
            UDPServer.serverStartListen(macCode, Integer.valueOf(port));
        } catch (IOException e) {
            e.printStackTrace();
        }
        map.put("success",true);
        map.put("msg","success");
        return map;
    }
}
