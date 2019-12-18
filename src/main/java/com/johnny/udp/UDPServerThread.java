package com.johnny.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * 服务器线程处理类
 */
public class UDPServerThread extends Thread {
    DatagramSocket socket = null;
    DatagramPacket packet = null;
    byte[] data = null;
    String macCode = null;

    public UDPServerThread(byte[] data, DatagramSocket socket, DatagramPacket packet,String macCode) {
        this.socket = socket;
        this.packet = packet;
        this.data = data;
        this.macCode = macCode;
    }

    //线程要执行的方法
    public void run() {
        String info = new String(data);
        InetAddress address = packet.getAddress();
        int port = packet.getPort();
        String hexString = bytesToHex(data);
        System.out.println("收到来自机器码"+macCode+"的信息：" + hexString+",线程已启动");

        /*从设备回复包动态获取机器码
        int macCode = Integer.valueOf(getMacCode(hexString));
        */
        //获取前端机器码
        String mac = Integer.toHexString(Integer.valueOf(macCode));
        String msg = "202054585041524b01000000ffffffff00007010"+mac+"0652454144590000";
        //String msg = "202054585041524b01000000ffffffff00007010305A453D0652454144590000";
        int len = msg.length() / 2;
        byte[] data2 = new byte[len];
        for (int i = 0; i < len; i++) {
            String n = msg.substring(i * 2, i * 2 + 2);
            int num = Integer.parseInt(n, 16);
            data2[i] = (byte) num;
        }
        DatagramPacket packet2 = new DatagramPacket(data2, data2.length, address, port);
        int cmd = getCommand(hexString);
        //3.响应客戶端
        try {
            //判断是否需要发送进入烧写模式
            if (cmd == 0x70) {
                socket.send(packet2);
                System.out.println("服务器端70指令数据发送完毕:"+bytesToHex(data2));
            } else {
                System.out.println("非70指令,暂时无需处理");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 17-18字节
     * 获取指令编码,返回值为10进制数字
     */
    public static int getCommand(String msg) {
        String cmd = msg.substring(34, 38);
        int intCmd = Integer.parseInt(cmd, 16);
        return intCmd;
    }

    /**
     * 20-23字节
     * 获取设备机器码
     */
    private static String getMacCode(String msg) {
        String code = msg.substring(40, 48);
        return Long.parseLong(code, 16) + "";
    }

    /**
     * byte[] -> hex string
     */
    private static final char[] HEX_CHAR = {'0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static String bytesToHex(byte[] bytes) {
        char[] buf = new char[bytes.length * 2];
        int index = 0;
        for (byte b : bytes) { // 利用位运算进行转换，可以看作方法一的变种
            buf[index++] = HEX_CHAR[b >>> 4 & 0xf];
            buf[index++] = HEX_CHAR[b & 0xf];
        }

        return new String(buf);
    }
}