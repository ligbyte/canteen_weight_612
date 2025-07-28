package com.stkj.common.ui.widget.tabs;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.graphics.PorterDuff;

import com.stkj.common.utils.DimensUtils;
public class RoundTab extends View {

    private final float INNER_VERTICAL_PADDING = DimensUtils.dpToPx(getContext(), 10f);
    private final float INNER_HORIZONTAL_PADDING = DimensUtils.dpToPx(getContext(), 10f) * 2;

    private final float OUTER_HORIZONTAL_EDGE_PADDING = DimensUtils.dpToPx(getContext(), 5f);
    private final float OUTER_HORIZONTAL_PADDING = DimensUtils.dpToPx(getContext(), 3f);

    private final int ICON_SIZE = DimensUtils.dpToPx(getContext(), 24);
    private final int ICON_HORIZONTAL_PADDING = DimensUtils.dpToPx(getContext(), 8);
    private final int ICON_HORIZONTAL_EDGE_PADDING = DimensUtils.dpToPx(getContext(), 10);

    private RectF tab;
    private Paint tabPaint;
    private Paint tabStrokePaint;

    private Rect textBounds;
    private Paint textPaint;

    private int tabBackgroundColor = Color.parseColor("#FFFFFF");
    private int tabStrokeColor = Color.parseColor("#1a2d4e");
    private int tabTextColor = Color.parseColor("#666666");
    private int tabIconColor = Color.parseColor("#FFFFFF");

    private boolean isFirst = false;
    private boolean isLast = false;

    private int parentHeight = 0;

    private boolean hasIcon = false;
    private boolean hasStroke = true;

    private Drawable icon;

    private String tabText = "";
    private int cornerRadius = 50;

    public RoundTab(Context context) {
        super(context);
        initView();
    }

    public RoundTab(Context context, int cornerRadius, Drawable iconRes, boolean isStrokeEnabled) {
        this(context);
        this.cornerRadius = cornerRadius;
        this.hasStroke = isStrokeEnabled;

        if (iconRes != null) {
            this.icon = iconRes;
            hasIcon = true;
        } else {
            hasIcon = false;
        }
    }

    private void initView() {
        tab = new RectF();
        tabStrokePaint = new Paint();
        textBounds = new Rect();
        textPaint = new Paint();
        tabPaint = new Paint();
    }

