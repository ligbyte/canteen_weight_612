package com.stkj.plate.weight.machine.model;

import com.stkj.plate.weight.pay.model.PriceYuan;

/**
 * Copyright (C), 2015-2025, 洛阳盛图科技有限公司
 * Author: Lime
 * Date: 2025/7/23 15:37
 * Description: 绑盘接口返回
 */
public class PlateBinding {

    private PriceYuan amount;
    private CustomerInfo customerInfo;


    public PriceYuan getAmount() {
        return amount;
    }

    public void setAmount(PriceYuan amount) {
        this.amount = amount;
    }

    public CustomerInfo getCustomerInfo() {
        return customerInfo;
    }

    public void setCustomerInfo(CustomerInfo customerInfo) {
        this.customerInfo = customerInfo;
    }
}
