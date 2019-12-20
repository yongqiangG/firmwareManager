package com.johnny.udp;

public class Config {
    /**
     * UDP服务器端口
     */
    public static final int UDP_SERVER_PORT = 3339;
    /**
     * UDP服务器IP
     */
    public static final String UDP_SERVER_IP =  "127.0.0.1";
    /**
     * UDP接收内容大小 5M
     */
    public static final int UDP_RECEIVE_CACHE_LEN = 1024 * 1024 ;
    /**
     * UDP 响应内容大小
     */
    public static final int UDP_RESPONSE_CACHE_LEN = 1024 * 1024 ;
    /**
     * 系统识别码
     * TXPARK
     */
    public static final String UDP_HEADER_CODE = "54585041524b";
    /**
     * 终端udp端口
     */
    public static final int UDP_CLIENT_PORT = 3341;
    /**
     * 最大线程数
     */
    public static final int MAX_POOL_SIZE = 50;

    public static final String MY_UDP_SERVER = "on";
    public static final String MY_MSG_SEND_THREAD_FLAG = "on";
}
