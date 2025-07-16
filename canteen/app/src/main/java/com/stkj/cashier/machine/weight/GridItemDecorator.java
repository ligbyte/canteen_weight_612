package com.stkj.cashier.machine.weight;

import android.graphics.Rect;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;

public class GridItemDecorator extends RecyclerView.ItemDecoration {
    private static final int SPAN_COUNT = 4;

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        int column = position % SPAN_COUNT;

        int itemWidth = parent.getWidth() / 4; // item外宽度
        int itemWidthInside = dp2px(65, view); // item内宽度
        int padding = itemWidth - itemWidthInside; // p
        int space = (parent.getWidth() - 4 * itemWidthInside) / 3; // space

        if (column == 0) {
            outRect.left = 0;
            outRect.right = padding;
        } else if (column == 1) {
            outRect.left = space - padding;
            outRect.right = padding * 2 - space;
        } else if (column == 2) {
            outRect.left = space * 2 - padding * 2;
            outRect.right = padding * 3 - space * 2;
        } else if (column == 3) {
            outRect.left = padding;
            outRect.right = 0;
        }
    }

    private int dp2px(int dp, View view) {
        float scale = view.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}

