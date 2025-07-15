package com.stkj.cashier.pay.model;

public class ConsumeFoodBean {

    private String id;
    private String tenantId;
    private String extJson;
    private String deleteFlag;
    private String createTime;
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
    private PriceYuan foodUnitPriceYuan;
    private PriceYuan foodPriceYuan;
    private PriceYuan refundFoodPriceYuan;
    private PriceYuan refundFoodPrice;
    private int refundFoodCount;

    private  boolean isSelected =  false;

    //输入的新的进货价
    private String inputGoodsInitPrice;
    //输入标准商品进货数
    private String standardGoodsCount = "0";
    //输入称重商品进货数
    private String weightGoodsCount = "0";

    /**
     * 是否是称重商品
     */
    public boolean isWeightGoods() {
        return foodMethod == 2;
    }

    public double getWeightGoodsCountWithDouble() {
        double weightGoodsCount = 0;
        try {
            weightGoodsCount = Double.parseDouble(this.weightGoodsCount);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return weightGoodsCount;
    }


    public int getStandardGoodsCountWithInt() {
        int standardGoodsCount = 0;
        try {
            standardGoodsCount = Integer.parseInt(this.standardGoodsCount);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return standardGoodsCount;
    }


    public double getInputGoodsInitPriceWithDouble() {
        if (foodUnitPriceYuan == null){
            return 0;
        }else {
            return foodUnitPriceYuan.getAmount();
        }
    }

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

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
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

    public PriceYuan getFoodUnitPriceYuan() {
        return foodUnitPriceYuan;
    }

    public void setFoodUnitPriceYuan(PriceYuan foodUnitPriceYuan) {
        this.foodUnitPriceYuan = foodUnitPriceYuan;
    }

    public PriceYuan getFoodPriceYuan() {
        return foodPriceYuan;
    }

    public void setFoodPriceYuan(PriceYuan foodPriceYuan) {
        this.foodPriceYuan = foodPriceYuan;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public PriceYuan getRefundFoodPriceYuan() {
        return refundFoodPriceYuan;
    }

    public void setRefundFoodPriceYuan(PriceYuan refundFoodPriceYuan) {
        this.refundFoodPriceYuan = refundFoodPriceYuan;
    }

    public int getRefundFoodCount() {
        return refundFoodCount;
    }

    public void setRefundFoodCount(int refundFoodCount) {
        this.refundFoodCount = refundFoodCount;
    }

    public String getInputGoodsInitPrice() {
        return inputGoodsInitPrice;
    }

    public void setInputGoodsInitPrice(String inputGoodsInitPrice) {
        this.inputGoodsInitPrice = inputGoodsInitPrice;
    }

    public String getStandardGoodsCount() {
        return standardGoodsCount;
    }

    public void setStandardGoodsCount(String standardGoodsCount) {
        this.standardGoodsCount = standardGoodsCount;
    }

    public String getWeightGoodsCount() {
        return weightGoodsCount;
    }

    public void setWeightGoodsCount(String weightGoodsCount) {
        this.weightGoodsCount = weightGoodsCount;
    }


    public PriceYuan getRefundFoodPrice() {
        return refundFoodPrice;
    }

    public void setRefundFoodPrice(PriceYuan refundFoodPrice) {
        this.refundFoodPrice = refundFoodPrice;
    }
}
