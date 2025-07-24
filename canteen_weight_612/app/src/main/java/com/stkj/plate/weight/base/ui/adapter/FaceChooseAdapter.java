package com.stkj.plate.weight.base.ui.adapter;

import android.content.Context;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.stkj.plate.weight.R;
import com.stkj.plate.weight.base.model.FaceChooseItemEntity;
import com.stkj.plate.weight.base.utils.StarUtils;

public class FaceChooseAdapter extends BaseQuickAdapter<FaceChooseItemEntity, BaseViewHolder> {

    private Context context;

    public FaceChooseAdapter(Context context)
    {
        super(R.layout.item_list_face_choose);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder holder, FaceChooseItemEntity item) {
        holder.setText(R.id.tv_user_name, StarUtils.nameStar(item.getUsername()));
        holder.setText(R.id.tv_user_phone, StarUtils.phoneStar(item.getPhone()));
        Glide.with(context).load(item.getHeadUrl()).into((ImageView) holder.getView(R.id.iv_icon));
        //((CheckBox)holder.getView(R.id.cb_status)).setChecked(item.isChecked());
        if (item.isChecked()){
            ((RelativeLayout)holder.getView(R.id.rl_root)).setSelected(true);
            ((ImageView)holder.getView(R.id.iv_status)).setImageResource(R.mipmap.box_checked);
        }else {
            ((RelativeLayout)holder.getView(R.id.rl_root)).setSelected(false);
            ((ImageView)holder.getView(R.id.iv_status)).setImageDrawable(null);
        }

    }
}
