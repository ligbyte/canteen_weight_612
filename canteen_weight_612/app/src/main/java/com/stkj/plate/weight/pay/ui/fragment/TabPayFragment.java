package com.stkj.plate.weight.pay.ui.fragment;

import com.stkj.plate.weight.R;
import com.stkj.plate.weight.base.utils.EventBusUtils;
import com.stkj.plate.weight.pay.callback.OnConsumerModeListener;
import com.stkj.plate.weight.pay.data.PayConstants;
import com.stkj.plate.weight.pay.helper.ConsumerModeHelper;
import com.stkj.plate.weight.pay.model.ChangeConsumerModeEvent;
import com.stkj.plate.weight.pay.model.RefreshConsumerGoodsModeEvent;
import com.stkj.common.ui.fragment.BaseRecyclerFragment;
import com.stkj.common.utils.FragmentUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 收银页面
 */
public class TabPayFragment extends BaseRecyclerFragment implements OnConsumerModeListener {

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_tab_payment;
    }

    @Override
    protected void onFragmentResume(boolean isFirstOnResume) {
        EventBusUtils.registerEventBus(this);
        ConsumerModeHelper consumerModeHelper = mActivity.getWeakRefHolder(ConsumerModeHelper.class);
        if (isFirstOnResume) {
            changeConsumerMode(consumerModeHelper.getCurrentConsumerMode());
        }
        consumerModeHelper.addConsumerModeListener(this);
    }

    /**
     * 切换餐厅模式
     */
    private void changeConsumerMode(int mode) {
        if (mode == PayConstants.CONSUMER_AMOUNT_MODE) {
            //默认金额模式
            FragmentUtils.safeReplaceFragment(getChildFragmentManager(), new AmountConsumerFragment(), R.id.fl_pay_second_content);
        } else if (mode == PayConstants.CONSUMER_NUMBER_MODE) {
            //按次模式
            FragmentUtils.safeReplaceFragment(getChildFragmentManager(), new NumberConsumerFragment(), R.id.fl_pay_second_content);
        } else if (mode == PayConstants.CONSUMER_TAKE_MODE) {
            //取餐模式
            FragmentUtils.safeReplaceFragment(getChildFragmentManager(), new TakeMealConsumerFragment(), R.id.fl_pay_second_content);
        } else if (mode == PayConstants.CONSUMER_SEND_MODE) {
            //送餐模式
            FragmentUtils.safeReplaceFragment(getChildFragmentManager(), new TakeMealConsumerFragment(), R.id.fl_pay_second_content);
        } else if (mode == PayConstants.CONSUMER_WEIGHT_MODE) {
            //称重模式
            FragmentUtils.safeReplaceFragment(getChildFragmentManager(), new WeightConsumerFragment(), R.id.fl_pay_second_content);
        } else if (mode == PayConstants.CONSUMER_GOODS_MODE) {
            //商品模式
            FragmentUtils.safeReplaceFragment(getChildFragmentManager(), new GoodsConsumerFragment(), R.id.fl_pay_second_content);
        }else {
            //默认金额模式
            FragmentUtils.safeReplaceFragment(getChildFragmentManager(), new AmountConsumerFragment(), R.id.fl_pay_second_content);
        }

        EventBus.getDefault().post(new ChangeConsumerModeEvent(mode));

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshConsumerGoodsModeEvent(RefreshConsumerGoodsModeEvent eventBus) {
        if (eventBus.getPageMode() == 1){
            FragmentUtils.safeReplaceFragment(getChildFragmentManager(), new AddGoodsFragment(), R.id.fl_pay_second_content);
        } else {
            FragmentUtils.safeReplaceFragment(getChildFragmentManager(), new GoodsConsumerFragment(), R.id.fl_pay_second_content);
        }
    }


    @Override
    public void onChangeConsumerMode(int consumerMode, int lastConsumerMode) {
        changeConsumerMode(consumerMode);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        EventBusUtils.unRegisterEventBus(this);
    }
}
