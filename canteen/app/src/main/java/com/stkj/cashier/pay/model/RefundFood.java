package com.stkj.cashier.pay.model;

public class RefundFood {
    
    private String id;
    private String tenantId;
    private String extJson;
    private String deleteFlag;
    private long createTime;
    private String createUser;
    private String updateTime;
    private String updateUser;
    private String foodId;
    private String billId;
    private String foodName;
    private int foodCount;
    private double foodWeight;
    private double foodUnitPrice;
    private double foodPrice;
    private int foodMethod;
    private PriceYuan foodPriceYuan;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getExtJson() {
        return extJson;
    }

    public void setExtJson(String extJson) {
        this.extJson = extJson;
    }

    public String getDeleteFlag() {
        return deleteFlag;
    }

    public void setDeleteFlag(String deleteFlag) {
        this.deleteFlag = deleteFlag;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public String getFoodId() {
        return foodId;
    }

    public void setFoodId(String foodId) {
        this.foodId = foodId;
    }

    public String getBillId() {
        return billId;
    }

    public void setBillId(String billId) {
        this.billId = billId;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public int getFoodCount() {
        return foodCount;
    }

    public void setFoodCount(int foodCount) {
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

    public double getFoodPrice() {
        return foodPrice;
    }

    public void setFoodPrice(double foodPrice) {
        this.foodPrice = foodPrice;
    }

    public int getFoodMethod() {
        return foodMethod;
    }

    public void setFoodMethod(int foodMethod) {
        this.foodMethod = foodMethod;
    }

    public PriceYuan getFoodPriceYuan() {
        return foodPriceYuan;
    }

    public void setFoodPriceYuan(PriceYuan foodPriceYuan) {
        this.foodPriceYuan = foodPriceYuan;
    }
}
