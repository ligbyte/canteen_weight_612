package com.stkj.cashier.home.ui.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.stkj.cashier.R;

/**
 * 通用提示布局
 */
public class WarningTipsView extends FrameLayout {

    private ImageView ivTips;
    private ImageView iv_common_close;
    private TextView tvTips;

    public WarningTipsView(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public WarningTipsView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public WarningTipsView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CommonTipsView);
        int tipsStyle = typedArray.getInt(R.styleable.CommonTipsView_ctv_style, 0);
        LayoutInflater.from(context).inflate(R.layout.include_warning_tips, this);
        typedArray.recycle();
        ivTips = (ImageView) findViewById(R.id.iv_common_tips);
        iv_common_close = (ImageView) findViewById(R.id.iv_common_close);
        tvTips = (TextView) findViewById(R.id.tv_common_tips);
        iv_common_close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideTipsView();
            }
        });
    }

    public void setTips(String tips) {
        if (tvTips != null) {
            setVisibility(VISIBLE);
            stopLoadAnim();
            ivTips.setImageResource(R.mipmap.prompt);
            tvTips.setText(tips);
        }
    }

    public void setLoading(String loading) {
        if (tvTips != null) {
            setVisibility(VISIBLE);
            ivTips.setImageResource(R.mipmap.prompt);
            stopLoadAnim();
            startLoadAnim();
            tvTips.setText(loading);
        }
    }

    private ValueAnimator loadingAnimator;

    private void stopLoadAnim() {
        if (loadingAnimator != null) {
            loadingAnimator.end();
            loadingAnimator = null;
        }
    }

    private void startLoadAnim() {
        stopLoadAnim();
        loadingAnimator = ValueAnimator.ofInt(0, 360);
        loadingAnimator.setDuration(1500);
        loadingAnimator.setInterpolator(new LinearInterpolator());
        loadingAnimator.setRepeatCount(ValueAnimator.INFINITE);
        loadingAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(@NonNull ValueAnimator animation) {
                Object animatedValue = animation.getAnimatedValue();
                if (animatedValue instanceof Integer) {
                    ivTips.setRotation((Integer) animatedValue);
                }
            }
        });
        loadingAnimator.start();
    }

    private final Runnable hideViewTask = new Runnable() {
        @Override
        public void run() {
            hideTipsView();
        }
    };

    public void delayHideTipsView() {
        removeCallbacks(hideViewTask);
        postDelayed(hideViewTask, 3000);
    }

    public void hideTipsView() {
        stopLoadAnim();
        setVisibility(GONE);
    }

}
