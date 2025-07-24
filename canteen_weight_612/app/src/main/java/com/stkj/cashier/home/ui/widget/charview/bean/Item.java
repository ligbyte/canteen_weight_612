package com.stkj.cashier.home.ui.widget.charview.bean;


/**
 * Copyright (C), 2015-2025, 洛阳盛图科技有限公司
 * FileName: Test
 * Author: Lime
 * Date: 2025/7/18 9:32
 * Description: 统计表单项数据
 */
public class Item {
    private String key;
    private float value;

    public Item () {
    }

    public Item (String key, float value) {
        this.key = key;
        this.value = value;
    }

    public String getKey () {
        return key;
    }

    public void setKey (String key) {
        this.key = key;
    }

    public float getValue () {
        return value;
    }

    public void setValue (float value) {
        this.value = value;
    }
}

