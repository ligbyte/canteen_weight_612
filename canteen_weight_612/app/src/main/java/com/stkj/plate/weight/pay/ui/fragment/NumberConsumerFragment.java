package com.stkj.plate.weight.pay.ui.fragment;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.stkj.plate.weight.R;
import com.stkj.plate.weight.base.utils.EventBusUtils;
import com.stkj.plate.weight.consumer.ConsumerManager;
import com.stkj.plate.weight.pay.callback.OnGetIntervalCardTypeListener;
import com.stkj.plate.weight.pay.data.PayConstants;
import com.stkj.plate.weight.pay.helper.ConsumerModeHelper;
import com.stkj.plate.weight.pay.model.IntervalCardType;
import com.stkj.plate.weight.pay.model.ModifyBalanceResult;
import com.stkj.plate.weight.pay.model.RefreshConsumerNumberModeEvent;
import com.stkj.common.rx.AutoDisposeUtils;
import com.stkj.common.rx.DefaultDisposeObserver;
import com.stkj.common.rx.DefaultObserver;
import com.stkj.common.rx.RxTransformerUtils;
import com.stkj.common.utils.FragmentUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Observable;

/**
 * 按次消费
 */
public class NumberConsumerFragment extends BasePayHelperFragment {

    private ImageView ivDefaultFace;
    private TextView tvNoConsumer;
    private TextView tvAmount;
    private TextView tvBillCount;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_number_consumer;
    }

    @Override
    protected void initViews(View rootView) {
        ivDefaultFace = (ImageView) findViewById(R.id.iv_default_face);
        tvAmount = (TextView) findViewById(R.id.tv_amount);
        tvBillCount = (TextView) findViewById(R.id.tv_bill_count);
        tvNoConsumer = (TextView) findViewById(R.id.tv_no_consumer);
    }

    private OnGetIntervalCardTypeListener onGetIntervalCardTypeListener = new OnGetIntervalCardTypeListener() {
        @Override
        public void onGetIntervalCardType(IntervalCardType intervalCardType) {
            ConsumerManager.INSTANCE.setPayPrice(getNumberRealPayMoney(), false);
        }
    };

    @Override
    protected void onFragmentResume(boolean isFirstOnResume) {
        if (isFirstOnResume) {
            EventBusUtils.registerEventBus(this);
            FragmentUtils.safeReplaceFragment(getChildFragmentManager(), new ConsumerRecordListFragment(), R.id.fl_consumer_list_content);
            //获取按次时段金额信息
            ConsumerModeHelper consumerModeHelper = mActivity.getWeakRefHolder(ConsumerModeHelper.class);
            consumerModeHelper.requestIntervalCardType();
            consumerModeHelper.addGetIntervalCardTypeListener(onGetIntervalCardTypeListener);
            Observable.timer(1, TimeUnit.SECONDS).compose(RxTransformerUtils.mainSchedulers()).to(AutoDisposeUtils.onDestroyDispose(this)).subscribe(new DefaultObserver<Long>() {
                @Override
                protected void onSuccess(Long aLong) {
                    ConsumerManager.INSTANCE.resetFaceConsumerLayout();
                    goToPay(getNumberRealPayMoney());
                }
            });
        }
    }

    @Override
    public void onPaySuccess(Map<String, String> payRequest, ModifyBalanceResult modifyBalanceResult) {
        super.onPaySuccess(payRequest, modifyBalanceResult);
        if (delayShowNoConsumerObserver != null) {
            delayShowNoConsumerObserver.dispose();
        }
        refreshConsumerLay(modifyBalanceResult);
    }

    @Override
    protected void onPayCancel(int payType) {
        speakTTSVoice("用户已取消");
        stopToPay();
        Observable.timer(3, TimeUnit.SECONDS).compose(RxTransformerUtils.mainSchedulers()).to(AutoDisposeUtils.onDestroyDispose(this)).subscribe(new DefaultObserver<Long>() {
            @Override
            protected void onSuccess(Long aLong) {
                goToPay(getNumberRealPayMoney());
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshConsumerNumberModeEvent(RefreshConsumerNumberModeEvent eventBus) {
       onPayCancel(-1);
    }


    /**
     * 获取按次消费时段金额
     */
    private String getNumberRealPayMoney() {
        ConsumerModeHelper consumerModeHelper = mActivity.getWeakRefHolder(ConsumerModeHelper.class);
        return consumerModeHelper.getNumberConsumerPayMoney();
    }

    @Override
    protected int getDeductionType() {
        return PayConstants.DEDUCTION_TYPE_NUMBER;
    }

    @Override
    protected void delayedToPayStatus() {
        goToPay(getNumberRealPayMoney());
    }

    private void refreshConsumerLay(ModifyBalanceResult modifyBalanceResult) {

    }

    private DefaultDisposeObserver<Long> delayShowNoConsumerObserver;

    /**
     * 延迟6秒展示未消费
     */
    private void delayShowNoConsumerLayout() {
        if (delayShowNoConsumerObserver != null) {
            delayShowNoConsumerObserver.dispose();
        }
        delayShowNoConsumerObserver = new DefaultDisposeObserver<Long>() {
            @Override
            protected void onSuccess(Long aLong) {
                ivDefaultFace.setImageResource(R.mipmap.icon_no_person);
                tvNoConsumer.setVisibility(View.VISIBLE);
                tvAmount.setVisibility(View.GONE);
                tvBillCount.setVisibility(View.GONE);
            }
        };
        Observable.timer(5, TimeUnit.SECONDS).compose(RxTransformerUtils.mainSchedulers()).to(AutoDisposeUtils.onDestroyDispose(this)).subscribe(delayShowNoConsumerObserver);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        stopToPay();
        if (delayShowNoConsumerObserver != null) {
            delayShowNoConsumerObserver.dispose();
            delayShowNoConsumerObserver = null;
        }
        ConsumerModeHelper consumerModeHelper = mActivity.getWeakRefHolder(ConsumerModeHelper.class);
        consumerModeHelper.removeGetIntervalCardTypeListener(onGetIntervalCardTypeListener);
        EventBusUtils.unRegisterEventBus(this);
    }
}
