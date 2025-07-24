package com.stkj.common.ui.widget.common;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import androidx.appcompat.widget.AppCompatTextView;

import com.stkj.common.R;

public class AutoScaleItemTextView extends AppCompatTextView {
    private static final float MAX_TEXT_SIZE_SP = 13.0f; // 最大字体大小 7sp
    private static final float ABSOLUTE_MIN_TEXT_SIZE_SP = 4.0f; // 绝对最小字体大小 4sp（避免太小看不清）

    private float mMaxTextSizePx; // 最大字体像素值
    private float mMinTextSizePx; // 用户设置的最小字体像素值（但实际缩放可能低于这个值，显示完整优先）
    private float mAbsoluteMinTextSizePx; // 绝对最小字体像素值
    private int mAvailableWidth;
    private final Paint mTextPaint = new Paint();
    private boolean mInitialized = false;

    public AutoScaleItemTextView(Context context) {
        this(context, null);
    }

    public AutoScaleItemTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoScaleItemTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        // 设置最大字号像素值
        mMaxTextSizePx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, MAX_TEXT_SIZE_SP, metrics);
        // 设置绝对最小字号像素值
        mAbsoluteMinTextSizePx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, ABSOLUTE_MIN_TEXT_SIZE_SP, metrics);

        // 初始化用户设置的最小字号（属性）
        float minSizeSp = 10f; // 默认最小字体大小10sp
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AutoScaleTextView);
            minSizeSp = a.getDimension(R.styleable.AutoScaleTextView_minTextSize, 10f);
            a.recycle();
        }
        // 转换为像素（不能超过最大字号）
        mMinTextSizePx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, minSizeSp, metrics);

        // 配置单行显示
        setSingleLine(true);
        setEllipsize(null); // 禁用省略号

        // 初始化Paint对象
        mTextPaint.set(getPaint());
        // 设置初始文本大小（不超过最大字号）
        if (getTextSize() > mMaxTextSizePx) {
            super.setTextSize(TypedValue.COMPLEX_UNIT_PX, mMaxTextSizePx);
        }
        mInitialized = true;
    }

    @Override
    public void setTextSize(float size) {
        setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
    }

    @Override
    public void setTextSize(int unit, float size) {
        // 如果外部设置的字号超过最大字号，则限制为最大字号
        float sizePx = TypedValue.applyDimension(unit, size, getResources().getDisplayMetrics());
        if (sizePx > mMaxTextSizePx) {
            super.setTextSize(TypedValue.COMPLEX_UNIT_PX, mMaxTextSizePx);
        } else {
            super.setTextSize(unit, size);
        }
        // 调整文本大小（可能因为控件宽度限制而需要缩放）
        adjustTextSize();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w > 0) {
            mAvailableWidth = w - getPaddingLeft() - getPaddingRight();
            adjustTextSize();
        }
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        adjustTextSize();
    }

    private void adjustTextSize() {
        if (!mInitialized || mAvailableWidth <= 0 || getText().length() == 0) return;

        final CharSequence text = getText();

        // 第一步：使用最大字号（14sp）测量文本
        mTextPaint.setTextSize(mMaxTextSizePx);
        float textWidth = mTextPaint.measureText(text, 0, text.length());

        // 如果最大字号可以完整显示，则使用最大字号
        if (textWidth <= mAvailableWidth) {
            super.setTextSize(TypedValue.COMPLEX_UNIT_PX, mMaxTextSizePx);
        } else {
            // 计算目标字号：按比例缩放，确保不超出可用空间
            float targetSize = mMaxTextSizePx * mAvailableWidth / textWidth;

            // 确保不小于绝对最小字号
            if (targetSize < mAbsoluteMinTextSizePx) {
                targetSize = mAbsoluteMinTextSizePx;
            }

            // 设置目标字号
            super.setTextSize(TypedValue.COMPLEX_UNIT_PX, targetSize);
        }
    }
}