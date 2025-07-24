package com.plate.deviceinterface.callback;

public interface OnMoneyBoxListener {
    void onBoxOpenSuccess();

    void onBoxOpenError(String message);
}
