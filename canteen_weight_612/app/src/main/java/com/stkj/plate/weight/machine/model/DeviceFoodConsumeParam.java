package com.stkj.plate.weight.machine.model;

import java.math.BigDecimal;

/**
 * Copyright (C), 2015-2025, 洛阳盛图科技有限公司
 * Author: Lime
 * Date: 2025/7/25 11:30
 * Description: 加菜参数
 */
public class DeviceFoodConsumeParam {

    //"设备编号"
    private String deviceNo;

    //"用户编号"
    private String customerId;

    //"餐盘编码"
    private String plateCode;

    //"菜品ID"
    private String foodId;

    //"菜品名称"
    private String foodName;

    //"菜品件数"
    private Integer foodCount;

    //"菜品重量 单位 g"
    private double foodWeight;

    //"菜品单价 元/kg"
    private double foodUnitPrice;

    //"菜品总价 元"
    private double foodTotalPrice;

    public DeviceFoodConsumeParam(String deviceNo, String customerId, String plateCode, String foodId, String foodName, Integer foodCount, double foodWeight, double foodUnitPrice, double foodTotalPrice) {
        this.deviceNo = deviceNo;
        this.customerId = customerId;
        this.plateCode = plateCode;
        this.foodId = foodId;
        this.foodName = foodName;
        this.foodCount = foodCount;
        this.foodWeight = foodWeight;
        this.foodUnitPrice = foodUnitPrice;
        this.foodTotalPrice = foodTotalPrice;
    }

    public String getDeviceNo() {
        return deviceNo;
    }

    public void setDeviceNo(String deviceNo) {
        this.deviceNo = deviceNo;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getPlateCode() {
        return plateCode;
    }

    public void setPlateCode(String plateCode) {
        this.plateCode = plateCode;
    }

    public String getFoodId() {
        return foodId;
    }

    public void setFoodId(String foodId) {
        this.foodId = foodId;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public Integer getFoodCount() {
        return foodCount;
    }

    public void setFoodCount(Integer foodCount) {
        this.foodCount = foodCount;
    }

    public double getFoodWeight() {
        return foodWeight;
    }

    public void setFoodWeight(double foodWeight) {
        this.foodWeight = foodWeight;
    }

    public double getFoodUnitPrice() {
        return foodUnitPrice;
    }

    public void setFoodUnitPrice(double foodUnitPrice) {
        this.foodUnitPrice = foodUnitPrice;
    }

    public double getFoodTotalPrice() {
        return foodTotalPrice;
    }

    public void setFoodTotalPrice(double foodTotalPrice) {
        this.foodTotalPrice = foodTotalPrice;
    }
}
