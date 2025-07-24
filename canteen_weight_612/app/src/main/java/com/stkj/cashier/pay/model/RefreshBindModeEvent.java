package com.stkj.cashier.pay.model;

public class RefreshBindModeEvent {

    private int mode;


    public RefreshBindModeEvent(int mode) {
        this.mode = mode;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }
}
