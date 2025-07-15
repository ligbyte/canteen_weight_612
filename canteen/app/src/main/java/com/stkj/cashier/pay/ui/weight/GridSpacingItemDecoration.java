package com.stkj.cashier.pay.ui.weight;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {
    private int spanCount;
    private int spacing;
    private boolean includeEdge;

    public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
        this.spanCount = spanCount;
        this.spacing = spacing;
        this.includeEdge = includeEdge;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view); // 获取当前 Item 的位置
        int column = position % spanCount; // 计算当前 Item 所在的列

        if (includeEdge) {
            // 包含边缘的情况
            outRect.left = spacing - column * spacing / spanCount;
            outRect.right = (column + 1) * spacing / spanCount;

            // 第一行顶部间距
            if (position < spanCount) {
                outRect.top = spacing;
            }
            // 所有 Item 底部间距
            outRect.bottom = spacing;
        } else {
            // 不包含边缘的情况
            outRect.left = column * spacing / spanCount;
            outRect.right = spacing - (column + 1) * spacing / spanCount;
            // 非第一行顶部间距
            if (position >= spanCount) {
                outRect.top = spacing;
            }
        }
    }
}
