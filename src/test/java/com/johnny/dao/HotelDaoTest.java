package com.johnny.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class HotelDaoTest {
    @Autowired
    private HotelDao hotelDao;
    @Test
    public void getHotelUrlById(){
        String extranet = hotelDao.getHotelUrlById(89);
        System.out.println(extranet);
    }
}
