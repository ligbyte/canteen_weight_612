package com.stkj.cashier.pay.model;

import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.stkj.cashier.R;
import com.stkj.cashier.base.ui.dialog.CommonAlertDialogFragment;
import com.stkj.cashier.base.ui.dialog.CommonInputDialogFragment;
import com.stkj.cashier.base.utils.EventBusUtils;
import com.stkj.cashier.base.utils.PriceUtils;
import com.stkj.cashier.pay.ui.fragment.GoodsConsumerFragment;
import com.stkj.cashier.setting.model.FoodInfoTable;
import com.stkj.common.glide.GlideApp;
import com.stkj.common.ui.adapter.holder.CommonRecyclerViewHolder;
import com.stkj.common.ui.toast.AppToast;
import com.stkj.common.ui.widget.shapelayout.ShapeTextView;
import com.stkj.common.utils.BigDecimalUtils;

import org.greenrobot.eventbus.EventBus;


/**
 * 商品入库库存列表
 */
public class GoodsInventoryListInfoViewHolder extends CommonRecyclerViewHolder<FoodInfoTable> {
    public static final int EVENT_CLICK = 1;
    public static final int EVENT_SELECTOR = 2;
    public static final int EVENT_REFRESH_PRICE = 3;
    public static final int EVENT_DELETE_ITEM = 4;

    private ImageView ivGoodsDelete;
    private TextView tvGoodsName;
    private TextView tv_count_kg;
    private TextView tvGoodsPrice;
    private ImageView ivCountMinus;
    private TextView tvGoodsCount;
    private ImageView ivCountPlus;
    private TextView tvGoodsTotalPrice;

    public GoodsInventoryListInfoViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void initViews(View itemView) {
        ivGoodsDelete = (ImageView) findViewById(R.id.iv_goods_delete);
        tv_count_kg = (TextView) findViewById(R.id.tv_count_kg);
        ivGoodsDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!GoodsConsumerFragment.canDelete){
                    EventBus.getDefault().post(new TTSSpeakEvent("请先取消结算"));
                    AppToast.toastMsg("请先取消结算");
                    return ;
                }
                CommonAlertDialogFragment.build()
                        .setAlertTitleTxt("提示")
                        .setAlertContentTxt("确认删除当前商品吗？")
                        .setLeftNavTxt("确定")
                        .setLeftNavClickListener(new CommonAlertDialogFragment.OnSweetClickListener() {
                            @Override
                            public void onClick(CommonAlertDialogFragment alertDialogFragment) {
                                mDataAdapter.removeData(mData);
                                mDataAdapter.notifyCustomItemEventListener(GoodsInventoryListInfoViewHolder.this, EVENT_DELETE_ITEM, mData);
                                EventBus.getDefault().post(new RefreshConsumerGoodsEvent(RefreshConsumerGoodsEvent.REFRESH_PRICE_MODE));
                                if (mDataAdapter.getDataList().size() <= 0){
                                    EventBus.getDefault().post(new GoodsClearAllEvent());
                                }
                            }
                        })
                        .setRightNavTxt("取消").show(mContext);
            }
        });
        tvGoodsName = (TextView) findViewById(R.id.tv_goods_name);
        tvGoodsPrice = (TextView) findViewById(R.id.tv_goods_price);

        ivCountMinus = (ImageView) findViewById(R.id.iv_count_minus);
        tvGoodsCount = (TextView) findViewById(R.id.tv_goods_count);
