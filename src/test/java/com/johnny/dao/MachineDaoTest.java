package com.johnny.dao;

import com.johnny.entity.Machine;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class MachineDaoTest {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private MachineDao machineDao;

    @Test
    public void queryByHotelId() {
        List<Machine> machineList = machineDao.queryByHotelId(89,0,10);
        for (Machine machine:machineList){
            logger.info("machine={}",machine);
        }
    }

    @Test
    public void addMachine() {
        for(int i=0;i<100;i++){
            Machine machine = new Machine(null,123456789L+i,89,"192.168.1.12","5555");
            //machineDao.addMachine(machine);
        }

    }

    @Test
    public void queryByRoomTypeName() {
        List<Machine> machineList = machineDao.queryByRoomTypeName(0,"十层右开门",0,30);
        logger.info("size={}",machineList.size());
    }

    @Test
    public void getRoomTypeById(){
        List<String> list = machineDao.getRoomTypeById(0);
        for(int i=0;i<list.size();i++){
            System.out.println(list.get(i));
        }
    }
}