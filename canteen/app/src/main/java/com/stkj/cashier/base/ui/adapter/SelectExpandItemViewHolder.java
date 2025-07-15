package com.stkj.cashier.base.ui.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.stkj.cashier.R;
import com.stkj.cashier.base.model.CommonExpandItem;
import com.stkj.cashier.setting.model.FoodInfoTable;
import com.stkj.common.ui.adapter.holder.CommonRecyclerViewHolder;
import com.stkj.common.ui.widget.linelayout.LineLinearLayout;

/**
 * 通用下拉选择 item
 */
public class SelectExpandItemViewHolder extends CommonRecyclerViewHolder<FoodInfoTable> {

    public static final int EVENT_CLICK = 1;
    public static final int EVENT_EDIT = 2;
    public static final int EVENT_DEL = 3;

    private TextView tvName;
    private ImageView ivDel;
    private LineLinearLayout lineLinearLayout;

    public SelectExpandItemViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void initViews(View itemView) {
        lineLinearLayout = (LineLinearLayout) itemView;
        tvName = (TextView) findViewById(R.id.tv_name);
        ivDel = (ImageView) findViewById(R.id.iv_del);
    }

    @Override
    public void initData(FoodInfoTable data) {
        lineLinearLayout.setLineBottom(getDataPosition() != (mDataAdapter.getItemCount() - 1));
        tvName.setText(data.getName());
        mItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDataAdapter.notifyCustomItemEventListener(SelectExpandItemViewHolder.this, EVENT_CLICK, mData);
            }
        });

        if (data.isSelected()) {
            ivDel.setVisibility(View.VISIBLE);
        } else {
            ivDel.setVisibility(View.GONE);
        }

    }

    public static class Factory implements CommonRecyclerViewHolder.Factory<FoodInfoTable> {
        @Override
        public CommonRecyclerViewHolder<FoodInfoTable> createViewHolder(View itemView) {
            return new SelectExpandItemViewHolder(itemView);
        }

        @Override
        public int getLayResId() {
            return R.layout.item_select_expand_list;
        }

        @Override
        public Class<FoodInfoTable> getItemDataClass() {
            return FoodInfoTable.class;
        }
    }


}
