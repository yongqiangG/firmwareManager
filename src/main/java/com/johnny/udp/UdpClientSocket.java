package com.johnny.udp;

import com.johnny.utils.HexUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UdpClientSocket {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private byte[] buffer = new byte[4096];

    private DatagramSocket ds = null;

    /**
     * 构造函数，创建UDP客户端
     *
     * @throws Exception
     */
    public UdpClientSocket() throws Exception {
        if(ds==null || ds.isClosed()){
            ds = new DatagramSocket();
        }
    }

    /**
     * 向指定的服务端发送数据信息.
     *
     * @param host  服务器主机地址
     * @param port  服务端端口
     * @param bytes 发送的数据信息
     * @return 返回构造后俄数据报
     * @throws IOException Creation date: 2007-8-16 - 下午11:02:41
     */
    public final DatagramPacket send(final String host, final int port,
                                     final byte[] bytes) throws IOException {
        DatagramPacket dp = new DatagramPacket(bytes, bytes.length, InetAddress
                .getByName(host), port);
        System.out.println("发送端端口"+ds.getLocalPort());
        System.out.println("接收端端口"+ds.getPort());
        UdpServer.datagramSocket.send(dp);
        return dp;
    }

    public final DatagramSocket getSocket() {
        return ds;
    }

    /**
     * 接收从指定的服务端发回的数据.
     *
     * @param lhost 服务端主机
     * @param lport 服务端端口
     * @return 返回从指定的服务端发回的数据.
     * @throws Exception
     * @author <a href="mailto:xiexingxing1121@126.com">AmigoXie</a>
     * Creation date: 2007-8-16 - 下午10:52:36
     */
    public final String receive(final String lhost, final int lport)
            throws Exception {
        DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
        ds.receive(dp);
        String info = new String(dp.getData(), 0, dp.getLength());
        return info;
    }

    /**
     * 关闭udp连接.
     * Creation date: 2007-8-16 - 下午10:53:52
     */
    public final void close() {
        try {
            ds.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 测试客户端发包和接收回应信息的方法
     * 数据模拟:
     * 70指令 202054585041524b00010000ffffffff0000701000bc614e0652454144590000
     */
    public static void main(String[] args) throws Exception {
        UdpClientSocket client = new UdpClientSocket();
        String serverHost = "127.0.0.1";
        int serverPort = 3339;
        byte[] bytes = HexUtil.hexToByteArray("202054585041524b00010000ffffffff0000701000bc614e0652454144590000");
        client.send(serverHost,serverPort,bytes);
        String infoFromServer =client.receive(serverHost,serverPort);
        System.out.println("接收到服务端的回复:"+infoFromServer);
    }
}
