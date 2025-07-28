package com.stkj.plate.weight.pay.ui.adapter;

import android.content.Context;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.stkj.plate.weight.R;
import com.stkj.plate.weight.base.utils.PriceUtils;
import com.stkj.plate.weight.setting.model.FoodInfoTable;

public class FoodGridAdapter extends BaseQuickAdapter<FoodInfoTable, BaseViewHolder> {

    private Context context;

    public FoodGridAdapter(Context context)
    {
        super(R.layout.item_list_cai_pin);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder holder, FoodInfoTable item) {
        holder.setText(R.id.tv_cai_name, item.getName());
        holder.setText(R.id.tv_cai_price, "￥" + PriceUtils.formatPrice(item.getUnitPriceMoney_amount()) + "/1000g");

        if (item.getHasChoose() == 1){
            holder.setBackgroundResource(R.id.rl_root,R.mipmap.bg_selected);
        }else {
            holder.setBackgroundResource(R.id.rl_root,R.mipmap.bg_unselect);
        }
//        if (!TextUtils.isEmpty(item.getImgpath())) {
//            Glide.with(context).load(item.getImgpath()).into((RoundImageView) holder.getView(R.id.iv_cai_icon));
//        }else {
//            ((RoundImageView) holder.getView(R.id.iv_cai_icon)).setImageResource(R.mipmap.ic_cai_default);
//        }
//
//        if (item.getPricingMethod() == 2) {
//            if (MainApplication.hasWeight) {
//                holder.setTextColor(R.id.tv_cai_name, Color.parseColor("#333333"));
//                holder.setTextColor(R.id.tv_cai_price, Color.parseColor("#333333"));
//            } else {
//                holder.setTextColor(R.id.tv_cai_name, Color.parseColor("#CCCCCC"));
//                holder.setTextColor(R.id.tv_cai_price, Color.parseColor("#CCCCCC"));
//            }
//        }else {
//            holder.setTextColor(R.id.tv_cai_name, Color.parseColor("#333333"));
//            holder.setTextColor(R.id.tv_cai_price, Color.parseColor("#333333"));
//        }

//        //((CheckBox)holder.getView(R.id.cb_status)).setChecked(item.isChecked());
//        if (item.isChecked()){
//            ((RelativeLayout)holder.getView(R.id.rl_root)).setSelected(true);
//            ((TextView)holder.getView(R.id.tv_status)).setSelected(true);
//        }else {
//            ((RelativeLayout)holder.getView(R.id.rl_root)).setSelected(false);
//            ((TextView)holder.getView(R.id.tv_status)).setSelected(false);
//        }

    }
}
