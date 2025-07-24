package com.stkj.plate.weight.machine.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.stkj.plate.weight.MainApplication;


public class SharePreUtil {

    private static SharedPreferences mSharePre;

    private SharePreUtil() {
        mSharePre = MainApplication.instances.getSharedPreferences(
                "test_share", Context.MODE_PRIVATE);
    }

    private static class SysSharePresHolder {

        static final SharePreUtil INSTANCE = new SharePreUtil();
    }

    public static SharePreUtil getInstance() {
        return SysSharePresHolder.INSTANCE;
    }

    public void clear(){
        mSharePre.edit().clear().commit();
    }

    public int getWeightDevicesType(){
        return mSharePre.getInt("WeightDevicesType", 1);
    }

    public void setWeightDevicesType(int weightDevicesType){
        mSharePre.edit().putInt("WeightDevicesType", weightDevicesType).commit();
    }
    public void setReadCardSerialPath(String serialPath){
        mSharePre.edit().putString("ReadCardSerialPath", serialPath).commit();
    }

    public String getReadCardSerialPath(){
        return mSharePre.getString("ReadCardSerialPath", "");
    }

    public void setWeightSerialPath(String serialPath){
        mSharePre.edit().putString("WeightSerialPath", serialPath).commit();
    }

    public String getWeightSerialPath(){
        return mSharePre.getString("WeightSerialPath", "");
    }

    /**
     * 追零范围
     * @param zero
     */
    public void setPerformanceSettingZero(int zero){
        mSharePre.edit().putInt("PerformanceSettingZero", zero).commit();
    }

    /**
     * 追零范围
     */
    public int getPerformanceSettingZero(){
        return mSharePre.getInt("PerformanceSettingZero", 0x18);
    }
    /**
     * 动态跟踪时间
     * @param dynamicTrackingTime
     */
    public void setPerformanceSettingDynamicTrackingTime(int dynamicTrackingTime){
        mSharePre.edit().putInt("setPerformanceSettingDynamicTrackingTime", dynamicTrackingTime).commit();
    }

    /**
     * 动态跟踪时间
     */
    public int getPerformanceSettingDynamicTrackingTime(){
        return mSharePre.getInt("setPerformanceSettingDynamicTrackingTime", 0x30);
    }

    /**
     * 动态跟踪范围
     * @param dynamicTrackingScope
     */
    public void setPerformanceSettingDynamicTrackingScope(int dynamicTrackingScope){
        mSharePre.edit().putInt("setPerformanceSettingDynamicTrackingScope", dynamicTrackingScope).commit();
    }

    /**
     * 动态跟踪范围
     */
    public int getPerformanceSettingDynamicTrackingScope(){
        return mSharePre.getInt("setPerformanceSettingDynamicTrackingScope", 0x05);
    }

    /**
     * 第一段标定值
     * @param weight
     */
    public void setCalibrationSettingWeight1(int weight){
        mSharePre.edit().putInt("CalibrationSettingWeight1", weight).commit();
    }

    /**
     * 第一段标定值
     */
    public int getCalibrationSettingWeight1(){
        return mSharePre.getInt("CalibrationSettingWeight1", 0);
    }

    /**
     * 第二段标定值
     * @param weight
     */
    public void setCalibrationSettingWeight2(int weight){
        mSharePre.edit().putInt("CalibrationSettingWeight2", weight).commit();
    }

    /**
     * 第二段标定值
     */
    public int getCalibrationSettingWeight2(){
        return mSharePre.getInt("CalibrationSettingWeight2", 0);
    }

    /**
     * 设定开机归零
     * @param weight
     */
    public void setBootReset(int weight){
        mSharePre.edit().putInt("BootReset", weight).commit();
    }

    /**
     * 设定开机归零
     */
    public int getBootReset(){
        return mSharePre.getInt("BootReset", -1);
    }

    /**
     * 设定量程
     * @param weight
     */
    public void setRangeValue(int weight){
        mSharePre.edit().putInt("RangeValue", weight).commit();
    }

    /**
     * 设定量程
     */
    public int getRangeValue(){
        return mSharePre.getInt("RangeValue", 40000);
    }

    /**
     * 设定分度值
     * @param mDivisionValue
     */
    public void setDivisionValue(int mDivisionValue){
        mSharePre.edit().putInt("DivisionValue", mDivisionValue).commit();
    }

    /**
     * 设定分度值
     */
    public int getDivisionValue(){
        return mSharePre.getInt("DivisionValue", 0);
    }


}
