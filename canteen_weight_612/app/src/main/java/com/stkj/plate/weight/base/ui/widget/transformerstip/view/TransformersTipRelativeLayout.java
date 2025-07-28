package com.stkj.plate.weight.base.ui.widget.transformerstip.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.stkj.plate.weight.base.ui.widget.transformerstip.ArrowDrawable;


public class TransformersTipRelativeLayout extends RelativeLayout {

    public TransformersTipRelativeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TransformersTipRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        ArrowDrawable arrowDrawable = new ArrowDrawable(context, attrs);
        arrowDrawable.expandShadowAndArrowPadding(this);
        setBackground(arrowDrawable);
    }
}
