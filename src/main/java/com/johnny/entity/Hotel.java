package com.johnny.entity;

import java.util.Date;

public class Hotel {
    private int hotelId;
    private String name;
    private String extranet;
    private Date createTime;

    public int getHotelId() {
        return hotelId;
    }

    public void setHotelId(int hotelId) {
        this.hotelId = hotelId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExtranet() {
        return extranet;
    }

    public void setExtranet(String extranet) {
        this.extranet = extranet;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "Hotel{" +
                "hotelId=" + hotelId +
                ", name='" + name + '\'' +
                ", extranet='" + extranet + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}
