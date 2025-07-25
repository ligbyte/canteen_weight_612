package com.stkj.plate.weight.machine.model;

import com.stkj.plate.weight.pay.model.PriceYuan;

/**
 * Copyright (C), 2015-2025, 洛阳盛图科技有限公司
 * Author: Lime
 * Date: 2025/7/23 15:37
 * Description: 绑盘接口返回
 */
public class AddFoodPreBean {

    private PriceYuan amount;
    private PriceYuan orderAmount;
    private CustomerInfo user;


    public PriceYuan getAmount() {
        return amount;
    }

    public void setAmount(PriceYuan amount) {
        this.amount = amount;
    }

    public PriceYuan getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(PriceYuan orderAmount) {
        this.orderAmount = orderAmount;
    }

    public CustomerInfo getUser() {
        return user;
    }

    public void setUser(CustomerInfo user) {
        this.user = user;
    }
}
