package com.stkj.cashier.consumer;

import android.content.Context;

import com.stkj.cashier.base.callback.OnConsumerConfirmListener;
import com.stkj.cashier.consumer.callback.ConsumerController;
import com.stkj.cashier.consumer.callback.ConsumerListener;
import com.stkj.cashier.machine.bind.TabWeightHomeFragment;
import com.stkj.cashier.setting.model.FacePassPeopleInfo;

/**
 * 面向消费者屏幕
 */
public enum ConsumerManager implements ConsumerController {
    INSTANCE;
    private TabWeightHomeFragment consumerPresentation;

    /**
     * 显示消费者页面
     */
    public void showConsumer(Context context, TabWeightHomeFragment tabBindHomeFragment, ConsumerListener consumerListener) {

        consumerPresentation = tabBindHomeFragment;
        consumerPresentation.setConsumerListener(consumerListener);
    }

    public void setConsumerListener(ConsumerListener consumerListener) {
        if (consumerPresentation != null) {
            consumerPresentation.setConsumerListener(consumerListener);
        }
    }

    public void setFacePassConfirmListener(OnConsumerConfirmListener facePassConfirmListener) {
        if (consumerPresentation != null) {
            consumerPresentation.setFacePassConfirmListener(facePassConfirmListener);
        }
    }

    @Override
    public void setFacePreview(boolean preview) {
        if (consumerPresentation != null) {
            consumerPresentation.setFacePreview(preview);
        }
    }

    @Override
    public void setConsumerTips(String tips) {
        setConsumerTips(tips, 0);
    }

    @Override
    public void setConsumerTips(String tips, int consumerPro) {
        if (consumerPresentation != null) {
            consumerPresentation.setConsumerTips(tips, consumerPro);
        }
    }

    @Override
    public void setConsumerAuthTips(String tips) {
        if (consumerPresentation != null) {
            consumerPresentation.setConsumerAuthTips(tips);
        }
    }

    @Override
    public boolean isConsumerAuthTips() {
        if (consumerPresentation != null) {
            return consumerPresentation.isConsumerAuthTips();
        }
        return false;
    }

    @Override
    public void setPayPrice(String payPrice, boolean canCancelPay) {
        if (consumerPresentation != null) {
            consumerPresentation.setPayPrice(payPrice, canCancelPay);
        }
    }

    @Override
    public void setCanCancelPay(boolean showCancelPay) {
        if (consumerPresentation != null) {
            consumerPresentation.setCanCancelPay(showCancelPay);
        }
    }

    @Override
    public void setConsumerConfirmFaceInfo(FacePassPeopleInfo facePassPeopleInfo, boolean needConfirm, int consumerType) {
        if (consumerPresentation != null) {
            consumerPresentation.setConsumerConfirmFaceInfo(facePassPeopleInfo, needConfirm, consumerType);
        }
    }

    @Override
    public void setConsumerConfirmCardInfo(String cardNumber, boolean needConfirm) {
        if (consumerPresentation != null) {
            consumerPresentation.setConsumerConfirmCardInfo(cardNumber, needConfirm);
        }
    }

    @Override
    public void setConsumerConfirmScanInfo(String scanData, boolean needConfirm) {
        if (consumerPresentation != null) {
            consumerPresentation.setConsumerConfirmScanInfo(scanData, needConfirm);
        }
    }

    @Override
    public void setConsumerTakeMealWay() {
        if (consumerPresentation != null) {
            consumerPresentation.setConsumerTakeMealWay();
        }
    }

    @Override
    public void resetFaceConsumerLayout() {
        if (consumerPresentation != null) {
            consumerPresentation.resetFaceConsumerLayout();
        }
    }

    @Override
    public void setNormalConsumeStatus() {
        if (consumerPresentation != null) {
            consumerPresentation.setNormalConsumeStatus();
        }
    }

    @Override
    public void setPayConsumeStatus() {
        if (consumerPresentation != null) {
            consumerPresentation.setPayConsumeStatus();
        }
    }

    public void clearConsumerPresentation() {
        consumerPresentation = null;
    }
}