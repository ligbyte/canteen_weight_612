package com.stkj.plate.weight.pay.goods.callback;

public interface OnGoodsCateSpecListener {

    void onGetCateSpecListEnd();

    default void onGetCateSpecError(String msg) {

    }
}
