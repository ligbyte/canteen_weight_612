package com.stkj.plate.weight.machine.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.stkj.plate.weight.R;
import com.stkj.plate.weight.machine.model.SettingBindTabInfo;
import com.stkj.common.ui.adapter.holder.CommonRecyclerViewHolder;

/**
 * 设置页顶部tab
 */
public class SettingBindTabInfoViewHolder extends CommonRecyclerViewHolder<SettingBindTabInfo> {

    private TextView sstvTabName;
    private ImageView sstvTabImage;

    public SettingBindTabInfoViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void initViews(View itemView) {
        sstvTabName = (TextView) findViewById(R.id.sstv_tab_name);
        sstvTabImage = (ImageView) findViewById(R.id.iv_icon);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDataAdapter.notifyItemClickListener(v, mData);
            }
        });
    }

    @Override
    public void initData(SettingBindTabInfo data) {
        sstvTabName.setText(data.getTabName());
        sstvTabImage.setImageResource(data.getTabImage());
    }

    public static class Factory implements CommonRecyclerViewHolder.Factory<SettingBindTabInfo> {
        @Override
        public CommonRecyclerViewHolder<SettingBindTabInfo> createViewHolder(View itemView) {
            return new SettingBindTabInfoViewHolder(itemView);
        }

        @Override
        public int getLayResId() {
            return R.layout.item_setting_bind_tab_info;
        }

        @Override
        public Class<SettingBindTabInfo> getItemDataClass() {
            return SettingBindTabInfo.class;
        }
    }
}