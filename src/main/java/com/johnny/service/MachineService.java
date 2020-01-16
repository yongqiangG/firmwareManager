package com.johnny.service;

import com.johnny.dto.FirmResult;
import com.johnny.entity.Machine;

import java.util.List;

public interface MachineService {
    //新增设备
    FirmResult addMachine(List<Machine> machineList,int hotelId);
    //根据酒店ID查询设备
    List<Machine> queryByHotelId(int id,int offset,int limit);
    //根据酒店ID和房型查找设备
    List<Machine> queryByRoomTypeName(int id,String roomTypeName,int offset,int limit);

}
