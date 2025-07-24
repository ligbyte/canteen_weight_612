package com.stkj.plate.weight.stat.model;

public class RefundFoodInfo {

    private String foodId;
    private String foodName;
    private int foodCount;
    private double foodWeight;
    private int foodMethod;
    private double foodUnitPrice;
    private double foodTotalPrice;



    public RefundFoodInfo(String foodId, String foodName, int foodCount, double foodWeight, int foodMethod, double foodUnitPrice, double foodTotalPrice) {
        this.foodId = foodId;
        this.foodName = foodName;
        this.foodCount = foodCount;
        this.foodWeight = foodWeight;
        this.foodMethod = foodMethod;
        this.foodUnitPrice = foodUnitPrice;
        this.foodTotalPrice = foodTotalPrice;
    }

    public String getFoodId() {
        return foodId;
    }

    public void setFoodId(String foodId) {
        this.foodId = foodId;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public int getFoodCount() {
        return foodCount;
    }

    public void setFoodCount(int foodCount) {
        this.foodCount = foodCount;
    }

    public double getFoodWeight() {
        return foodWeight;
    }

    public void setFoodWeight(double foodWeight) {
        this.foodWeight = foodWeight;
    }

    public int getFoodMethod() {
        return foodMethod;
    }

    public void setFoodMethod(int foodMethod) {
        this.foodMethod = foodMethod;
    }

    public double getFoodUnitPrice() {
        return foodUnitPrice;
    }

    public void setFoodUnitPrice(double foodUnitPrice) {
        this.foodUnitPrice = foodUnitPrice;
    }

    public double getFoodTotalPrice() {
        return foodTotalPrice;
    }

    public void setFoodTotalPrice(double foodTotalPrice) {
        this.foodTotalPrice = foodTotalPrice;
    }
}
