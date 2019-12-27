package com.johnny.dao;

import com.johnny.entity.Machine;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MachineDao {
    //获取所有的RCU主机
    List<Machine> queryAll(@Param("offset") int offset, @Param("limit") int limit);
    //根据酒店ID获取RCU主机
    List<Machine> queryByHotelId(@Param("hotelId") int hotelId, @Param("offset") int offset, @Param("limit") int limit);
    //根据机器码获取RCU主机
    Machine queryByMachineCode(Long machineCode);
    //根据ID获取RCU主机
    Machine queryById(Long machineId);
}
