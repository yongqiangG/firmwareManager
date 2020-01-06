package com.johnny.utils;

import com.johnny.udp.cache.MsgCache;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.CRC32;

public class FirmwareUtil {
    private static final int byteSize = 200 * 1024;//读取字节数
    private static final String firstLinePrefix = ":020000";//第一行标识
    private static final String endLinePrefix = ":04000005";//结束标识
    private static final int firstAddress_10 = 2048;//第一次循环开始的地址为0800,10进制即为2048;
    private static final int dataCharMaxNum = 32;//数据部分最大字符数32,即16字节;
    private static final String compareString = "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF";//数据部分32个F字符

    public static void main(String[] args) {
        List list = getLines(new File("E:\\johnny\\TK3100.hex"));
        //将每一行string组成的list转换成每16行组成一个string的list
        List list1 = transToList(list);
        //去除每一行的Address部分
        //返回数据结构:高字节地址+低字节地址+256字节数据(最后可能不满256字节)
        List list2 = TransToUdp(list1);
    }

    //返回0x72固件升级置零的每一udp包组成的list
    public static List<String> hardWarePackage(File file) {
        List list = getLines(file);
        //将每一行string组成的list转换成每16行组成一个string的list
        List list1 = transToList(list);
        //去除每一行的Address部分
        //返回数据结构:高字节地址+低字节地址+256字节数据(最后可能不满256字节)
        List list2 = TransToUdp(list1);
        return list2;
    }

    //使用F补满数据部分的16字节长度
    private static String fillDataStr(String str) {
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        while (sb.length() < dataCharMaxNum) {
            sb.append("F");
        }
        return sb.toString();
    }

    //读取每一行数据
    public static List getLines(File file) {
        System.out.println(file.getPath());
        File hFile = new File(file.getPath());
        InputStream inputStream = null;
        BufferedReader bufferedReader = null;
        StringBuffer sb = new StringBuffer();
        try {
            if (hFile == null) {
                System.out.println("文件为空");
            }
            inputStream = new FileInputStream(hFile);
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            //当前字符串
            String lineStr = null;
            //当前行数
            int lineNum = 0;
            //当前行地址位
            int lineAddress = 0;
            //每次循环的首地址,第一次是0800,后面都是0000
            int firstAddress = firstAddress_10;
            //当前行的下标
            int offset = 0;
            //地址置零后,保存前面地址的偏移量
            int offsetAdd = 0;
            byte[] bytes = new byte[byteSize];
            List<String> list = new ArrayList<String>();
            while ((lineStr = bufferedReader.readLine()) != null) {
                lineNum++;
                //判断是否为第一行
                if (lineStr.startsWith(firstLinePrefix)) {
                    if (lineNum != 1) {
                        //不是第一行的话,地址位置零,偏移量16字节
                        firstAddress += 1;
                    }
                    continue;
                }
                //每个低字节地址
                lineAddress = Integer.parseInt(lineStr.substring(3, 7), 16);
                //读取数据部分
                String preData = lineStr.substring(9);
                String dataSource = preData.substring(0, preData.length() - 2);
                String data = dataDecryption(dataSource);
                //crc32校验全部数据
                sb.append(dataSource);
                //如果数据部分小于16字节,用F补满
                /*if (data.length() < dataCharMaxNum) {
                    FirmwareUtil.fillDataStr(data);
                }*/
                //offset = lineAddress-firstAddress+offsetAdd;
                list.add(firstAddress + "-" + lineAddress + "-" + data);
                //判断结束
                if (lineStr.startsWith(endLinePrefix)) {
                    break;
                }


            }
            //去除结束标识行
            list.remove(list.size() - 1);
            String str1 = sb.toString();
            String str2 = str1.substring(0,str1.length()-8);
            Long crc32 = getCRC32(str2);
            MsgCache.firmwareCRC32 = crc32;
            MsgCache.firmwareLength = str2.length()/2;
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //将每一行string组成的list转换成每16行组成一个string的list
    public static List transToList(List list) {
        List list1 = new ArrayList();
        StringBuilder sb = new StringBuilder();
        boolean flag = false;
        for (int i = 0; i < list.size(); i++) {
            sb.delete(0, sb.length());
            for (int j = 0; j < 16; j++) {
                if ((i * 16 + j + 1) > list.size()) {
                    flag = true;
                    break;
                }
                sb.append(list.get(i * 16 + j) + "-");
            }
            list1.add(new String(sb));
            if (flag) {
                break;
            }
        }
        return list1;
    }

    //转换成我们需要发送的udp数据包
    public static List TransToUdp(List<String> list) {
        List list1 = new ArrayList();
        String[] temp;
        StringBuffer sb1 = new StringBuffer();
        StringBuffer sb2 = new StringBuffer();

        for (int i = 0; i < list.size(); i++) {
            String dataAddressStart = "";
            String data = "";
            String a = list.get(i);
            temp = a.split("-");
            for (int j = 0; j < temp.length / 3; j++) {
                data += temp[j * 3 + 2];
            }
            String b = HexUtil.toHexString(Integer.parseInt(temp[0]), 4);
            String c = HexUtil.toHexString(Integer.parseInt(temp[1]), 4);
            String bReverse = exchangeHighLow(b);
            String cReverse = exchangeHighLow(c);
            dataAddressStart = cReverse + "" + bReverse;
            list1.add(dataAddressStart + "" + data);
        }
        return list1;

    }
    /**
     * 四位字符串高低交换
     */
    public static String exchangeHighLow(String addr){
        if(addr.length()!=4){
            return "字符串长度不是4,无法交换高低字节";
        }
        StringBuffer sb = new StringBuffer();
        String str1 = addr.substring(0,2);
        String str2 = addr.substring(2,4);
        sb.append(str2);
        sb.append(str1);
        return sb.toString();
    }
    /**
     * 八位字符串高低交换
     */
    public static String exchangeHighLow1(String addr){
        if(addr.length()!=8){
            return "字符串长度不是8,无法交换高低字节";
        }
        StringBuffer sb1 = new StringBuffer();
        StringBuffer sb2 = new StringBuffer();
        String str1 = addr.substring(0,4);
        String str2 = addr.substring(4,8);
        sb1.append(str2);
        sb1.append(str1);
        String str3 = exchangeHighLow(sb1.toString().substring(0,4));
        String str4 = exchangeHighLow(sb1.toString().substring(4,8));
        sb2.append(str3);
        sb2.append(str4);
        return sb2.toString();
    }

    /**
     * 异或加密
     * 256字节固件数据加密方式：例如数据字节byte0=0x49;byte0^0xDE^0xA5-1 最终得到0x49^0xDE^0xA5-1=0x31
     */
    private static String dataDecryption(String data) {
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i<data.length()/2;i++){
            String a = data.charAt(i*2)+""+data.charAt(i*2+1);
            Long a1 = HexUtil.hex2Number1(a);
            Long b = a1^0xDE^0xA5;
            String b1 = Long.toHexString(b-1);
            if(b1.length() == 1){
                b1 = "0"+b1;
            }
            sb.append(b1);
        }
        return sb.toString();

    }
    /**
     * CRC32校验
     */
    private static Long getCRC32(String dataStr){
        byte[] bytes = HexUtil.hexToByteArray(dataStr);
        CRC32 crc32 = new CRC32();
        crc32.update(bytes);
        return crc32.getValue();
    }
}
