package com.johnny.udp;

import com.johnny.udp.cache.ServiceMessageCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TimeoutThread extends Thread {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public void run() {
        List<String> deleteKeys = new ArrayList<String>();
        MessageCallService callService = new MessageCallService();
        UdpClientSocket client = null;
        ServiceMessageCache.InitMacIP();
        List<Long> delList = new ArrayList<Long>();
        String msg = "";
        while (true) {

            Date d = new Date();
            long now = d.getTime();
            /**
             * 信息服务处理
             */
            try {

                if (ServiceMessageCache.unSaveServiceMsg.size() == 0) continue;

                //System.out.println("信息服务处理111");
                if (callService == null) callService = new MessageCallService();

                if (client == null) client = new UdpClientSocket();

                long index = 1l;

                //循环处理
                for (index = ServiceMessageCache.currentSave_Index; index <= ServiceMessageCache.serviceIndex_Add; index++) {
                    msg = ServiceMessageCache.unSaveServiceMsg.get(index);
                    if (msg != null) {
                        callService.read(msg, client, index);
                        delList.add(index);
                    } else {
                        break;
                    }
                }
                ServiceMessageCache.currentSave_Index = index;
                //处理过的清除掉
                if (delList.size() > 0) {
                    for (Long long1 : delList) {
                        ServiceMessageCache.unSaveServiceMsg.remove(long1);
                    }
                    callService = null;
                    if (client != null) client.close();
                    client = null;
                    delList.clear();
                }
            } catch (Exception e) {

            }
        }
    }
}
