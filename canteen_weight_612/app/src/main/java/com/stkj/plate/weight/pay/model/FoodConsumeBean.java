package com.stkj.plate.weight.pay.model;

/**
 * 订单信息
 */
public class FoodConsumeBean {
    private String id;
    private String deleteFlag;
    private String createTime;
    private int number;
    private int isRefund;
    private String customerId;
    private double customerFee;
    private String tensntId;
    private PriceYuan customerFeeYuan;
    private String billNo;
    private String orderNo;
    private String type;
    private String customerName;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public int getIsRefund() {
        return isRefund;
    }

    public void setIsRefund(int isRefund) {
        this.isRefund = isRefund;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public double getCustomerFee() {
        return customerFee;
    }

    public void setCustomerFee(double customerFee) {
        this.customerFee = customerFee;
    }

    public String getTensntId() {
        return tensntId;
    }

    public void setTensntId(String tensntId) {
        this.tensntId = tensntId;
    }

    public PriceYuan getCustomerFeeYuan() {
        return customerFeeYuan;
    }

    public void setCustomerFeeYuan(PriceYuan customerFeeYuan) {
        this.customerFeeYuan = customerFeeYuan;
    }

    public String getBillNo() {
        return billNo;
    }

    public void setBillNo(String billNo) {
        this.billNo = billNo;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }


    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }


    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }
}