    public RoundTab initTab(String tabText) {
        this.tabText = tabText;

        textPaint.setTextSize(DimensUtils.spToPx(getContext(), 14));
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(tabTextColor);
        textPaint.setAntiAlias(true);
        textPaint.setFakeBoldText(false);
        textPaint.getTextBounds(tabText, 0, tabText.length(), textBounds);

        tabStrokePaint.setStyle(Paint.Style.STROKE);
        tabStrokePaint.setColor(tabStrokeColor);
        tabStrokePaint.setStrokeWidth(DimensUtils.dpToPx(getContext(), 2f));
        tabStrokePaint.setAntiAlias(true);

        if (tabBackgroundColor == Color.parseColor("#1a2d4e")) {
            tabPaint.setStyle(Paint.Style.STROKE);
        }else {
            tabPaint.setStyle(Paint.Style.FILL);
        }

        tabPaint.setColor(tabBackgroundColor);
        tabPaint.setStrokeWidth(DimensUtils.dpToPx(getContext(), 1.5f));
        tabPaint.setAntiAlias(true);

        if (icon != null) {
            icon.setColorFilter(tabTextColor, PorterDuff.Mode.SRC_ATOP);
        }

        return this;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        tabPaint.setColor(tabBackgroundColor);
        tabStrokePaint.setColor(tabStrokeColor);
        textPaint.setColor(tabTextColor);

        canvas.drawRoundRect(tab, cornerRadius, cornerRadius, tabPaint);

        if (hasStroke) {
            canvas.drawRoundRect(tab, cornerRadius, cornerRadius, tabStrokePaint);
        }

        if (hasIcon) {
            canvas.drawText(tabText,
                    tab.left + ICON_SIZE + ICON_HORIZONTAL_EDGE_PADDING + ICON_HORIZONTAL_PADDING,
                    parentHeight / 2 + textBounds.height() / 2,
                    textPaint);
        } else {
            canvas.drawText(tabText,
                    tab.left + INNER_HORIZONTAL_PADDING / 2,
                    parentHeight / 2 + textBounds.height() / 2,
                    textPaint);
        }

        if (icon != null) {
            icon.setBounds(
                    (int) (tab.left + ICON_HORIZONTAL_PADDING * 2),
                    (int)(parentHeight / 2 - INNER_VERTICAL_PADDING),
                    (int) (tab.left + ICON_HORIZONTAL_PADDING * 2 + ICON_SIZE),
                    (int)(parentHeight / 2 + INNER_VERTICAL_PADDING)
            );
            icon.setColorFilter(tabTextColor, PorterDuff.Mode.SRC_ATOP);
            icon.draw(canvas);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (hasIcon) {
            tab.left = OUTER_HORIZONTAL_PADDING;
            tab.top = parentHeight / 2 - textBounds.height() / 2 - INNER_VERTICAL_PADDING;
            tab.right = textBounds.right + INNER_HORIZONTAL_PADDING + ICON_SIZE + ICON_HORIZONTAL_PADDING * 2;
            tab.bottom = parentHeight / 2 + textBounds.height() / 2 + INNER_VERTICAL_PADDING;

            if (isFirst) {
                tab.left = OUTER_HORIZONTAL_EDGE_PADDING;
                tab.right = textBounds.right + INNER_HORIZONTAL_PADDING + ICON_SIZE + 4 + ICON_HORIZONTAL_EDGE_PADDING + ICON_HORIZONTAL_PADDING;
            }
        } else {
            tab.left = OUTER_HORIZONTAL_PADDING;
            tab.right = textBounds.right + OUTER_HORIZONTAL_PADDING + INNER_HORIZONTAL_PADDING;
            tab.top = (parentHeight / 2 - textBounds.height() / 2) - INNER_VERTICAL_PADDING;
            tab.bottom = (parentHeight / 2 + textBounds.height() / 2) + INNER_VERTICAL_PADDING;

            if (isFirst) {
                tab.left = OUTER_HORIZONTAL_EDGE_PADDING;
                tab.right = textBounds.right + OUTER_HORIZONTAL_EDGE_PADDING + INNER_HORIZONTAL_PADDING;
                textBounds.left = (int)OUTER_HORIZONTAL_EDGE_PADDING;
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (hasIcon && isFirst) {
            setMeasuredDimension(
                    (int) (textBounds.right + INNER_HORIZONTAL_PADDING + OUTER_HORIZONTAL_EDGE_PADDING + ICON_SIZE + ICON_HORIZONTAL_EDGE_PADDING),
                    heightMeasureSpec
            );
        } else if (hasIcon && isLast) {
            setMeasuredDimension(
                    (int) (textBounds.right + INNER_HORIZONTAL_PADDING + OUTER_HORIZONTAL_EDGE_PADDING + OUTER_HORIZONTAL_PADDING + ICON_HORIZONTAL_EDGE_PADDING + ICON_SIZE),
                    heightMeasureSpec
            );
        } else if (hasIcon) {
            setMeasuredDimension(
                    (int) (textBounds.right + INNER_HORIZONTAL_PADDING + OUTER_HORIZONTAL_PADDING + ICON_SIZE + ICON_HORIZONTAL_PADDING * 2),
                    heightMeasureSpec
            );
        } else if (isFirst) {
            setMeasuredDimension(
                    (int) (textBounds.right + OUTER_HORIZONTAL_EDGE_PADDING + INNER_HORIZONTAL_PADDING + OUTER_HORIZONTAL_PADDING),
                    heightMeasureSpec
            );
        } else if (isLast) {
            setMeasuredDimension(
                    (int) (textBounds.right + OUTER_HORIZONTAL_EDGE_PADDING + INNER_HORIZONTAL_PADDING + OUTER_HORIZONTAL_PADDING),
                    heightMeasureSpec
            );
        } else {
            setMeasuredDimension(
                    (int) (textBounds.right + INNER_HORIZONTAL_PADDING + OUTER_HORIZONTAL_PADDING * 2),
                    heightMeasureSpec
            );
        }
    }

    public void setTabBackgroundColor(int tabBackgroundColor) {
        this.tabBackgroundColor = tabBackgroundColor;
        invalidate();
    }

    public void setTabStrokeColor(int tabStrokeColor) {
        this.tabStrokeColor = tabStrokeColor;
        invalidate();
    }

    public void setTabTextColor(int tabTextColor) {
        this.tabTextColor = tabTextColor;
        if (icon != null) {
            icon.setColorFilter(tabTextColor, PorterDuff.Mode.SRC_ATOP);
        }
        invalidate();
    }

    public void setTabIconTint(int tabIconColor) {
        this.tabIconColor = tabIconColor;
        if (icon != null) {
            icon.setColorFilter(tabIconColor, PorterDuff.Mode.SRC_ATOP);
        }
        invalidate();
    }

    public void setParentHeight(int parentHeight) {
        this.parentHeight = parentHeight;
        invalidate();
    }

    public void setFirst(boolean first) {
        isFirst = first;
        invalidate();
    }

    public void setLast(boolean last) {
        isLast = last;
        invalidate();
    }
}
