package com.stkj.cashier.pay.goods.callback;


import com.stkj.cashier.pay.goods.model.GoodsSpec;

/**
 * 添加商品规格回调
 */
public interface OnAddGoodsSpecListener {
    void onAddSpecSuccess(GoodsSpec goodsSpec);

    default void onAddSpecError(String specName, String msg) {

    }
}
