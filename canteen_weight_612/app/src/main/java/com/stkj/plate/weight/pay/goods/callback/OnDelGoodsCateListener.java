package com.stkj.plate.weight.pay.goods.callback;

/**
 * 删除商品分类回调
 */
public interface OnDelGoodsCateListener {
    void onDelCateSuccess(String cateName);

    default void onDelCateError(String cateName, String msg) {

    }
}
