package com.stkj.cashier.pay.model;

public class DeviceFoodConsumePageParam {

    private String deviceNo;
    private String name;
    private String orderNO;
    private String startTime;
    private String endTime;
    /**
     * 0 表示用户账单 1 表示三方账单
     */
    private int billType;
    private int pageIndex;
    private int pageSize;


    public DeviceFoodConsumePageParam(String deviceNo, String name, String orderNO, String startTime, String endTime, int billType, int pageIndex, int pageSize) {
        this.deviceNo = deviceNo;
        this.name = name;
        this.orderNO = orderNO;
        this.startTime = startTime;
        this.endTime = endTime;
        this.billType = billType;
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
    }

    public String getDeviceNo() {
        return deviceNo;
    }

    public void setDeviceNo(String deviceNo) {
        this.deviceNo = deviceNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrderNO() {
        return orderNO;
    }

    public void setOrderNO(String orderNO) {
        this.orderNO = orderNO;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getBillType() {
        return billType;
    }

    public void setBillType(int billType) {
        this.billType = billType;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
