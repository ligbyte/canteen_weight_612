package com.stkj.plate.weight.pay.model;

import com.stkj.plate.weight.setting.model.FoodInfoTable;

import java.util.ArrayList;
import java.util.List;

public class ChangeConsumerModeEvent {


    private int mode;
    List<FoodInfoTable> foods = new ArrayList<>();

    public ChangeConsumerModeEvent(int mode) {
        this.mode = mode;
    }

    public ChangeConsumerModeEvent(int mode, List<FoodInfoTable> foods) {
        this.mode = mode;
        this.foods = foods;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public List<FoodInfoTable> getFoods() {
        return foods;
    }

    public void setFoods(List<FoodInfoTable> foods) {
        this.foods = foods;
    }
}
