package com.stkj.cashier.pay.goods.callback;


import com.stkj.cashier.pay.goods.model.GoodsSpec;

import java.util.List;

public interface OnGetGoodsSpecListListener {
    void onGetSpecListSuccess(int goodsType, List<GoodsSpec> goodsSpecList);

    default void onGetSpecListError(int goodsType, String msg) {

    }
}
