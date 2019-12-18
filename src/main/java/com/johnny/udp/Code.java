package com.johnny.udp;

/**
 * 升级指令集
 */
public class Code {
    //复位启动握手指令
    public static final int HARDWARE_RESET = 0x70;
    //升级开始
    public static final int UPDATE_HARDWARE_START = 0X75;
    //升级中
    public static final int UPDATE_HARDWARE_BY_PACKAGE = 0x72;
    //升级结束
    public static final int UPDATE_HARDWARE_END = 0x76;
}
