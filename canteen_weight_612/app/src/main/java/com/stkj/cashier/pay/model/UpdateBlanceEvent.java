package com.stkj.cashier.pay.model;

public class UpdateBlanceEvent {

    private String blance;

    public UpdateBlanceEvent(String blance) {
        this.blance = blance;
    }

    public String getBlance() {
        return blance;
    }

    public void setBlance(String blance) {
        this.blance = blance;
    }
}
