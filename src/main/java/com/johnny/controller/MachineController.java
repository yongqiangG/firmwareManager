package com.johnny.controller;

import com.johnny.dto.FirmResult;
import com.johnny.entity.Machine;
import com.johnny.service.HotelService;
import com.johnny.service.MachineService;
import com.johnny.utils.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping(value = "/machine")
public class MachineController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private HotelService hotelService;
    @Autowired
    private MachineService machineService;


    //获取所有设备
    @RequestMapping(value = "/{hotelId}/getMachine",
                    method = RequestMethod.POST,
                    produces = {"application/json;charset=UTF-8"}
    )
    @ResponseBody
    public FirmResult getMachineByHotelId(@PathVariable("hotelId") int hotelId){
        if(hotelId==0){
            return new FirmResult(false,"酒店Id为空");
        }
        //根据酒店Id获取客控外网地址
        String extranet = hotelService.getUrlByHotelId(hotelId);
        //请求获取设备信息 TODO
        List<Machine> machineList = HttpUtil.getRequest1();
        if(machineList==null){
            return new FirmResult(false,"获取到0个设备");
        }
        FirmResult fr = machineService.addMachine(machineList,hotelId);
        return fr;
    }
    //获取房型
    @RequestMapping(value = "/{hotelId}/getRoomType",
                    method = RequestMethod.POST,
                    produces = {"application/json;charset=UTF-8"}
    )
    @ResponseBody
    public FirmResult getRoomType(@PathVariable("hotelId") int hotelId){
        List<String> roomTypeList = machineService.getRoomType(hotelId);
        if(roomTypeList.size()==0){
            return new FirmResult(false,"未获取到任何房型");
        }
        return new FirmResult(true,roomTypeList);
    }
    //根据房型获取设备
    @RequestMapping(
            value = "/{hotelId}/queryByRoomTypeName",
            method = RequestMethod.POST,
            produces = {"application/json;charset=UTF-8"}
    )
    @ResponseBody
    public FirmResult queryByRoomTypeName(HttpServletRequest requestq, @PathVariable("hotelId")int hotelId) {
        String roomTypeName = requestq.getParameter("roomTypeName");
        List<Machine> machines = machineService.queryByRoomTypeName(hotelId,roomTypeName,0,200);
        if(machines.size()==0){
            return new FirmResult(false,"未获取到该房型的任何设备");
        }
        return new FirmResult(true,machines);
    }
    //批量升级请求

}
