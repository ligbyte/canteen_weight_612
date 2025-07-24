package com.stkj.plate.weight.base.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.stkj.plate.weight.R;
import com.stkj.plate.weight.base.ui.adapter.SelectListShowAdapter;
import com.stkj.plate.weight.setting.model.FoodInfoTable;
import com.stkj.common.ui.popupwindow.CommonPopupWindow;

import java.util.ArrayList;
import java.util.List;

public class SelectExpandListPopWindow extends CommonPopupWindow {

    private SelectListShowAdapter selectListShowAdapter;
    private boolean needCustomItem;
    private boolean allowEditItem;
    private boolean allowDelItem;

    public SelectExpandListPopWindow(Context context) {
        super(context);
    }

    public SelectExpandListPopWindow(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SelectExpandListPopWindow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SelectExpandListPopWindow(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.pop_select_expand_list;
    }

    @Override
    protected void initViews(View rootView) {
        RecyclerView recyclerView = rootView.findViewById(R.id.rv_weight_goods_content);
        selectListShowAdapter = new SelectListShowAdapter(getContentView().getContext());
        recyclerView.setAdapter(selectListShowAdapter);
    }

    @Override
    protected ViewGroup.LayoutParams getLayoutParams() {
        return null;
    }

    public void setNeedCustomItem(boolean needCustomItem) {
        this.needCustomItem = needCustomItem;
    }

    public void setAllowEditItem(boolean allowEditItem) {
        this.allowEditItem = allowEditItem;
    }

    public void setAllowDelItem(boolean allowDelItem) {
        this.allowDelItem = allowDelItem;
    }

    private OnExpandItemClickListener itemClickListener;

    public void setItemClickListener(OnExpandItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    private OnCustomItemClickListener customItemClickListener;

    public void setCustomItemClickListener(OnCustomItemClickListener customItemClickListener) {
        this.customItemClickListener = customItemClickListener;
    }

    public void setExpandItemList(List expandItemList) {
        if (expandItemList != null) {
            selectListShowAdapter.setList(null);
            selectListShowAdapter.addData(expandItemList);
        } else {
            selectListShowAdapter.setList(null);
//            if (needCustomItem) {
//                selectListShowAdapter.addData(new FoodInfoTable());
//            }
        }
    }

    public List<FoodInfoTable> getDataList() {
        return selectListShowAdapter.getData();
    }

    public SelectListShowAdapter getSelectListShowAdapter() {
        return selectListShowAdapter;
    }

    /**
     * 添加自定义的item
     *
     * @param customExpandItem
     */
    public void addCustomItem(FoodInfoTable customExpandItem) {
        List<FoodInfoTable> dataList = selectListShowAdapter.getData();
        if (dataList != null) {
            List<Object> newDataList = new ArrayList<>();
            newDataList.add(customExpandItem);
            for (int i = 0; i < dataList.size(); i++) {
                Object o = dataList.get(i);
                if (o instanceof FoodInfoTable) {
                    newDataList.add(o);
                }
            }
            setExpandItemList(newDataList);
        }
    }

    public interface OnExpandItemClickListener {
        void onClickItem(FoodInfoTable commonExpandItem);

        default void onEditItem(FoodInfoTable commonExpandItem) {

        }

        default void onDelItem(FoodInfoTable commonExpandItem) {
        }
    }

    public interface OnCustomItemClickListener {
        void onClickCustom();
    }
}
