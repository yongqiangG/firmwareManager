package com.johnny.controller;

import com.johnny.udp.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Controller
@RequestMapping("/firmware")
public class IndexController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(value = "/index")
    public String index(){
        //TODO
        return "index";
    }
    @RequestMapping(value = "/index1")
    public String index1(){
        //TODO
        return "index1";
    }

    @RequestMapping(value = "/bindDevice")
    public void bindDevice(HttpServletRequest request){
        String macCode = request.getParameter("macCode");
        String ipAddress = request.getParameter("ipAddress");
        String ipBroadcast = request.getParameter("ipBroadcast");
        logger.info("macCode:"+macCode+",ipAddress:"+ipAddress+",ipBroadcast"+ipBroadcast);
        MessageSender.sendBindDevice(macCode,ipAddress,ipBroadcast);
    }

    @RequestMapping(value = "/macReset")
    @ResponseBody
    public void macReset(HttpServletRequest request) {
        logger.info("开始启动服务---------------------");
        /**
         * UDP 服务器主线程
         */
        //new MainThread().start();  myUDPServer
        String myUDPServer= Config.MY_UDP_SERVER;
        if(myUDPServer!=null && "on".equals(myUDPServer)){
            //UDP服务重启机制
            UDPMainThread udpMth = new UDPMainThread();
            UDPMainThreadListener listener = new UDPMainThreadListener();
            udpMth.addObserver(listener);
            new Thread(udpMth).start();
        }else{
            new MainThread().start();
        }

        /**
         * UDP消息发送线程
         */
        String msgSendThreadFlag=Config.MY_MSG_SEND_THREAD_FLAG;
        if(msgSendThreadFlag!=null && "on".equals(msgSendThreadFlag)){
            //重启机制
            MessageSendThreadMain msgThreadMain = new MessageSendThreadMain();
            MessageSendThreadMainListener msgSendListener = new MessageSendThreadMainListener();
            msgThreadMain.addObserver(msgSendListener);
            new Thread(msgThreadMain).start();
        }else{
            new MessageSenderThread().start();
        }
        /**
         * 终端设备超时检测线程
         */
        String sendPoolFlag=Config.MY_SEND_POOL_FLAG;
        if(sendPoolFlag!=null&& "on".equals(sendPoolFlag)){
            String poolNumString=Config.SEND_POOL_SIZE;
            int sendPool=Integer.parseInt(poolNumString.trim());
            ExecutorService timePool = Executors.newFixedThreadPool(sendPool);
            Thread myTimeOuThread=new TimeoutThread();
            timePool.execute(myTimeOuThread);
        }else{
            new TimeoutThread().start();
        }
    }
}
