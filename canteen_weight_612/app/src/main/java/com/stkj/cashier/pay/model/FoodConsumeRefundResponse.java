package com.stkj.cashier.pay.model;


import java.util.List;

/**
 * 退款记录
 */
public class FoodConsumeRefundResponse {

   private String settleTime;
   private List<RefundFood> refundFoodList;
   private int refundTotalFoodCount;
   private PriceYuan refundTotalPrice;


    public String getSettleTime() {
        return settleTime;
    }

    public void setSettleTime(String settleTime) {
        this.settleTime = settleTime;
    }

    public List<RefundFood> getRefundFoodList() {
        return refundFoodList;
    }

    public void setRefundFoodList(List<RefundFood> refundFoodList) {
        this.refundFoodList = refundFoodList;
    }

    public int getRefundTotalFoodCount() {
        return refundTotalFoodCount;
    }

    public void setRefundTotalFoodCount(int refundTotalFoodCount) {
        this.refundTotalFoodCount = refundTotalFoodCount;
    }

    public PriceYuan getRefundTotalPrice() {
        return refundTotalPrice;
    }

    public void setRefundTotalPrice(PriceYuan refundTotalPrice) {
        this.refundTotalPrice = refundTotalPrice;
    }
}