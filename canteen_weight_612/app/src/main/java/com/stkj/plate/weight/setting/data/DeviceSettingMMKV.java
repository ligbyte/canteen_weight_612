package com.stkj.plate.weight.setting.data;

import com.tencent.mmkv.MMKV;

/**
 * 设备设置
 */
public class DeviceSettingMMKV {

    public static final String MMKV_NAME = "device_setting";
    public static final String KEY_SCREEN_PROTECT_NAME = "screen_protect_time";
    public static final String KEY_OPEN_SYS_LOG = "open_sys_log";
    public static final String KEY_WARNING_SWITCH = "key_warning_switch";
    public static final String KEY_COAST_WARNING_SWITCH = "key_coast_warning_switch";

    public static final String KEY_WARNING_TIME = "key_warning_time";
    public static final String KEY_WARNING_G = "key_warning_g";
    public static final String KEY_BEFORE_CHOOSE_FOOD = "key_before_choose_food";

    public static void putBeforeChooseFood(String choose_food) {
        MMKV serverSettingMMKV = getSettingMMKV();
        serverSettingMMKV.putString(KEY_BEFORE_CHOOSE_FOOD, choose_food);
    }

    public static String getBeforeChooseFood() {
        MMKV serverSettingMMKV = getSettingMMKV();
        return serverSettingMMKV.decodeString(KEY_BEFORE_CHOOSE_FOOD, "");
    }

    public static void putScreenProtectTime(int interval) {
        MMKV serverSettingMMKV = getSettingMMKV();
        serverSettingMMKV.putInt(KEY_SCREEN_PROTECT_NAME, interval);
    }

    public static int getScreenProtectTime() {
        MMKV serverSettingMMKV = getSettingMMKV();
        return serverSettingMMKV.decodeInt(KEY_SCREEN_PROTECT_NAME, -1);
    }


    public static void putWarningTime(int interval) {
        MMKV serverSettingMMKV = getSettingMMKV();
        serverSettingMMKV.putInt(KEY_SCREEN_PROTECT_NAME, interval);
    }

    public static int getWarningTime() {
        MMKV serverSettingMMKV = getSettingMMKV();
        return serverSettingMMKV.decodeInt(KEY_WARNING_TIME, 15);
    }

    public static void putWarningg(int interval) {
        MMKV serverSettingMMKV = getSettingMMKV();
        serverSettingMMKV.putInt(KEY_WARNING_G, interval);
    }

    public static int getWarningg() {
        MMKV serverSettingMMKV = getSettingMMKV();
        return serverSettingMMKV.decodeInt(KEY_WARNING_G, 5);
    }

    public static boolean isOpenSysLog() {
        MMKV mmkv = getSettingMMKV();
        return mmkv.getBoolean(KEY_OPEN_SYS_LOG, false);
    }

    public static void putOpenSysLog(boolean openSysLog) {
        getSettingMMKV().putBoolean(KEY_OPEN_SYS_LOG, openSysLog);
    }


    public static boolean isOpenWarning() {
        MMKV mmkv = getSettingMMKV();
        return mmkv.getBoolean(KEY_WARNING_SWITCH, true);
    }

    public static void putOpenWarning(boolean openWarning) {
        getSettingMMKV().putBoolean(KEY_WARNING_SWITCH, openWarning);
    }

    public static boolean isOpenCoastWarning() {
        MMKV mmkv = getSettingMMKV();
        return mmkv.getBoolean(KEY_COAST_WARNING_SWITCH, true);
    }

    public static void putOpenCoastWarning(boolean openWarning) {
        getSettingMMKV().putBoolean(KEY_COAST_WARNING_SWITCH, openWarning);
    }


    public static MMKV getSettingMMKV() {
        return MMKV.mmkvWithID(MMKV_NAME);
    }
}
