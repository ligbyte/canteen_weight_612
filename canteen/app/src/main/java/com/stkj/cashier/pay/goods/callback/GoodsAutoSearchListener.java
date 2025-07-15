package com.stkj.cashier.pay.goods.callback;


import com.stkj.cashier.pay.goods.model.GoodsIdBaseListInfo;
import com.stkj.cashier.pay.goods.model.GoodsSaleListInfo;
import com.stkj.cashier.setting.model.FoodInfoTable;

import java.util.List;

public interface GoodsAutoSearchListener {
    void onStartGetGoodsItemDetail(FoodInfoTable goodsIdBaseListInfo);

    void onSuccessGetGoodsItemDetail(FoodInfoTable saleListInfo);

    void onErrorGetGoodsItemDetail(FoodInfoTable goodsIdBaseListInfo, String msg);

    void onSearchGoodsList(String key, List<FoodInfoTable> goodsIdBaseListInfoList);
}
