package com.johnny.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FirmwareUtil {
    private static final int byteSize = 200*1024;//读取字节数
    private static final String firstLinePrefix = ":020000";//第一行标识
    private static final String endLinePrefix = ":04000005";//结束标识
    private static final int firstAddress_10 = 2048;//第一次循环开始的地址为0800,10进制即为2048;
    private static final int dataCharMaxNum = 32;//数据部分最大字符数32,即16字节;
    private static final String compareString ="FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF";//数据部分32个F字符

    public static void main(String[] args) {
        List list = getLines(new File("F:\\txpark_doc\\TK3100.hex"));
        //将每一行string组成的list转换成每16行组成一个string的list
        List list1 = transToList(list);
        //去除每一行的Address部分
        //返回数据结构:高字节地址+低字节地址+256字节数据(最后可能不满256字节)
        List list2 = TransToUdp(list1);
    }

    //返回0x72固件升级置零的每一udp包组成的list
    public static List<String> hardWarePackage(File file){
        List list = getLines(file);
        //将每一行string组成的list转换成每16行组成一个string的list
        List list1 = transToList(list);
        //去除每一行的Address部分
        //返回数据结构:高字节地址+低字节地址+256字节数据(最后可能不满256字节)
        List list2 = TransToUdp(list1);
        return list2;
    }

    //使用F补满数据部分的16字节长度
    private static String fillDataStr(String str){
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        while(sb.length()<dataCharMaxNum){
            sb.append("F");
        }
        return sb.toString();
    }
    //读取每一行数据
    public static List getLines(File file){
        File hFile = new File(file.getPath());
        InputStream inputStream = null;
        BufferedReader bufferedReader = null;
        try {
            if(hFile==null){
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
            while((lineStr = bufferedReader.readLine())!=null){
                lineNum++;
                //判断是否为第一行
                if (lineStr.startsWith(firstLinePrefix)) {
                    if(lineNum!=1){
                        //不是第一行的话,地址位置零,偏移量16字节
                        firstAddress += 1;
                    }
                    continue;
                }
                //每个低字节地址
                lineAddress = Integer.parseInt(lineStr.substring(3,7),16);
                //读取数据部分
                String preData = lineStr.substring(9);
                String data = preData.substring(0,preData.length()-2);
                //如果数据部分小于16字节,用F补满
                if(data.length()<dataCharMaxNum){
                    FirmwareUtil.fillDataStr(data);
                }
                //offset = lineAddress-firstAddress+offsetAdd;
                list.add(firstAddress+"-"+lineAddress+"-"+data);
                //判断结束
                if(lineStr.startsWith(endLinePrefix)){
                    break;
                }


            }
            //去除结束标识行
            list.remove(list.size()-1);
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    //将每一行string组成的list转换成每16行组成一个string的list
    public static List transToList(List list){
        List list1 = new ArrayList();
        StringBuilder sb = new StringBuilder();
        boolean flag = false;
        for(int i=0;i<list.size();i++){
            sb.delete(0,sb.length());
            for(int j=0;j<16;j++){
                if((i*16+j+1)>list.size()){
                    flag = true;
                    break;
                }
                sb.append(list.get(i*16+j)+"-");
            }
            list1.add(new String(sb));
            if(flag){
                break;
            }
        }
        return list1;
    }
    //转换成我们需要发送的udp数据包
    public static List TransToUdp(List<String> list){
        List list1 = new ArrayList();
        String[] temp;

        for(int i=0;i<list.size();i++){
            String dataAddressStart = "";
            String data = "";
            String a = list.get(i);
            temp = a.split("-");
            for(int j=0 ;j<temp.length/3;j++){
                data += temp[j*3+2];
            }
            String b = HexUtil.toHexString(Integer.parseInt(temp[0]),4);
            String c = HexUtil.toHexString(Integer.parseInt(temp[1]),4);
            dataAddressStart = b+""+c;
            list1.add(dataAddressStart+""+data);
        }
        return list1;

    }
}
