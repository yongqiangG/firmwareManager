package com.johnny.service.impl;

import com.johnny.dao.HotelDao;
import com.johnny.exception.FirmwareException;
import com.johnny.exception.HotelException;
import com.johnny.service.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value = "hotelService")
public class HotelServiceImpl implements HotelService {
    @Autowired
    private HotelDao hotelDao;

    @Override
    public String getUrlByHotelId(int hotelId) {
        String extranet = hotelDao.getHotelUrlById(hotelId);
        try {
            if(extranet=="" || extranet==null){
                throw new HotelException("无效ID");
            }
            return extranet;
        } catch (HotelException e) {
            throw e;
        } catch (Exception e){
            throw new FirmwareException("Firmware Inner Error:"+e.getMessage());
        }
    }
}
