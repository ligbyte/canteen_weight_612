package com.stkj.plate.weight.pay.model;

import java.util.List;

/**
 * 历史订单
 */
public class FoodBillStatDayPageResponse {

    private List<FoodBillRecords> records;
    private PriceYuan  actualTotalRefundFee;
    private PriceYuan  actualTotalConsumeFee;
    private int total;
    private int current;
    private int totalPage;
    private int size;
    private int actualTotalOrderNoCount;
    private int actualTotalFoodCount;


    public List<FoodBillRecords> getRecords() {
        return records;
    }

    public void setRecords(List<FoodBillRecords> records) {
        this.records = records;
    }

    public PriceYuan getActualTotalRefundFee() {
        return actualTotalRefundFee;
    }

    public void setActualTotalRefundFee(PriceYuan actualTotalRefundFee) {
        this.actualTotalRefundFee = actualTotalRefundFee;
    }

    public PriceYuan getActualTotalConsumeFee() {
        return actualTotalConsumeFee;
    }

    public void setActualTotalConsumeFee(PriceYuan actualTotalConsumeFee) {
        this.actualTotalConsumeFee = actualTotalConsumeFee;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }


    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getActualTotalOrderNoCount() {
        return actualTotalOrderNoCount;
    }

    public void setActualTotalOrderNoCount(int actualTotalOrderNoCount) {
        this.actualTotalOrderNoCount = actualTotalOrderNoCount;
    }

    public int getActualTotalFoodCount() {
        return actualTotalFoodCount;
    }

    public void setActualTotalFoodCount(int actualTotalFoodCount) {
        this.actualTotalFoodCount = actualTotalFoodCount;
    }
}