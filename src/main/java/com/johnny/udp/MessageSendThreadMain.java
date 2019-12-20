package com.johnny.udp;

import com.johnny.udp.cache.MsgCache;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Observable;

public class MessageSendThreadMain extends Observable implements Runnable {
    public void doBusiness() {
        if (true) {
            super.setChanged();
        }
        notifyObservers();
    }

    public void run(){
        while(true){
            Date date = new Date();
            long time = date.getTime();
            List<Msg> removeList = new ArrayList<Msg>();
            try{
                if(MsgCache.unResponseMsg!=null)
                    for (String key : MsgCache.unResponseMsg.keySet()) {
                        Msg msg = MsgCache.unResponseMsg.get(key);
                        if(msg.sendTimes >= 10){
                            //MsgCache.remove(msg);
                            removeList.add(msg);
                            continue;
                        }
                        if(msg.success && time > msg.deleteTime){
                            //MsgCache.remove(msg);
                            removeList.add(msg);
                        }
                        if(msg.success){
                            continue;
                        }
                        if(msg.offline){
                            //MsgCache.remove(msg);
                            removeList.add(msg);
                            continue;
                        }
                        if(time > msg.lastSendTime + 3 * 1000){
                            msg.lastSendTime = time;
                            //System.out.println("[发送未发送响应消息]["+msg.sendTimes+"]");
                            //System.out.println(msg.cmd+":"+msg.msg+": key :"+key);
                            String ip = null;
                            int port = 0;
                            MessageSender.sendUnresponseMsg(msg,ip,port);
                            msg.sendTimes++;
                            Thread.sleep(100);
                            time += 100;
                        }
                    }

                for (int i = 0; i < removeList.size(); i++) {
                    //System.out.println("【删除发送成功消息】");
                    MsgCache.remove(removeList.get(i));
                }
                List<String>removeKeyList = new ArrayList<String>();
                if(MsgCache.responseMsg!=null)
                    for (String key : MsgCache.responseMsg.keySet()) {
                        ResponseMsg repMsg = MsgCache.responseMsg.get(key);
                        if(time > repMsg.lastSendTime + 10 * 60 * 1000){
                            //MsgCache.responseMsg.remove(key);
                            removeKeyList.add(key);
                        }
                    }
                for (int i = 0; i < removeKeyList.size(); i++) {
                    MsgCache.responseMsg.remove(removeKeyList.get(i));
                }
                Thread.sleep(1 * 1000);
            }catch(Exception e){
                e.printStackTrace();
                doBusiness();
                break;
            }
        }
    }
}
