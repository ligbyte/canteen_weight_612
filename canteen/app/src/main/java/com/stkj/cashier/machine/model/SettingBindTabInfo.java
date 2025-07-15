package com.stkj.cashier.machine.model;

public class SettingBindTabInfo {

    public static final int TAB_TYPE_SERVER_ADDRESS = 1;
    public static final String TAB_NAME_SERVER_ADDRESS = "人脸管理";
    public static final int TAB_TYPE_DEVICE_SETTING = 2;
    public static final String TAB_NAME_DEVICE_SETTING = "设备ID";
    public static final int TAB_TYPE_WIFI_CONNECT = 3;
    public static final String TAB_NAME_WIFI_CONNECT = "应用设置";
    public static final int TAB_TYPE_PAYMENT_SETTING = 4;
    public static final String TAB_NAME_PAYMENT_SETTING = "用餐设置";




    private String tabName;
    private int tabImage;
    private int tabType;
    private boolean isSelected;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getTabType() {
        return tabType;
    }

    public void setTabType(int tabType) {
        this.tabType = tabType;
    }

    public SettingBindTabInfo() {
    }

    public SettingBindTabInfo(String tabName, int tabType, int tabImage) {
        this.tabName = tabName;
        this.tabType = tabType;
        this.tabImage = tabImage;
    }

    public SettingBindTabInfo(String tabName, int tabType) {
        this.tabName = tabName;
        this.tabType = tabType;
    }

    public String getTabName() {
        return tabName;
    }

    public void setTabName(String tabName) {
        this.tabName = tabName;
    }

    public int getTabImage() {
        return tabImage;
    }

    public void setTabImage(int tabImage) {
        this.tabImage = tabImage;
    }
}
