package com.johnny.service.impl;

import com.johnny.dao.MachineDao;
import com.johnny.dto.FirmResult;
import com.johnny.entity.Machine;
import com.johnny.exception.FirmwareException;
import com.johnny.exception.MachineException;
import com.johnny.service.MachineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service(value = "machineService")
public class MachineServiceImpl implements MachineService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private MachineDao machineDao;
    @Override
    public FirmResult addMachine(List<Machine> machineList,int hotelId) {
        int machineSize = machineList.size();
        int insertCount = 0;
        for (Machine machine : machineList){
            int result = machineDao.addMachine(machine.getMachineCode(),machine.getHotelId(),"0","0",machine.getRoomTypeName(),machine.getRoomNo());
            insertCount += result;
        }
        //先清空原始数据
        machineDao.deleteByHotelId(hotelId);
        try {
            if(machineSize!=insertCount){
                //插入数量不正确
                throw new MachineException("Insert Machine Exception");
            }else{
                return new FirmResult(true);
            }
        } catch (MachineException e) {
            throw e;
        } catch (Exception e){
            logger.error(e.getMessage(),e);
            throw new FirmwareException("Firmware Inner Error:"+e.getMessage());
        }

    }

    @Override
    public List<Machine> queryByHotelId(int id, int offset, int limit) {

        return null;
    }

    @Override
    public List<Machine> queryByRoomTypeName(int id, String roomTypeName, int offset, int limit) {
        return machineDao.queryByRoomTypeName(id,roomTypeName,offset,limit);
    }

    @Override
    public List<String> getRoomType(int id) {
        return machineDao.getRoomTypeById(id);
    }

}
