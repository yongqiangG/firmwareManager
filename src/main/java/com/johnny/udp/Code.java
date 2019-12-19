package com.johnny.udp;

public class Code {
    //终端复位启动握手指令
    public static final int MAC_RESET_SHAKE = 0x70;

    //已进入升级模式握手指令
    public static final int MAC_READY_SHAKE = 0x7F;

    //获取终端固件版本号
    public static final int GET_MAC_VERSION = 0x71;

    //应用端写数据到终端指令
    public static final int MAC_FIRMWARE_UPGRADE = 0x72;

    //应用端发送校验数据到终端指令
    public static final int MAC_CRC_CHECK = 0x73;

    //应用端发送固件数据加密指令
    public static final int MAC_FIRMWARE_ENCRYPTION = 0x74;

    //应用端发送固件升级启动指令
    public static final int MAC_FIRMWARE_UPGRADE_START = 0x75;

    //应用端发送固件升级结束指令
    public static final int MAC_FIRMWARE_UPGRADE_END = 0x76;

    //应用端发送终端进入应用模式指令
    public static final int MAC_TO_WORK = 0x77;

    //终端机器码修改设置指令
    public static final int MAC_CODE_MODIFY = 0x79;
}
