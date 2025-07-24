package com.stkj.plate.weight.stat.ui.fragment;

import com.stkj.plate.weight.R;
import com.stkj.plate.weight.base.utils.EventBusUtils;
import com.stkj.plate.weight.pay.callback.OnConsumerModeListener;
import com.stkj.plate.weight.pay.data.PayConstants;
import com.stkj.plate.weight.pay.helper.ConsumerModeHelper;
import com.stkj.plate.weight.pay.model.RefreshConsumerRefundModeEvent;
import com.stkj.common.ui.fragment.BaseRecyclerFragment;
import com.stkj.common.utils.FragmentUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 餐厅统计
 */
public class TabStatSwitchFragment extends BaseRecyclerFragment implements OnConsumerModeListener {

    private TabStatGoodsFragment tabStatGoodsFragment;
    private TabStatFragment tabStatFragment;
    private RefundListFragment refundListFragment;

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
       if (mode == PayConstants.CONSUMER_GOODS_MODE) {
            //商品模式
            FragmentUtils.safeReplaceFragment(getChildFragmentManager(), new TabStatGoodsFragment(), R.id.fl_pay_second_content);
        }else {
            //默认金额模式
            FragmentUtils.safeReplaceFragment(getChildFragmentManager(), new TabStatFragment(), R.id.fl_pay_second_content);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshConsumerRefundModeEvent(RefreshConsumerRefundModeEvent eventBus) {
        if (eventBus.getPageMode() == 1){
            FragmentUtils.safeReplaceFragment(getChildFragmentManager(), new RefundListFragment(), R.id.fl_pay_second_content);
        } else {
            FragmentUtils.safeReplaceFragment(getChildFragmentManager(), new TabStatGoodsFragment(), R.id.fl_pay_second_content);
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
