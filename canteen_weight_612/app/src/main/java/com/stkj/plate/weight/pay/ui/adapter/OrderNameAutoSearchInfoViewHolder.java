package com.stkj.plate.weight.pay.ui.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.stkj.plate.weight.R;
import com.stkj.plate.weight.pay.model.FoodConsumeBean;
import com.stkj.common.ui.adapter.holder.CommonRecyclerViewHolder;

/**
 * 商品自动搜索列表
 */
public class OrderNameAutoSearchInfoViewHolder extends CommonRecyclerViewHolder<FoodConsumeBean> {

    private TextView tvGoodsName;
    private View divider;

    public OrderNameAutoSearchInfoViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void initViews(View itemView) {
        tvGoodsName = (TextView) findViewById(R.id.tv_goods_name);
        divider = findViewById(R.id.divider);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDataAdapter.notifyItemClickListener(v, mData);
            }
        });
    }

    @Override
    public void initData(FoodConsumeBean data) {
        //String goodsImg = data.getGoodsImg();
        //String[] split = goodsImg.split(",");
//        if (split.length > 0) {
//            String picUrl = split[0];
//            if (!TextUtils.isEmpty(picUrl)) {
//                GlideApp.with(mContext).load(picUrl).placeholder(R.mipmap.icon_goods_default).into(ivGoodsPic);
//            } else {
//                ivGoodsPic.setImageResource(R.mipmap.icon_goods_default);
//            }
//        } else {
//            ivGoodsPic.setImageResource(R.mipmap.icon_goods_default);
//        }
        tvGoodsName.setText(data.getCustomerName());
        if (getDataPosition() == (getmDataAdapter().getDataList().size() - 1)){
            divider.setVisibility(View.GONE);
        }else {
            divider.setVisibility(View.VISIBLE);
        }
//        tvGoodsQrcode.setText("条码: " + data.getGoodsCode());
    }

    public static class Factory implements CommonRecyclerViewHolder.Factory<FoodConsumeBean> {
        @Override
        public CommonRecyclerViewHolder<FoodConsumeBean> createViewHolder(View itemView) {
            return new OrderNameAutoSearchInfoViewHolder(itemView);
        }

        @Override
        public int getLayResId() {
            return R.layout.item_goods_auto_search_info;
        }

        @Override
        public Class<FoodConsumeBean> getItemDataClass() {
            return FoodConsumeBean.class;
        }
    }
}
