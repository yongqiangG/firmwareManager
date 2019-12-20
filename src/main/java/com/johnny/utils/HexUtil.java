package com.johnny.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

public class HexUtil {
    private static Logger logger = LoggerFactory.getLogger(HexUtil.class);
    private static SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd-HH-mm-ss");

    public static String toHexString(String s, int length) {
        StringBuffer str = new StringBuffer("");
        for (int i = 0; i < s.length(); i++) {
            int ch = (int) s.charAt(i);
            if (ch > 255) {
                System.err.println("10进制转换16进制出错，值[" + ch + "]超出255");
            }
            String s4 = Integer.toHexString(ch);
            str.append(s4);
        }
        if (length == 0) {
            return str.toString();
        }
        while (str.length() < length) {
            str.insert(0, "0");
        }
        return str.toString();
    }

    public static String toHexString(int number, int length) {
        String n = Integer.toHexString(number);
        if (length == 0) {
            return n;
        }
        while (n.length() < length) {
            n = "0" + n;
        }
        if (n.length() > length) {
            logger.error("出现异常={}"+"数字过大");
        }
        return n;
    }

    public static String hex2Number(String s) {
        return Long.parseLong(s, 16) + "";
    }

    public static String toHexString(long number, int length) {
        String n = Long.toHexString(number);
        if (length == 0) {
            return n;
        }
        if (n.length() > length) {
            logger.error("出现异常={}"+"数字过大");
        }
        while (n.length() < length) {
            n = "0" + n;
        }
        return n;
    }

    public static String hex2Str(String s) {
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(
                        i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            s = new String(baKeyword, "utf-8");// UTF-16le:Not
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return s;
    }

    public static String getTimeHexStr() {
        String str = sdf.format(new Date());
        //System.out.println(str);
        String[] arr = str.split("-");
        StringBuffer sb = new StringBuffer();
        for (int i = arr.length - 1; i >= 0; i--) {
            int v = Integer.parseInt(arr[i]);
            sb.append(toHexString(v, 2));
        }
        return sb.toString();
    }

    /**
     * 192.168.1.1
     *
     * @param ip
     * @return
     */
    public static String toIpHexStr(String ip) {
        StringBuffer sb = new StringBuffer("");
        String[] arr = ip.split("\\.");
        for (int i = 0; i < arr.length; i++) {
            int tmp = Integer.parseInt(arr[i]);
            sb.append(toHexString(tmp, 2));
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        int code = 25;
        //System.out.println(toBinaryString(25-22,3));
    }

    public static String toBinaryString(int num, int length) {
        String n = Integer.toBinaryString(num);
        if (length == 0) {
            return n;
        }
        if (n.length() > length) {
            System.out.println("此处开始异常数字：" + num + "  ,限制长度：" + length);
            logger.error("出现异常={}"+"数字过大");
        }
        while (n.length() < length) {
            n = "0" + n;
        }
        return n;
    }
}
