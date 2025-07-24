package com.stkj.plate.weight.base.callback;

public interface AppNetCallback {
    default void onNetInitSuccess() {
    }

    default void onNetInitError(String message) {
    }
}
