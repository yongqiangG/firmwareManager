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
    //增加一台设备
    int addMachine(@Param("machineCode") Long machineCode,
                   @Param("hotelId") int hotelId,
                   @Param("machineIp") String machineIp,
                   @Param("machinePort") String machinePort,
                   @Param("roomTypeName") String roomTypeName,
                   @Param("roomNo") String roomNo);
    //根据房型获取设备列表
    List<Machine> queryByRoomTypeName(@Param("hotelId") int hotelId,@Param("roomTypeName") String roomTypeName,
                                      @Param("offset") int offset,@Param("limit") int limit);
    //根据酒店ID删除机器码
    int deleteByHotelId(@Param("hotelId") int hotelId);

    //根据酒店ID获取房型列表
    List<String> getRoomTypeById(@Param("hotelId") int hotelId);
}
