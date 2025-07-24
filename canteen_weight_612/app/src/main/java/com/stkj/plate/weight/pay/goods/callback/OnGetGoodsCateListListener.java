package com.stkj.plate.weight.pay.goods.callback;


import com.stkj.plate.weight.pay.goods.model.GoodsCate;

import java.util.List;

public interface OnGetGoodsCateListListener {
    void onGetCateListSuccess(int goodsType, List<GoodsCate> goodsCateList);

    default void onGetCateListError(int goodsType, String msg) {

    }
}
