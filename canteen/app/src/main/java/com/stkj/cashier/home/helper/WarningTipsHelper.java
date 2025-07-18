package com.stkj.cashier.home.helper;


import com.stkj.cashier.home.ui.widget.WarningTipsView;

public enum WarningTipsHelper {
    INSTANCE;

    private WarningTipsView mainTipsView;
    private WarningTipsView consumerTipsView;

    public void setMainTipsView(WarningTipsView mainTipsView) {
        this.mainTipsView = mainTipsView;
    }

    public void setConsumerTipsView(WarningTipsView consumerTipsView) {
        this.consumerTipsView = consumerTipsView;
    }

    public void setTips(String tips) {
        if (mainTipsView != null) {
            mainTipsView.setTips(tips);
        }
        if (consumerTipsView != null) {
            consumerTipsView.setTips(tips);
        }
    }

    public void setTipsDelayHide(String tips) {
        if (mainTipsView != null) {
            mainTipsView.setTips(tips);
            mainTipsView.delayHideTipsView();
        }
        if (consumerTipsView != null) {
            consumerTipsView.setTips(tips);
            consumerTipsView.delayHideTipsView();
        }
    }

    public void setLoading(String loading) {
        if (mainTipsView != null) {
            mainTipsView.setLoading(loading);
        }
        if (consumerTipsView != null) {
            consumerTipsView.setLoading(loading);
        }
    }

    public void delayHideTipsView() {
        if (mainTipsView != null) {
            mainTipsView.delayHideTipsView();
        }
        if (consumerTipsView != null) {
            consumerTipsView.delayHideTipsView();
        }
    }

    public void hideTipsView() {
        if (mainTipsView != null) {
            mainTipsView.hideTipsView();
        }
        if (consumerTipsView != null) {
            consumerTipsView.hideTipsView();
        }
    }
}