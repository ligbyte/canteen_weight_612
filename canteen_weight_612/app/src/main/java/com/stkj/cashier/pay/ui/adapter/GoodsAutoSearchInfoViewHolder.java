package com.stkj.cashier.pay.ui.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.stkj.cashier.R;
import com.stkj.cashier.setting.model.FoodInfoTable;
import com.stkj.common.glide.GlideApp;
import com.stkj.common.ui.adapter.holder.CommonRecyclerViewHolder;

/**
 * 商品自动搜索列表
 */
public class GoodsAutoSearchInfoViewHolder extends CommonRecyclerViewHolder<FoodInfoTable> {

    private TextView tvGoodsName;
    private View divider;

    public GoodsAutoSearchInfoViewHolder(@NonNull View itemView) {
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
    public void initData(FoodInfoTable data) {
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
        tvGoodsName.setText(data.getName());
        if (getDataPosition() == (getmDataAdapter().getDataList().size() - 1)){
            divider.setVisibility(View.GONE);
        }else {
            divider.setVisibility(View.VISIBLE);
        }
//        tvGoodsQrcode.setText("条码: " + data.getGoodsCode());
    }

    public static class Factory implements CommonRecyclerViewHolder.Factory<FoodInfoTable> {
        @Override
        public CommonRecyclerViewHolder<FoodInfoTable> createViewHolder(View itemView) {
            return new GoodsAutoSearchInfoViewHolder(itemView);
        }

        @Override
        public int getLayResId() {
            return R.layout.item_goods_auto_search_info;
        }

        @Override
        public Class<FoodInfoTable> getItemDataClass() {
            return FoodInfoTable.class;
        }
    }
}
