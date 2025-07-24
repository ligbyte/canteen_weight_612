package com.stkj.cashier.base.net;

import com.stkj.cashier.base.model.BaseNetResponse;
import com.stkj.cashier.base.model.BaseResponse;
import com.stkj.cashier.login.helper.LoginHelper;
import com.stkj.common.core.MainThreadHolder;
import com.stkj.common.net.callback.RetrofitConvertJsonListener;

/**
 * 系统json解析监听
 */
public class AppRetrofitJsonConvertListener implements RetrofitConvertJsonListener {
    @Override
    public void onConvertJson(Object o) {
        if (o instanceof BaseNetResponse) {
            BaseNetResponse baseNetResponse = (BaseNetResponse) o;

        } else if (o instanceof BaseResponse) {
            BaseResponse baseResponse = (BaseResponse) o;

        }
    }

    private void setNeedHandleLoginValid() {
        LoginHelper.INSTANCE.setNeedHandleLoginValid();
        //主线程处理登录失效弹窗
        MainThreadHolder.post(new Runnable() {
            @Override
            public void run() {
                LoginHelper.INSTANCE.handleLoginValid(true);
            }
        });
    }
}
