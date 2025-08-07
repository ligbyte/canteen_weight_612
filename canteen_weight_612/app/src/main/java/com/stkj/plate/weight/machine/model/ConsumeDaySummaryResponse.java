package com.stkj.plate.weight.machine.model;

import com.stkj.plate.weight.pay.model.PriceYuan;

import java.util.List;

/**
 * Copyright (C), 2015-2025, 洛阳盛图科技有限公司
 * Author: Lime
 * Date: 2025/7/25 11:38
 * Description: 获取当日营业情Response
 */
public class ConsumeDaySummaryResponse {

    private PriceYuan total;
    private List<ConsumeDaySummaryBean> data;

    public PriceYuan getTotal() {
        return total;
    }

    public void setTotal(PriceYuan total) {
        this.total = total;
    }

    public List<ConsumeDaySummaryBean> getData() {
        return data;
    }

    public void setData(List<ConsumeDaySummaryBean> data) {
        this.data = data;
    }
}
