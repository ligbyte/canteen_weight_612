package com.stkj.plate.weight.pay.model;


import java.util.List;

/**
 * 历史订单详情
 */
public class FoodConsumeDetailResponse {

   private String orgName;
   private String settleTime;
   private String restaurantName;
   private List<ConsumeFoodBean> consumeFoodList;
   private String deviceNo;
   private String restaurantId;
   private String deviceId;
   private String deviceName;
   private String customerAccount;
   private String customerName;
   private int settleType;
   private FoodConsumeOrder order;



    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getSettleTime() {
        return settleTime;
    }

    public void setSettleTime(String settleTime) {
        this.settleTime = settleTime;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }


    public List<ConsumeFoodBean> getConsumeFoodList() {
        return consumeFoodList;
    }

    public void setConsumeFoodList(List<ConsumeFoodBean> consumeFoodList) {
        this.consumeFoodList = consumeFoodList;
    }

    public String getDeviceNo() {
        return deviceNo;
    }

    public void setDeviceNo(String deviceNo) {
        this.deviceNo = deviceNo;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getCustomerAccount() {
        return customerAccount;
    }

    public void setCustomerAccount(String customerAccount) {
        this.customerAccount = customerAccount;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public FoodConsumeOrder getOrder() {
        return order;
    }

    public void setOrder(FoodConsumeOrder order) {
        this.order = order;
    }


    public int getSettleType() {
        return settleType;
    }

    public void setSettleType(int settleType) {
        this.settleType = settleType;
    }
}