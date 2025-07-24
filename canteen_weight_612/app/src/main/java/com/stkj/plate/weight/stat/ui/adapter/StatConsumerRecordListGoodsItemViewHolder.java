package com.stkj.plate.weight.stat.ui.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.stkj.plate.weight.R;
import com.stkj.plate.weight.base.utils.PriceUtils;
import com.stkj.plate.weight.base.utils.StarUtils;
import com.stkj.plate.weight.pay.model.FoodConsumeBean;
import com.stkj.common.ui.adapter.holder.CommonRecyclerViewHolder;

/**
 * 统计页面消费账单列表
 */
public class StatConsumerRecordListGoodsItemViewHolder extends CommonRecyclerViewHolder<FoodConsumeBean> {

    public static final int EVENT_CLICK = 1;
    private TextView tvName;
    private TextView tvAccount;
    private TextView tvFeeType;
    private TextView tvAmount;
    private TextView tvPayType;
    private TextView tvTime;

    public StatConsumerRecordListGoodsItemViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void initViews(View itemView) {
        tvName = (TextView) findViewById(R.id.tv_name);
        tvAccount = (TextView) findViewById(R.id.tv_account);
        tvFeeType = (TextView) findViewById(R.id.tv_fee_type);
        tvAmount = (TextView) findViewById(R.id.tv_amount);
        tvPayType = (TextView) findViewById(R.id.tv_pay_type);
        tvTime = (TextView) findViewById(R.id.tv_time);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDataAdapter.notifyCustomItemEventListener(StatConsumerRecordListGoodsItemViewHolder.this, EVENT_CLICK, mData);
            }
        });
    }

    @Override
    public void initData(FoodConsumeBean data) {
        if (!TextUtils.isEmpty(data.getBillNo())) {
            tvName.setText(data.getCustomerName());
            tvAccount.setText(StarUtils.phoneStar(data.getBillNo()));
            tvFeeType.setText("1");
            tvAmount.setText(PriceUtils.formatPrice(data.getCustomerFeeYuan().getAmount()));
            tvPayType.setText("查看详情");
            tvTime = (TextView) findViewById(R.id.tv_time);
            tvTime.setText(data.getCreateTime());
        }

    }

    public static class Factory implements CommonRecyclerViewHolder.Factory<FoodConsumeBean> {
        @Override
        public CommonRecyclerViewHolder<FoodConsumeBean> createViewHolder(View itemView) {
            return new StatConsumerRecordListGoodsItemViewHolder(itemView);
        }

        @Override
        public int getLayResId() {
            return R.layout.item_stat_consumer_record_list_goods;
        }

        @Override
        public Class<FoodConsumeBean> getItemDataClass() {
            return FoodConsumeBean.class;
        }
    }


}
