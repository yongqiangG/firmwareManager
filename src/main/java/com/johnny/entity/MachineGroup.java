package com.johnny.entity;

import java.util.ArrayList;
import java.util.List;

public class MachineGroup {
    private List<Machine> machineList = new ArrayList<Machine>();

    public List<Machine> getMachineList() {
        return machineList;
    }

    public void setMachineList(List<Machine> machineList) {
        this.machineList = machineList;
    }
}
