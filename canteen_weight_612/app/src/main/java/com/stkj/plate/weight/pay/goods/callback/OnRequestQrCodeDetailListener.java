package com.stkj.plate.weight.pay.goods.callback;


import com.stkj.plate.weight.pay.goods.model.GoodsQrCodeDetail;

public interface OnRequestQrCodeDetailListener {

    void onRequestDetailSuccess(GoodsQrCodeDetail data);

    void onRequestDetailError(String qrcode, String msg);
}
