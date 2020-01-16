package com.johnny.dao;

public interface HotelDao {
    //根据酒店ID获取外网地址
    String getHotelUrlById(int hotelId);
}
