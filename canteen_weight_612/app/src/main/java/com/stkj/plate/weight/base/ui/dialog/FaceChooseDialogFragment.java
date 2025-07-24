package com.stkj.plate.weight.base.ui.dialog;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.stkj.plate.weight.MainApplication;
import com.stkj.plate.weight.R;
import com.stkj.plate.weight.base.model.FaceChooseItemEntity;
import com.stkj.plate.weight.base.ui.adapter.FaceChooseAdapter;
import com.stkj.common.ui.fragment.BaseDialogFragment;
import com.stkj.common.ui.toast.AppToast;
import com.stkj.common.ui.widget.common.ItemDecorationH;
import com.stkj.common.ui.widget.shapelayout.ShapeTextView;

import java.util.List;

/**
 * 人脸选择弹窗
 */
public class FaceChooseDialogFragment extends BaseDialogFragment {

    private TextView tvTitle;
    private RecyclerView rvSelectList;
    private ShapeTextView stvLeftBt;
    private ShapeTextView stvRightBt;
    private String alertTitleTxt;
    private FaceChooseAdapter mSelectItemAdapter;
    private OnSelectListener onSelectListener;
    private List<? extends FaceChooseItemEntity> mDataList;

    @Override
    protected int getLayoutResId() {
        return R.layout.dialog_face_choose;
    }

    @Override
    protected void initViews(View rootView) {
        tvTitle = (TextView) findViewById(R.id.tv_title);
        if (!TextUtils.isEmpty(alertTitleTxt)) {
            tvTitle.setText(alertTitleTxt);
        }
        rvSelectList = (RecyclerView) findViewById(R.id.rv_select_list);
        stvLeftBt = (ShapeTextView) findViewById(R.id.stv_left_bt);
        stvRightBt = (ShapeTextView) findViewById(R.id.stv_right_bt);
        rvSelectList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mSelectItemAdapter = new FaceChooseAdapter(getActivity());
        mSelectItemAdapter.setOnItemClickListener((adapter, view, position) -> {
            // 处理点击事件
              for (int i = 0; i < mSelectItemAdapter.getData().size(); i++) {
                    if (i == position) {
                        mSelectItemAdapter.getData().get(i).setChecked(true);
                    }else {
                        mSelectItemAdapter.getData().get(i).setChecked(false);
                    }
                }

            mSelectItemAdapter.notifyDataSetChanged();

        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rvSelectList.setLayoutManager(layoutManager);
        rvSelectList.addItemDecoration(new ItemDecorationH(24));
        rvSelectList.setAdapter(mSelectItemAdapter);

        stvLeftBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FaceChooseItemEntity faceChooseItemEntity = null;
                List<FaceChooseItemEntity> dataList = mSelectItemAdapter.getData();
                int position = 0;
                for (int i = 0; i < dataList.size(); i++) {
                    FaceChooseItemEntity item = (FaceChooseItemEntity) dataList.get(i);
                    if (item.isChecked()) {
                        faceChooseItemEntity = item;
                        position = i;
                    }
                }
                if (faceChooseItemEntity != null) {
                    onSelectListener.onConfirmSelectItem(faceChooseItemEntity,position);
                    dismiss();
                } else {
                    AppToast.toastMsg("请选择人脸");
                }
            }
        });
        stvRightBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainApplication.isNeedCache = false;
                dismiss();
                if (onSelectListener != null) {
                    onSelectListener.onDismiss();
                }
            }
        });
        if (mDataList != null) {
            mSelectItemAdapter.addData(mDataList);
        }
    }

    public FaceChooseDialogFragment setSelectListData(List<FaceChooseItemEntity> listData) {
        if (listData != null) {
            mDataList = listData;
            if (mSelectItemAdapter != null) {
                mSelectItemAdapter.setNewInstance(listData);
            }
        }
        return this;
    }

    /**
     * 设置弹窗标题
     */
    public FaceChooseDialogFragment setTitle(String alertTitle) {
        this.alertTitleTxt = alertTitle;
        if (tvTitle != null) {
            tvTitle.setText(alertTitle);
        }
        return this;
    }

    public FaceChooseDialogFragment setOnSelectListener(OnSelectListener onSelectListener) {
        this.onSelectListener = onSelectListener;
        return this;
    }

    public static FaceChooseDialogFragment build() {
        return new FaceChooseDialogFragment();
    }

    public interface OnSelectListener {
        void onConfirmSelectItem(FaceChooseItemEntity faceChooseItemEntity,int position);

        default void onDismiss() {
        }
    }

}
