package com.johnny.udp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Observable;
import java.util.Observer;

public class MessageSendThreadMainListener implements Observer {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Override
    public void update(Observable o, Object arg) {
        logger.info("MsgSenderThread死机");
        MessageSendThreadMain msgThread = new MessageSendThreadMain();
        msgThread.addObserver(this);
        new Thread(msgThread).start();
        logger.info("MsgSenderThread重启");
    }
}
