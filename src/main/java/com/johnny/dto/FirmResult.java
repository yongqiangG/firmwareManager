package com.johnny.dto;

public class FirmResult<T> {
    private boolean success;
    private T data;
    private String errorInfo;

    public FirmResult(boolean success) {
        this.success = success;
    }

    public FirmResult(boolean success, T data) {
        this.success = success;
        this.data = data;
    }

    public FirmResult(boolean success, String errorInfo) {
        this.success = success;
        this.errorInfo = errorInfo;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getErrorInfo() {
        return errorInfo;
    }

    public void setErrorInfo(String errorInfo) {
        this.errorInfo = errorInfo;
    }
}
