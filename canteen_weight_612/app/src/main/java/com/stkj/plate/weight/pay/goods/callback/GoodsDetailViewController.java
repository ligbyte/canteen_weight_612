package com.stkj.plate.weight.pay.goods.callback;


import com.stkj.plate.weight.pay.goods.model.GoodsEditBaseInfo;
import com.stkj.plate.weight.pay.goods.model.GoodsQrCodeDetail;

public interface GoodsDetailViewController {

    GoodsEditBaseInfo getGoodsDetailEditInfo();

    void setGoodsDetailEditMode();

    void setGoodsQrCodeInfo(GoodsQrCodeDetail goodsQrCodeInfo);

}
