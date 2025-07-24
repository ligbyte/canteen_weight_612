package com.stkj.plate.weight.pay.model;

public class RefreshConsumerGoodsEvent {

    public final static int REFRESH_PRICE_MODE = 0;

    private int mode;


    public RefreshConsumerGoodsEvent(int mode) {
        this.mode = mode;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }
}