//        tvGoodsCount.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                CommonInputDialogFragment.build()
//                        .setTitle("数量")
//                        .setInputContent(tvGoodsCount.getText().toString())
////                        .setInputType(mData.isWeightGoods() ? CommonInputDialogFragment.INPUT_TYPE_NUMBER_DECIMAL : CommonInputDialogFragment.INPUT_TYPE_NUMBER)
//                        .setNeedLimitNumber(true)
//                        .setOnInputListener(new CommonInputDialogFragment.OnInputListener() {
//                            @Override
//                            public void onInputEnd(String input) {
//                                try {
//                                    double parseDouble = Double.parseDouble(input);
//                                    ivCountMinus.setEnabled(true);
//                                    ivCountPlus.setEnabled(true);
//                                    if (parseDouble <= 1) {
//                                        ivCountMinus.setEnabled(false);
//                                    } else if (parseDouble >= 9999) {
//                                        ivCountPlus.setEnabled(false);
//                                    }
//                                } catch (Throwable e) {
//                                    e.printStackTrace();
//                                }
//                                tvGoodsCount.setText(input);
////                                if (mData.isWeightGoods()) {
////                                    mData.setWeightGoodsCount(input);
////                                } else {
////                                    mData.setStandardGoodsCount(input);
////                                }
//                                refreshTotalPrice();
//                            }
//                        }).show(mContext);
//            }
//        });
        ivCountPlus = (ImageView) findViewById(R.id.iv_count_plus);
        tvGoodsTotalPrice = (TextView) findViewById(R.id.tv_xiaoji_price);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDataAdapter.notifyCustomItemEventListener(GoodsInventoryListInfoViewHolder.this, EVENT_CLICK, mData);
            }
        });
        ivCountMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!GoodsConsumerFragment.canDelete){
                    EventBus.getDefault().post(new TTSSpeakEvent("请先取消结算"));
                    AppToast.toastMsg("请先取消结算");
                    return ;
                }
                double newInputGoodsCount = mData.getType() == 2 ? BigDecimalUtils.sub(mData.getWeightGoodsCountWithDouble(), 1) : mData.getStandardGoodsCountWithInt() - 1;
                if (newInputGoodsCount <= 1) {
                    newInputGoodsCount = 1;
                    ivCountMinus.setEnabled(false);
                    //库存为 0 取消选中状态
//                    mData.setSelected(false);
                    ivGoodsDelete.setSelected(false);
                    mDataAdapter.notifyCustomItemEventListener(GoodsInventoryListInfoViewHolder.this, EVENT_SELECTOR, mData);
                }
                String newCountStr;
                if (mData.getPricingMethod() == 2) {
                    newCountStr = String.valueOf(newInputGoodsCount);
                    mData.setWeightGoodsCount(newCountStr);
                } else {
                    newCountStr = String.valueOf((int) newInputGoodsCount);
                    mData.setStandardGoodsCount(newCountStr);
                }
                tvGoodsCount.setText(newCountStr);
                refreshTotalPrice();
                EventBus.getDefault().post(new RefreshConsumerGoodsEvent(RefreshConsumerGoodsEvent.REFRESH_PRICE_MODE));
            }
        });
        ivCountPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!GoodsConsumerFragment.canDelete){
                    EventBus.getDefault().post(new TTSSpeakEvent("请先取消结算"));
                    AppToast.toastMsg("请先取消结算");
                    return ;
                }
                double newInputGoodsCount = mData.getPricingMethod() == 2 ? BigDecimalUtils.add(mData.getWeightGoodsCountWithDouble(), 1) : mData.getStandardGoodsCountWithInt() + 1;
                if (newInputGoodsCount >= 1) {
                    ivCountMinus.setEnabled(true);
                    if (newInputGoodsCount >= 9999) {
                        newInputGoodsCount = 9999;
                        ivCountPlus.setEnabled(false);
                    }
                }
                String newCountStr;
                if (mData.getPricingMethod() == 2) {
                    newCountStr = String.valueOf(newInputGoodsCount);
                    mData.setWeightGoodsCount(newCountStr);
                } else {
                    newCountStr = String.valueOf((int) newInputGoodsCount);
                    mData.setStandardGoodsCount(newCountStr);
                }
                tvGoodsCount.setText(newCountStr);
                refreshTotalPrice();
                EventBus.getDefault().post(new RefreshConsumerGoodsEvent(RefreshConsumerGoodsEvent.REFRESH_PRICE_MODE));
            }
        });
        mItemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!GoodsConsumerFragment.canDelete){
                    EventBus.getDefault().post(new TTSSpeakEvent("请先取消结算"));
                    AppToast.toastMsg("请先取消结算");
                    return true;
                }
                CommonAlertDialogFragment.build()
                        .setAlertTitleTxt("提示")
                        .setAlertContentTxt("确认删除当前商品吗？")
                        .setLeftNavTxt("确定")
                        .setLeftNavClickListener(new CommonAlertDialogFragment.OnSweetClickListener() {
                            @Override
                            public void onClick(CommonAlertDialogFragment alertDialogFragment) {
                                mDataAdapter.removeData(mData);
                                mDataAdapter.notifyCustomItemEventListener(GoodsInventoryListInfoViewHolder.this, EVENT_DELETE_ITEM, mData);
                                EventBus.getDefault().post(new RefreshConsumerGoodsEvent(RefreshConsumerGoodsEvent.REFRESH_PRICE_MODE));
                                if (mDataAdapter.getDataList().size() <= 0){
                                    EventBus.getDefault().post(new GoodsClearAllEvent());
                                }
                            }
                        })
                        .setRightNavTxt("取消").show(mContext);
                return true;
            }
        });
    }

    @Override
    public void initData(FoodInfoTable foodInfoTable) {

        tvGoodsName.setText(foodInfoTable.getName());
        tvGoodsPrice.setText(PriceUtils.formatPrice(foodInfoTable.getUnitPriceMoney_amount()));
        if (mData.getPricingMethod() == 2) {
            double inputGoodsCount = mData.getWeightGoodsCountWithDouble();
            ivCountMinus.setEnabled(inputGoodsCount > 1);
            ivCountPlus.setEnabled(inputGoodsCount <= 9999);
            tvGoodsCount.setText(PriceUtils.formatPrice(inputGoodsCount));
            ivCountMinus.setVisibility(View.INVISIBLE);
            ivCountPlus.setVisibility(View.GONE);
            tv_count_kg.setVisibility(View.VISIBLE);
            tvGoodsCount.setBackgroundColor(Color.parseColor("#00F5F5F5"));

        } else {
            int inputGoodsCount = mData.getStandardGoodsCountWithInt();
            ivCountMinus.setEnabled(inputGoodsCount > 1);
            ivCountPlus.setEnabled(inputGoodsCount <= 9999);
            tvGoodsCount.setText(String.valueOf(inputGoodsCount));
            ivCountMinus.setVisibility(View.VISIBLE);
            ivCountPlus.setVisibility(View.VISIBLE);
            tv_count_kg.setVisibility(View.GONE);
            tvGoodsCount.setBackgroundColor(Color.parseColor("#F5F5F5"));
            if (mData.getId().equals("-1")){
                ivCountPlus.setEnabled(false);
            }

        }
        refreshTotalPrice();
    }

    public void refreshTotalPrice() {
        double inputGoodsCount = mData.getPricingMethod() == 2 ? mData.getWeightGoodsCountWithDouble() : mData.getStandardGoodsCountWithInt();
        double inputGoodsInitPrice = mData.getInputGoodsInitPriceWithDouble();
        double totalPrice = BigDecimalUtils.mul(inputGoodsInitPrice, inputGoodsCount);
        tvGoodsTotalPrice.setText(PriceUtils.formatPrice(totalPrice));
//        //商品被选中时 需要刷新右侧入库的商品列表头部总价格
//        if (mData.isSelected()) {
//            mDataAdapter.notifyCustomItemEventListener(this, EVENT_REFRESH_PRICE, mData);
//        }
    }

    public static class Factory implements CommonRecyclerViewHolder.Factory<FoodInfoTable> {
        @Override
        public CommonRecyclerViewHolder<FoodInfoTable> createViewHolder(View itemView) {
            return new GoodsInventoryListInfoViewHolder(itemView);
        }

        @Override
        public int getLayResId() {
            return R.layout.item_goods_inventory_list_info;
        }

        @Override
        public Class<FoodInfoTable> getItemDataClass() {
            return FoodInfoTable.class;
        }
    }


    public TextView getTvGoodsCount() {
        return tvGoodsCount;
    }



}
