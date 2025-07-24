package com.stkj.plate.weight.login.callback;

public interface LoginCallback {
    void onLoginSuccess();

    default void onLoginError(String msg) {
    }
}
