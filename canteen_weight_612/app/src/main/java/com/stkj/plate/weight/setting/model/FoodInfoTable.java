package com.stkj.plate.weight.setting.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;

/**
 * 食物数据
 */
@Entity
public class FoodInfoTable {

    @Id
    private String id;
    private String tenantId;
    @Index
    private String deleteFlag;
    private String createTime;
    private String createUser;
    private String updateTime;
    private String updateUser;
    private String restaurantId;
    @Index
    private String categoryMap;
    @Index
    private String name;
    private String imgpath;
    private String deviceId;
    @Index
    private int pricingMethod;
    private String pricingUnit;
    @Index
    private int type;
    private long unitPriceMoney_cent;
    private String unitPriceMoney_currency;
    private double unitPriceMoney_amount;
    private String unitPriceMoney_centFactor;
    @Index
    private int sort;

    private boolean isSelected = false;
    @Index
    private int status;
    private String templateId;
    private String remark;
    //输入的新的进货价
    private String inputGoodsInitPrice;
    //输入标准商品进货数
    private String standardGoodsCount = "0";
    //输入称重商品进货数
    private String weightGoodsCount = "0";





    @Generated(hash = 1363088206)
    public FoodInfoTable(String id, String tenantId, String deleteFlag,
            String createTime, String createUser, String updateTime,
            String updateUser, String restaurantId, String categoryMap, String name,
            String imgpath, String deviceId, int pricingMethod, String pricingUnit,
            int type, long unitPriceMoney_cent, String unitPriceMoney_currency,
            double unitPriceMoney_amount, String unitPriceMoney_centFactor,
            int sort, boolean isSelected, int status, String templateId,
            String remark, String inputGoodsInitPrice, String standardGoodsCount,
            String weightGoodsCount) {
        this.id = id;
        this.tenantId = tenantId;
        this.deleteFlag = deleteFlag;
        this.createTime = createTime;
        this.createUser = createUser;
        this.updateTime = updateTime;
        this.updateUser = updateUser;
        this.restaurantId = restaurantId;
        this.categoryMap = categoryMap;
        this.name = name;
        this.imgpath = imgpath;
        this.deviceId = deviceId;
        this.pricingMethod = pricingMethod;
        this.pricingUnit = pricingUnit;
        this.type = type;
        this.unitPriceMoney_cent = unitPriceMoney_cent;
        this.unitPriceMoney_currency = unitPriceMoney_currency;
        this.unitPriceMoney_amount = unitPriceMoney_amount;
        this.unitPriceMoney_centFactor = unitPriceMoney_centFactor;
        this.sort = sort;
        this.isSelected = isSelected;
        this.status = status;
        this.templateId = templateId;
        this.remark = remark;
        this.inputGoodsInitPrice = inputGoodsInitPrice;
        this.standardGoodsCount = standardGoodsCount;
        this.weightGoodsCount = weightGoodsCount;
    }

    @Generated(hash = 431731772)
    public FoodInfoTable() {
    }





    /**
     * 是否是称重商品
     */
    public boolean isWeightGoods() {
        return pricingMethod == 2;
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
//        double inputGoodsInitPrice = 0;
//        try {
//            inputGoodsInitPrice = Double.parseDouble(this.);
//        } catch (Throwable e) {
//            e.printStackTrace();
//        }
        return unitPriceMoney_amount;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTenantId() {
        return this.tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getDeleteFlag() {
        return this.deleteFlag;
    }

    public void setDeleteFlag(String deleteFlag) {
        this.deleteFlag = deleteFlag;
    }

    public String getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getCreateUser() {
        return this.createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getUpdateTime() {
        return this.updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getUpdateUser() {
        return this.updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public String getRestaurantId() {
        return this.restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getCategoryMap() {
        return this.categoryMap;
    }

    public void setCategoryMap(String categoryMap) {
        this.categoryMap = categoryMap;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImgpath() {
        return this.imgpath;
    }

    public void setImgpath(String imgpath) {
        this.imgpath = imgpath;
    }

    public String getDeviceId() {
        return this.deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public int getPricingMethod() {
        return this.pricingMethod;
    }

    public void setPricingMethod(int pricingMethod) {
        this.pricingMethod = pricingMethod;
    }

    public String getPricingUnit() {
        return this.pricingUnit;
    }

    public void setPricingUnit(String pricingUnit) {
        this.pricingUnit = pricingUnit;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getUnitPriceMoney_cent() {
        return this.unitPriceMoney_cent;
    }

    public void setUnitPriceMoney_cent(long unitPriceMoney_cent) {
        this.unitPriceMoney_cent = unitPriceMoney_cent;
    }

    public String getUnitPriceMoney_currency() {
        return this.unitPriceMoney_currency;
    }

    public void setUnitPriceMoney_currency(String unitPriceMoney_currency) {
        this.unitPriceMoney_currency = unitPriceMoney_currency;
    }

    public double getUnitPriceMoney_amount() {
        return this.unitPriceMoney_amount;
    }

    public void setUnitPriceMoney_amount(double unitPriceMoney_amount) {
        this.unitPriceMoney_amount = unitPriceMoney_amount;
    }

    public String getUnitPriceMoney_centFactor() {
        return this.unitPriceMoney_centFactor;
    }

    public void setUnitPriceMoney_centFactor(String unitPriceMoney_centFactor) {
        this.unitPriceMoney_centFactor = unitPriceMoney_centFactor;
    }

    public int getSort() {
        return this.sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTemplateId() {
        return this.templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getInputGoodsInitPrice() {
        return this.inputGoodsInitPrice;
    }

    public void setInputGoodsInitPrice(String inputGoodsInitPrice) {
        this.inputGoodsInitPrice = inputGoodsInitPrice;
    }

    public String getStandardGoodsCount() {
        return this.standardGoodsCount;
    }

    public void setStandardGoodsCount(String standardGoodsCount) {
        this.standardGoodsCount = standardGoodsCount;
    }

    public String getWeightGoodsCount() {
        return this.weightGoodsCount;
    }

    public void setWeightGoodsCount(String weightGoodsCount) {
        this.weightGoodsCount = weightGoodsCount;
    }


    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean getIsSelected() {
        return this.isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
}
