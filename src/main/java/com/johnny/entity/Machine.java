package com.johnny.entity;

import java.util.Date;

public class Machine {
    private Long machineId;
    private Long machineCode;
    private int hotelId;
    private String machineIp;
    private String machinePort;
    private Date createTime;
    private String roomTypeName;
    private String roomNo;



    public Machine() {
    }



    public Machine(Long machineId, Long machineCode, int hotelId, String machineIp, String machinePort) {
        this.machineId = machineId;
        this.machineCode = machineCode;
        this.hotelId = hotelId;
        this.machineIp = machineIp;
        this.machinePort = machinePort;
    }
    public String getRoomTypeName() {
        return roomTypeName;
    }

    public void setRoomTypeName(String roomTypeName) {
        this.roomTypeName = roomTypeName;
    }

    public String getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(String roomNo) {
        this.roomNo = roomNo;
    }

    public Long getMachineId() {
        return machineId;
    }

    public void setMachineId(Long machineId) {
        this.machineId = machineId;
    }

    public Long getMachineCode() {
        return machineCode;
    }

    public void setMachineCode(Long machineCode) {
        this.machineCode = machineCode;
    }

    public int getHotelId() {
        return hotelId;
    }

    public void setHotelId(int hotelId) {
        this.hotelId = hotelId;
    }

    public String getMachineIp() {
        return machineIp;
    }

    public void setMachineIp(String machineIp) {
        this.machineIp = machineIp;
    }

    public String getMachinePort() {
        return machinePort;
    }

    public void setMachinePort(String machinePort) {
        this.machinePort = machinePort;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "Machine{" +
                "machineId=" + machineId +
                ", machineCode=" + machineCode +
                ", hotelId=" + hotelId +
                ", machineIp='" + machineIp + '\'' +
                ", machinePort='" + machinePort + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}
