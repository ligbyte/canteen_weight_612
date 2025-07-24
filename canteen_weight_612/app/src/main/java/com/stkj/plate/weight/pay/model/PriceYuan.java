package com.stkj.plate.weight.pay.model;

public class PriceYuan {

    private long cent;
    private String currency;
    private double amount;
    private long centFactor;

    public PriceYuan() {
    }

    public PriceYuan(long cent, String currency, double amount, long centFactor) {
        this.cent = cent;
        this.currency = currency;
        this.amount = amount;
        this.centFactor = centFactor;
    }

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

    public long getCentFactor() {
        return centFactor;
    }

    public void setCentFactor(long centFactor) {
        this.centFactor = centFactor;
    }
}
