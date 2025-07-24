package com.stkj.cashier.stat.model;

import java.util.List;

public class DeviceFoodRefundParam {

    private String deviceNo;
    private Integer consumptionType;
    private String billId;
    private double money;
    private List<RefundFoodInfo> refundFoodInfoList;

    public DeviceFoodRefundParam(String deviceNo, Integer consumptionType, String billId, double money, List<RefundFoodInfo> refundFoodInfoList) {
        this.deviceNo = deviceNo;
        this.consumptionType = consumptionType;
        this.billId = billId;
        this.money = money;
        this.refundFoodInfoList = refundFoodInfoList;
    }

    public String getDeviceNo() {
        return deviceNo;
    }

    public void setDeviceNo(String deviceNo) {
        this.deviceNo = deviceNo;
    }

    public Integer getConsumptionType() {
        return consumptionType;
    }

    public void setConsumptionType(Integer consumptionType) {
        this.consumptionType = consumptionType;
    }

    public String getBillId() {
        return billId;
    }

    public void setBillId(String billId) {
        this.billId = billId;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public List<RefundFoodInfo> getRefundFoodInfoList() {
        return refundFoodInfoList;
    }

    public void setRefundFoodInfoList(List<RefundFoodInfo> refundFoodInfoList) {
        this.refundFoodInfoList = refundFoodInfoList;
    }
}
