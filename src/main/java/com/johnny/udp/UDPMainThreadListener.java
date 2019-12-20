package com.johnny.udp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Observable;
import java.util.Observer;

public class UDPMainThreadListener implements Observer {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    public void update(Observable o, Object arg) {
        logger.info("RunThread死机");
        UDPMainThread UDPRun = new UDPMainThread();
        UDPRun.addObserver(this);
        new Thread(UDPRun).start();
        logger.info("RunThread重启");
    }
}
