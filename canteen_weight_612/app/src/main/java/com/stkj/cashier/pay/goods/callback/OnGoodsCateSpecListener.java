package com.stkj.cashier.pay.goods.callback;

public interface OnGoodsCateSpecListener {

    void onGetCateSpecListEnd();

    default void onGetCateSpecError(String msg) {

    }
}
