package com.stkj.common.ui.widget.common;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Copyright (C), 2015-2019, suntront
 * FileName: SpacesItemDecoration
 * Author: Jeek
 * Date: 2019/11/12 10:37
 * Description: ${DESCRIPTION}
 */
public class ItemDecorationVertical extends RecyclerView.ItemDecoration {
    private int space;

    public ItemDecorationVertical(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
        outRect.bottom = space;
    }
}
