package com.johnny.exception;

public class MachineException extends RuntimeException {
    //插入设备异常
    public MachineException(String message){
        super(message);
    }
}
