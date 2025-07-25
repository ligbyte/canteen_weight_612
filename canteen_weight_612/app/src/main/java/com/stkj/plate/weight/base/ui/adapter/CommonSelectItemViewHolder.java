package com.stkj.plate.weight.base.ui.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.stkj.plate.weight.R;
import com.stkj.plate.weight.base.model.CommonSelectItem;
import com.stkj.common.ui.adapter.holder.CommonRecyclerViewHolder;

/**
 * 通用弹窗选择 item
 */
public class CommonSelectItemViewHolder extends CommonRecyclerViewHolder<CommonSelectItem> {

    private TextView tvName;
    private ImageView ivSelect;

    public CommonSelectItemViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void initViews(View itemView) {
        tvName = (TextView) findViewById(R.id.tv_name);
        ivSelect = (ImageView) findViewById(R.id.iv_select);
        mItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDataAdapter.notifyItemClickListener(v, mData);
            }
        });
    }

    @Override
    public void initData(CommonSelectItem data) {
        tvName.setText(data.getName());
        ivSelect.setSelected(data.isSelect());
    }

    public static class Factory implements CommonRecyclerViewHolder.Factory<CommonSelectItem> {
        @Override
        public CommonRecyclerViewHolder<CommonSelectItem> createViewHolder(View itemView) {
            return new CommonSelectItemViewHolder(itemView);
        }

        @Override
        public int getLayResId() {
            return R.layout.item_common_select_list;
        }

        @Override
        public Class<CommonSelectItem> getItemDataClass() {
            return CommonSelectItem.class;
        }
    }


}
