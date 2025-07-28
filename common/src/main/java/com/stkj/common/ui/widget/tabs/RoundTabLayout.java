package com.stkj.common.ui.widget.tabs;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import androidx.viewpager.widget.ViewPager;

import com.stkj.common.R;
import com.stkj.common.utils.DimensUtils;

import java.util.ArrayList;
import java.util.List;

public class RoundTabLayout extends HorizontalScrollView implements ViewPager.OnPageChangeListener {

    private static final int ANIMATION_FADE_IN = 1;
    private static final int ANIMATION_FADE_OUT = 0;

    private static final String TAG = RoundTabLayout.class.getSimpleName();

    private List<RoundTab> tabs = new ArrayList<>();
    private LinearLayout.LayoutParams layoutParams;
    private LinearLayout tabStrip;
    private ViewPager viewPager;

    private int tabBackColor;
    private int tabStrokeColor;
    private Drawable iconRes;

    private int cornerRadius = 50;
    private int clickedPosition = 0;

    private boolean hasStroke = true;

    public RoundTabLayout(Context context) {
        super(context);
        init(context, null);
    }

    public RoundTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundTabLayout);
            tabStrokeColor = typedArray.getColor(R.styleable.RoundTabLayout_accent, 0x1a2d4e);
            cornerRadius = typedArray.getInt(R.styleable.RoundTabLayout_corner1Radius, 40);
            hasStroke = typedArray.getBoolean(R.styleable.RoundTabLayout_withStroke, true);
            iconRes = typedArray.getDrawable(R.styleable.RoundTabLayout_src);
            typedArray.recycle();

            try {
                Drawable background = getBackground();
                if (background instanceof ColorDrawable) {
                    tabBackColor = ((ColorDrawable) background).getColor();
                } else {
                    tabBackColor = 0x1a2d4e; // default fallback
                }
            } catch (ClassCastException e) {
                Log.d(TAG, "Tab layout background color Class Cast Exception");
                tabBackColor = 0x1a2d4e;
            }
        }

        setHorizontalScrollBarEnabled(false);
        addViews(context);
    }

    private void addViews(Context context) {
        tabStrip = new LinearLayout(context);
        layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        layoutParams.gravity = Gravity.CENTER;
        tabStrip.setLayoutParams(layoutParams);
        tabStrip.setOrientation(LinearLayout.HORIZONTAL);
        this.addView(tabStrip, layoutParams);
    }

    public List<RoundTab> getTabs() {
        return tabs;
    }

    public void setupWithViewPager(ViewPager viewPager) {
        this.viewPager = viewPager;
        viewPager.setOnPageChangeListener(this);

        for (int i = 0; i < viewPager.getAdapter().getCount(); i++) {
            String tabText = viewPager.getAdapter().getPageTitle(i).toString().toUpperCase();
            RoundTab tab = new RoundTab(getContext(), cornerRadius, iconRes, hasStroke).initTab(tabText);

            if (i == viewPager.getCurrentItem()) {
                tab.setTabBackgroundColor(Color.parseColor("#2196F3"));
                tab.setTabTextColor(Color.parseColor("#FFFFFF"));
            } else {
                tab.setTabBackgroundColor(Color.parseColor("#1a2d4e"));
                tab.setTabTextColor(Color.parseColor("#FFFFFF"));
            }

            tab.setTabStrokeColor(tabStrokeColor);
            tabs.add(tab);
        }

        if (tabStrip != null) {
            tabStrip.removeAllViews();
        }
        for (RoundTab tab : tabs) {
            tabStrip.addView(tab, layoutParams);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < tabs.size(); i++) {
            RoundTab tab = tabs.get(i);
            tab.setParentHeight(getHeight());
            final int index = i;

            tab.setOnClickListener(v -> {
                if (index != clickedPosition && clickedPosition != -1) {
                    animateFade(tabs.get(clickedPosition), ANIMATION_FADE_OUT);
                    tabs.get(clickedPosition).invalidate();
                    clickedPosition = index;
                    animateFade(tab, ANIMATION_FADE_IN);
                    tab.invalidate();
                    if (viewPager != null) {
                        viewPager.setCurrentItem(index);
                    }
                }
            });

            if (i == 0) {
                tab.setFirst(true);
                tab.requestLayout();
            }

            if (i == tabs.size() - 1) {
                tab.setLast(true);
                tab.requestLayout();
            }

            tab.invalidate();
        }
    }

    @Override
    public void onPageSelected(int position) {
        scrollTabView(position);
        if (position != clickedPosition && clickedPosition != -1) {
            animateFade(tabs.get(clickedPosition), ANIMATION_FADE_OUT);
            tabs.get(clickedPosition).invalidate();
            clickedPosition = position;
            animateFade(tabs.get(position), ANIMATION_FADE_IN);
            tabs.get(position).invalidate();
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

    @Override
    public void onPageScrollStateChanged(int state) {}

    private void animateFade(RoundTab tab, int animType) {
        int tabBackgroundFrom, tabBackgroundTo, contentColorFrom, contentColorTo;

        switch (animType) {
            case ANIMATION_FADE_IN:
                tabBackgroundFrom = 0x1a2d4e;
                tabBackgroundTo = Color.parseColor("#2196F3");
                contentColorFrom = tabStrokeColor;
                contentColorTo = Color.parseColor("#FFFFFF");
                break;
            case ANIMATION_FADE_OUT:
                tabBackgroundFrom = Color.parseColor("#2196F3");
                tabBackgroundTo = Color.parseColor("#1a2d4e");
                contentColorFrom = tabBackColor;
                contentColorTo = Color.parseColor("#FFFFFF");
                break;
            default:
                tabBackgroundFrom = 0x1a2d4e;
                tabBackgroundTo = tabStrokeColor;
                contentColorFrom = tabStrokeColor;
                contentColorTo = tabBackColor;
                break;
        }

        ValueAnimator colorAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), tabBackgroundFrom, tabBackgroundTo);
        colorAnimator.setDuration(250);
        colorAnimator.addUpdateListener(animation -> tab.setTabBackgroundColor((Integer) animation.getAnimatedValue()));
        colorAnimator.start();

        ValueAnimator textAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), contentColorFrom, contentColorTo);
        textAnimator.setDuration(200);
        textAnimator.addUpdateListener(animation -> tab.setTabTextColor((Integer) animation.getAnimatedValue()));
        textAnimator.start();

        ValueAnimator iconAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), contentColorFrom, contentColorTo);
        iconAnimator.setDuration(350);
        iconAnimator.addUpdateListener(animation -> tab.setTabIconTint((Integer) animation.getAnimatedValue()));
        iconAnimator.start();
    }

    private void scrollTabView(int position) {
        int previousPosition = 0;
        int nextPosition = 0;

        if (position == tabs.size() - 1) {
            smoothScrollTo(getRight(), 0);
            return;
        }

        for (int i = 0; i < position - 1; i++) {
            previousPosition += tabs.get(i).getWidth();
        }

        for (int i = 0; i <= position + 1 - 1; i++) {
            nextPosition += tabs.get(i).getWidth();
        }

        int currentPosition = 0;
        for (int i = 0; i <= position; i++) {
            currentPosition += tabs.get(i).getWidth();
        }

        if (previousPosition < getScrollX()) {
            smoothScrollTo(previousPosition, 0);
        } else if (currentPosition > getWidth()) {
            smoothScrollTo(previousPosition, 0);
        } else if (nextPosition > getWidth()) {
            smoothScrollTo(currentPosition, 0);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, DimensUtils.dpToPx(getContext(), 48));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        postInvalidate();
    }

    public RoundTab getTab(int position) {
        return tabs.get(position);
    }
}