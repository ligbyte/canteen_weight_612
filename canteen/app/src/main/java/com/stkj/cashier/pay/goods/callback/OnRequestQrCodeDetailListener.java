package com.stkj.cashier.pay.goods.callback;


import com.stkj.cashier.pay.goods.model.GoodsQrCodeDetail;

public interface OnRequestQrCodeDetailListener {

    void onRequestDetailSuccess(GoodsQrCodeDetail data);

    void onRequestDetailError(String qrcode, String msg);
}
