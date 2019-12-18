package com.johnny.udp;

public class HardWareModel {
    //记录0x72固件升级当前发送到第几条数据
    public static int index;

    public static int getIndex() {
        return index;
    }

    public static void setIndex(int index) {
        HardWareModel.index = index;
    }
}
