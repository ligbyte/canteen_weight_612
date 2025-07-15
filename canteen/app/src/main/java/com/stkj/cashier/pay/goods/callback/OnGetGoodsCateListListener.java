package com.stkj.cashier.pay.goods.callback;


import com.stkj.cashier.pay.goods.model.GoodsCate;

import java.util.List;

public interface OnGetGoodsCateListListener {
    void onGetCateListSuccess(int goodsType, List<GoodsCate> goodsCateList);

    default void onGetCateListError(int goodsType, String msg) {

    }
}
