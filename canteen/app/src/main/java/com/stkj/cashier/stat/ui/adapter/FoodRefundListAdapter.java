package com.stkj.cashier.stat.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.stkj.cashier.R;
import com.stkj.cashier.base.utils.PriceUtils;
import com.stkj.cashier.base.utils.StarUtils;
import com.stkj.cashier.pay.model.ConsumeFoodBean;
import com.stkj.cashier.pay.model.FoodConsumeBean;
import com.stkj.cashier.pay.model.GoodsInventoryListInfoViewHolder;
import com.stkj.cashier.pay.model.RefreshConsumerGoodsEvent;
import com.stkj.cashier.pay.model.RefreshRefundGoodsEvent;
import com.stkj.common.utils.BigDecimalUtils;

import org.greenrobot.eventbus.EventBus;

public class FoodRefundListAdapter extends BaseQuickAdapter<ConsumeFoodBean, BaseViewHolder> {

    public final static String TAG = "FoodRefundListAdapter";
    public FoodRefundListAdapterListener foodRefundListAdapterListener;
    private Context context;

    public FoodRefundListAdapter(Context context)
    {
        super(R.layout.item_food_refund_list);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder holder, ConsumeFoodBean item) {

        Log.d(TAG, "limeConsumeFoodBean item: " + JSON.toJSONString(item));
        try {
            holder.setText(R.id.tv_refund_name, item.getFoodName());

            holder.setText(R.id.tv_refund_price, PriceUtils.formatPrice(item.getFoodUnitPriceYuan().getAmount()));


            if (item.getFoodMethod() == 2){
                if (item.getRefundFoodCount() > 0) {
                    holder.setText(R.id.tv_goods_count, "0");
                }else {
                    holder.setText(R.id.tv_goods_count, "1");
                }
                holder.setText(R.id.tv_refund_count, String.valueOf(item.getFoodCount()));
                holder.setText(R.id.tv_refund_ktkje, PriceUtils.formatPrice(item.getRefundFoodCount() == 1  ? 0 :item.getFoodWeight() * item.getFoodUnitPriceYuan().getAmount()));
                holder.setText(R.id.tv_refund_ytkje, PriceUtils.formatPrice(item.getRefundFoodCount() * item.getFoodWeight() * item.getFoodUnitPriceYuan().getAmount()));
                holder.setText(R.id.tv_refund_ytkjs, String.valueOf(item.getRefundFoodCount()));
            }else {
                holder.setText(R.id.tv_goods_count, String.valueOf(item.getFoodCount() - item.getRefundFoodCount()));
                holder.setText(R.id.tv_refund_count, String.valueOf(item.getFoodCount()));
                holder.setText(R.id.tv_refund_ktkje, PriceUtils.formatPrice((item.getFoodCount() - item.getRefundFoodCount()) * item.getFoodUnitPriceYuan().getAmount()));
                holder.setText(R.id.tv_refund_ytkje, PriceUtils.formatPrice(item.getRefundFoodCount() * item.getFoodUnitPriceYuan().getAmount()));
                holder.setText(R.id.tv_refund_ytkjs, String.valueOf(item.getRefundFoodCount()));
                if (!item.getStandardGoodsCount().equals("0")){
                    holder.setText(R.id.tv_goods_count, item.getStandardGoodsCount());
                    holder.setText(R.id.tv_refund_ktkje, PriceUtils.formatPrice(Double.parseDouble(item.getStandardGoodsCount()) * item.getFoodUnitPriceYuan().getAmount()));
                }
            }


            if (item.isSelected()) {
                ((ImageView) holder.getView(R.id.iv_checkbox)).setImageResource(R.mipmap.icon_goods_selected);
            } else {
                ((ImageView) holder.getView(R.id.iv_checkbox)).setImageResource(R.mipmap.icon_goods_unselect);
            }


            ImageView ivCountMinus = (ImageView) holder.getView(R.id.iv_count_minus);
            ImageView ivCountPlus = (ImageView) holder.getView(R.id.iv_count_plus);

            if (item.getFoodMethod() == 2) {
//                if (item.getStandardGoodsCountWithInt() <= 0) {
//                    ivCountMinus.setEnabled(false);
//                    ivCountPlus.setEnabled(false);
//                }
                Log.d(TAG, "limeivCountMinus=========: " + 77);
                ivCountMinus.setEnabled(false);
                ivCountPlus.setEnabled(false);
                ivCountMinus.setVisibility(View.VISIBLE);
                ivCountPlus.setVisibility(View.VISIBLE);
                holder.getView(R.id.tv_count_kg).setVisibility(View.GONE);

            } else {
                Log.d(TAG, "limeivCountMinus 85: " + item.getStandardGoodsCountWithInt());
                Log.d(TAG, "limeivCountMinus========= 94: " + ((item.getFoodCount() - item.getRefundFoodCount())));
                if (Integer.parseInt(((TextView)holder.getView(R.id.tv_goods_count)).getText().toString().trim()) <= 1) {
                    Log.d(TAG, "limeivCountMinus=========: " + 87);
                    ivCountMinus.setEnabled(false);
                    if((item.getFoodCount() - item.getRefundFoodCount()) <= 1) {
                        ivCountPlus.setEnabled(false);
                    }else {
                        ivCountPlus.setEnabled(true);
                    }

                }else {
                    if (Integer.parseInt(((TextView)holder.getView(R.id.tv_goods_count)).getText().toString().trim()) >= (item.getFoodCount() - item.getRefundFoodCount())) {
                        ivCountPlus.setEnabled(false);
                        ivCountMinus.setEnabled(true);
                    }else {
                        Log.d(TAG, "limeivCountMinus: " + 97);
                        ivCountMinus.setEnabled(true);
                        ivCountPlus.setEnabled(true);
                    }
                }



                ivCountMinus.setVisibility(View.VISIBLE);
                ivCountPlus.setVisibility(View.VISIBLE);
                holder.getView(R.id.tv_count_kg).setVisibility(View.GONE);
            }


            ivCountMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    double newInputGoodsCount =  item.getFoodMethod() == 2  ? BigDecimalUtils.sub(item.getWeightGoodsCountWithDouble(), 1) : item.getStandardGoodsCountWithInt() - 1;
                    if (newInputGoodsCount <= 1) {
                        newInputGoodsCount = 1;
                        ivCountMinus.setEnabled(false);
                        Log.d(TAG, "limeivCountMinus=========: " + 116);
                        //库存为 0 取消选中状态
//                    mData.setSelected(false);
//                    ivGoodsDelete.setSelected(false);
//                    mDataAdapter.notifyCustomItemEventListener(GoodsInventoryListInfoViewHolder.this, EVENT_SELECTOR, mData);
                    }

                    if (newInputGoodsCount < (item.getFoodCount() - item.getRefundFoodCount())) {
                        ivCountPlus.setEnabled(true);
                    }

                    String newCountStr;
                    if (item.getFoodMethod() == 2) {
                        newCountStr = String.valueOf(newInputGoodsCount);
                        item.setWeightGoodsCount(newCountStr);
                        holder.setText(R.id.tv_refund_ktkje, PriceUtils.formatPrice(item.getRefundFoodCount() == 1  ? 0 :item.getFoodWeight() * item.getFoodUnitPriceYuan().getAmount()));
                        holder.setText(R.id.tv_goods_count, "1");
                    } else {
                        newCountStr = String.valueOf((int) newInputGoodsCount);
                        item.setStandardGoodsCount(newCountStr);
                        holder.setText(R.id.tv_refund_ktkje, PriceUtils.formatPrice((Double.parseDouble(newCountStr)) * item.getFoodUnitPriceYuan().getAmount()));
                        holder.setText(R.id.tv_goods_count, newCountStr);
                    }

                    if (foodRefundListAdapterListener != null){
                        foodRefundListAdapterListener.countStand(getItemPosition(item),newCountStr);
                    }
                    EventBus.getDefault().post(new RefreshRefundGoodsEvent());
                }
            });
            ivCountPlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    double newInputGoodsCount =  (item.getFoodMethod() == 2)  ? BigDecimalUtils.add(item.getWeightGoodsCountWithDouble(), 1) : item.getStandardGoodsCountWithInt() + 1;
                    if (newInputGoodsCount >= 1) {
                        ivCountMinus.setEnabled(true);
                        if (newInputGoodsCount >= (item.getFoodCount() - item.getRefundFoodCount())) {
                            newInputGoodsCount = (item.getFoodCount() - item.getRefundFoodCount());
                            ivCountPlus.setEnabled(false);
                        }
                    }
                    String newCountStr;
                    if (item.getFoodMethod() == 2) {
                        newCountStr = String.valueOf(newInputGoodsCount);
                        item.setWeightGoodsCount(newCountStr);
                        holder.setText(R.id.tv_refund_ktkje, PriceUtils.formatPrice(item.getRefundFoodCount() == 1  ? 0 :item.getFoodWeight() * item.getFoodUnitPriceYuan().getAmount()));
                        Log.d(TAG, "limeConsumeFoodBean 152: " +PriceUtils.formatPrice(item.getRefundFoodCount() == 1  ? 0 :item.getFoodWeight() * item.getFoodPrice()));
                        holder.setText(R.id.tv_goods_count, "1");
                    } else {
                        newCountStr = String.valueOf((int) newInputGoodsCount);
                        item.setStandardGoodsCount(newCountStr);
                        holder.setText(R.id.tv_refund_ktkje, PriceUtils.formatPrice((Double.parseDouble(newCountStr)) * item.getFoodUnitPriceYuan().getAmount()));
                        holder.setText(R.id.tv_goods_count, newCountStr);
                    }

                    if (foodRefundListAdapterListener != null){
                        foodRefundListAdapterListener.countStand(getItemPosition(item),newCountStr);
                    }
                    EventBus.getDefault().post(new RefreshRefundGoodsEvent());
                }
            });


        }catch (Exception e){
            Log.e(TAG, "limeFoodOrderDetailAdapter 126: " + e.getMessage());
        }
    }

    public FoodRefundListAdapterListener getFoodRefundListAdapterListener() {
        return foodRefundListAdapterListener;
    }

    public void setFoodRefundListAdapterListener(FoodRefundListAdapterListener foodRefundListAdapterListener) {
        this.foodRefundListAdapterListener = foodRefundListAdapterListener;
    }

    public interface FoodRefundListAdapterListener{
       void countStand(int position,String newCount);
    }


}
