package com.stkj.cashier.pay.model;

import java.util.List;

public class DeviceFoodConsumeParam {

    private String deviceNo;
    private String cardNumber;
    private int deductionType;
    private double money;
    private int consumptionType;
    private String onlineOrderNumber;
    private int payType;
    private int isQuick;
    private List<ConsumeFoodInfo> consumeFoodInfoList;


    public DeviceFoodConsumeParam(String deviceNo, String cardNumber, int deductionType, double money, int consumptionType, String onlineOrderNumber, int payType, int isQuick, List<ConsumeFoodInfo> consumeFoodInfoList) {
        this.deviceNo = deviceNo;
        this.cardNumber = cardNumber;
        this.deductionType = deductionType;
        this.money = money;
        this.consumptionType = consumptionType;
        this.onlineOrderNumber = onlineOrderNumber;
        this.payType = payType;
        this.isQuick = isQuick;
        this.consumeFoodInfoList = consumeFoodInfoList;
    }

    public String getDeviceNo() {
        return deviceNo;
    }

    public void setDeviceNo(String deviceNo) {
        this.deviceNo = deviceNo;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public int getDeductionType() {
        return deductionType;
    }

    public void setDeductionType(int deductionType) {
        this.deductionType = deductionType;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public int getConsumptionType() {
        return consumptionType;
    }

    public void setConsumptionType(int consumptionType) {
        this.consumptionType = consumptionType;
    }

    public String getOnlineOrderNumber() {
        return onlineOrderNumber;
    }

    public void setOnlineOrderNumber(String onlineOrderNumber) {
        this.onlineOrderNumber = onlineOrderNumber;
    }

    public int getPayType() {
        return payType;
    }

    public void setPayType(int payType) {
        this.payType = payType;
    }

    public int getIsQuick() {
        return isQuick;
    }

    public void setIsQuick(int isQuick) {
        this.isQuick = isQuick;
    }

    public List<ConsumeFoodInfo> getConsumeFoodInfoList() {
        return consumeFoodInfoList;
    }

    public void setConsumeFoodInfoList(List<ConsumeFoodInfo> consumeFoodInfoList) {
        this.consumeFoodInfoList = consumeFoodInfoList;
    }
}
