package com.stkj.plate.weight.pay.model;

public class RefreshConsumerGoodsModeEvent {

    private int pageMode;

    public RefreshConsumerGoodsModeEvent(int pageMode) {
        this.pageMode = pageMode;
    }

    public int getPageMode() {
        return pageMode;
    }

    public void setPageMode(int pageMode) {
        this.pageMode = pageMode;
    }
}
