package com.stkj.plate.weight.pay.model;

public class RefreshConsumerRefundModeEvent {

    private int pageMode;

    public RefreshConsumerRefundModeEvent(int pageMode) {
        this.pageMode = pageMode;
    }

    public int getPageMode() {
        return pageMode;
    }

    public void setPageMode(int pageMode) {
        this.pageMode = pageMode;
    }
}
