package com.stkj.plate.weight.machine.model;

import com.stkj.plate.weight.pay.model.PriceYuan;

/**
 * Copyright (C), 2015-2025, 洛阳盛图科技有限公司
 * Author: Lime
 * Date: 2025/8/7 15:57
 * Description: 当日营业额bean
 */
public class ConsumeDaySummaryBean {

    private int  feeTypeKey;
    private String feeTypeName;
    private PriceYuan fee;

    public int getFeeTypeKey() {
        return feeTypeKey;
    }

    public void setFeeTypeKey(int feeTypeKey) {
        this.feeTypeKey = feeTypeKey;
    }

    public String getFeeTypeName() {
        return feeTypeName;
    }

    public void setFeeTypeName(String feeTypeName) {
        this.feeTypeName = feeTypeName;
    }

    public PriceYuan getFee() {
        return fee;
    }

    public void setFee(PriceYuan fee) {
        this.fee = fee;
    }
}
