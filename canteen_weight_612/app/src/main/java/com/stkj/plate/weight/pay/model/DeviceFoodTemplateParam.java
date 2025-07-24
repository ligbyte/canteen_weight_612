package com.stkj.plate.weight.pay.model;

import java.util.HashMap;

public class DeviceFoodTemplateParam {
    private String id;
    private String name;
    private int type;
    private HashMap<String, String> categoryMap;
    private String deviceNo;
    private int status;
    private int pricingMethod;
    private String pricingUnit;
    private String tenantId;
    private double foodPrice;
    private String sort;
    private String foodImgPath;
    private String remark;
    private HashMap<String, String> packageFoodMap;

    public DeviceFoodTemplateParam(String id, String name, int type, HashMap<String, String> categoryMap, String deviceNo, int status, int pricingMethod, String pricingUnit, String tenantId, double foodPrice, String sort, String foodImgPath, String remark, HashMap<String, String> packageFoodMap) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.categoryMap = categoryMap;
        this.deviceNo = deviceNo;
        this.status = status;
        this.pricingMethod = pricingMethod;
        this.pricingUnit = pricingUnit;
        this.tenantId = tenantId;
        this.foodPrice = foodPrice;
        this.sort = sort;
        this.foodImgPath = foodImgPath;
        this.remark = remark;
        this.packageFoodMap = packageFoodMap;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public HashMap<String, String> getCategoryMap() {
        return categoryMap;
    }

    public void setCategoryMap(HashMap<String, String> categoryMap) {
        this.categoryMap = categoryMap;
    }

    public String getDeviceNo() {
        return deviceNo;
    }

    public void setDeviceNo(String deviceNo) {
        this.deviceNo = deviceNo;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getPricingMethod() {
        return pricingMethod;
    }

    public void setPricingMethod(int pricingMethod) {
        this.pricingMethod = pricingMethod;
    }

    public String getPricingUnit() {
        return pricingUnit;
    }

    public void setPricingUnit(String pricingUnit) {
        this.pricingUnit = pricingUnit;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public double getFoodPrice() {
        return foodPrice;
    }

    public void setFoodPrice(double foodPrice) {
        this.foodPrice = foodPrice;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getFoodImgPath() {
        return foodImgPath;
    }

    public void setFoodImgPath(String foodImgPath) {
        this.foodImgPath = foodImgPath;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public HashMap<String, String> getPackageFoodMap() {
        return packageFoodMap;
    }

    public void setPackageFoodMap(HashMap<String, String> packageFoodMap) {
        this.packageFoodMap = packageFoodMap;
    }
}
