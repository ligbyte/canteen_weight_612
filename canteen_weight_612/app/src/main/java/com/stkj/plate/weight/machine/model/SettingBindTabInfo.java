package com.stkj.plate.weight.machine.model;

public class SettingBindTabInfo {

    public static final int TAB_TYPE_FOODS = 1;
    public static final String TAB_NAME_FOODS = "菜品设置";
    public static final int TAB_TYPE_ID = 2;
    public static final String TAB_NAME_ID = "设备ID";
    public static final int TAB_TYPE_WEIGHT = 3;
    public static final String TAB_NAME_WEIGHT = "称重校准";
    public static final int TAB_TYPE_COAST = 4;
    public static final String TAB_NAME_COAST = "消费设置";
    public static final int TAB_TYPE_WARNING = 5;
    public static final String TAB_NAME_WARNING = "报警设置";
    public static final int TAB_TYPE_SYSTEM = 6;
    public static final String TAB_NAME_SYSTEM = "系统设置";



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
