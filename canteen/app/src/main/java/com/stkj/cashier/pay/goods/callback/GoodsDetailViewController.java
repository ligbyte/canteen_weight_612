package com.stkj.cashier.pay.goods.callback;


import com.stkj.cashier.pay.goods.model.GoodsEditBaseInfo;
import com.stkj.cashier.pay.goods.model.GoodsQrCodeDetail;

public interface GoodsDetailViewController {

    GoodsEditBaseInfo getGoodsDetailEditInfo();

    void setGoodsDetailEditMode();

    void setGoodsQrCodeInfo(GoodsQrCodeDetail goodsQrCodeInfo);

}
