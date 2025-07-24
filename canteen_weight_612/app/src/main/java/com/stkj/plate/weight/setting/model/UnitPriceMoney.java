package com.stkj.plate.weight.setting.model;

public class UnitPriceMoney {
    private long cent;
    private String currency;
    private double amount;
    private String centFactor;


    public long getCent() {
        return cent;
    }

    public void setCent(long cent) {
        this.cent = cent;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCentFactor() {
        return centFactor;
    }

    public void setCentFactor(String centFactor) {
        this.centFactor = centFactor;
    }
}
