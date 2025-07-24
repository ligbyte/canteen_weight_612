package com.stkj.cashier.pay.goods.callback;



import com.stkj.cashier.pay.model.FoodConsumeBean;

import java.util.List;

public interface OrderNoAutoSearchListener {
    void onStartGetGoodsItemDetail(FoodConsumeBean goodsIdBaseListInfo);

    void onSuccessGetGoodsItemDetail(FoodConsumeBean saleListInfo);

    void onErrorGetGoodsItemDetail(FoodConsumeBean goodsIdBaseListInfo, String msg);

    void onSearchGoodsList(String key, List<FoodConsumeBean> goodsIdBaseListInfoList);
}
