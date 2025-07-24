package com.stkj.plate.weight.base.ui.dialog;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.stkj.plate.weight.R;
import com.stkj.plate.weight.base.utils.PriceUtils;
import com.stkj.plate.weight.pay.model.FoodConsumeRefundResponse;
import com.stkj.plate.weight.stat.ui.adapter.FoodRefundRecordListAdapter;
import com.stkj.common.ui.fragment.BaseDialogFragment;

/**
 * 退款记录
 */
public class OrderRefundAlertDialogFragment extends BaseDialogFragment {

    public final static String TAG = "OrderAlertDialogFragment";
    private TextView tvTitle;
//    private TextView tvAlertContent;
    private boolean needHandleDismiss;
    private final FoodConsumeRefundResponse foodConsumeDetailResponse;
    private ImageView iv_close;
    private TextView tv_total_count;
    private TextView tv_total_money;
    private LinearLayout ll_list_empty;
    private RecyclerView rv_goods_list;
    private FoodRefundRecordListAdapter foodRefundRecordListAdapter;

    public OrderRefundAlertDialogFragment(FoodConsumeRefundResponse foodConsumeDetailResponse) {
        this.foodConsumeDetailResponse = foodConsumeDetailResponse;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.dialog_order_refund_alert;
    }

    public static OrderRefundAlertDialogFragment build(FoodConsumeRefundResponse foodConsumeDetailResponse) {
        return new OrderRefundAlertDialogFragment(foodConsumeDetailResponse);
    }

    public OrderRefundAlertDialogFragment setNeedHandleDismiss(boolean needHandleDismiss) {
        this.needHandleDismiss = needHandleDismiss;
        return this;
    }

    private OnSweetClickListener mRightNavClickListener;
    private OnSweetClickListener mLeftNavClickListener;

    private String leftNavTxt;

    /**
     * 设置左侧按钮文案
     */
    public OrderRefundAlertDialogFragment setLeftNavTxt(String leftNavTxt) {
        this.leftNavTxt = leftNavTxt;

        return this;
    }

    private String rightNavTxt;

    /**
     * 设置右侧按钮文案
     */
    public OrderRefundAlertDialogFragment setRightNavTxt(String rightNavTxt) {
        this.rightNavTxt = rightNavTxt;

        return this;
    }

    private String alertTitleTxt;

    /**
     * 设置弹窗标题
     */
    public OrderRefundAlertDialogFragment setAlertTitleTxt(String alertTitle) {
        this.alertTitleTxt = alertTitle;
        if (tvTitle != null) {
            tvTitle.setText(alertTitle);
        }
        return this;
    }

    private String alertContentTxt;

    /**
     * 设置弹窗内容
     */
    public OrderRefundAlertDialogFragment setAlertContentTxt(String alertContent) {
        this.alertContentTxt = alertContent;
//        if (tvAlertContent != null) {
//            if (!TextUtils.isEmpty(alertContent)) {
//                tvAlertContent.setVisibility(View.VISIBLE);
//                tvAlertContent.setText(alertContent);
//            } else {
//                tvAlertContent.setVisibility(View.GONE);
//            }
//        }
        return this;
    }

    /**
     * 设置右侧按钮点击事件
     */
    public OrderRefundAlertDialogFragment setRightNavClickListener(OnSweetClickListener listener) {
        mRightNavClickListener = listener;
        return this;
    }

    /**
     * 设置左侧按钮点击事件
     */
    public OrderRefundAlertDialogFragment setLeftNavClickListener(OnSweetClickListener listener) {
        mLeftNavClickListener = listener;
        return this;
    }

    public interface OnSweetClickListener {
        void onClick(OrderRefundAlertDialogFragment alertDialogFragment);
    }

    @Override
    protected void initViews(View rootView) {
        tvTitle = (TextView) findViewById(R.id.tv_title);
        if (!TextUtils.isEmpty(alertTitleTxt)) {
            tvTitle.setText(alertTitleTxt);
        }
//        tvAlertContent = (TextView) findViewById(R.id.tv_alert_content);
//        tvAlertContent.setMovementMethod(ScrollingMovementMethod.getInstance());
//        if (!TextUtils.isEmpty(alertContentTxt)) {
//            tvAlertContent.setText(alertContentTxt);
//        }

        iv_close = (ImageView) findViewById(R.id.iv_close);
        rv_goods_list = (RecyclerView) findViewById(R.id.rv_goods_list);
        ll_list_empty = (LinearLayout) findViewById(R.id.ll_list_empty);
        tv_total_money = (TextView) findViewById(R.id.tv_total_money);
        tv_total_count = (TextView) findViewById(R.id.tv_total_count);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext()) {
            @Override
            public boolean canScrollVertically() {
                return true;
            }

            @Override
            public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec, int heightSpec) {
                super.onMeasure(recycler, state, widthSpec, heightSpec);
            }
        };
//        rv_goods_list.setHasFixedSize(true);
//        rv_goods_list.setNestedScrollingEnabled(false);
        rv_goods_list.setLayoutManager(linearLayoutManager);
        foodRefundRecordListAdapter = new FoodRefundRecordListAdapter(getContext());
        rv_goods_list.setAdapter(foodRefundRecordListAdapter);

        if (foodConsumeDetailResponse == null || foodConsumeDetailResponse.getRefundFoodList() == null || foodConsumeDetailResponse.getRefundFoodList().size() == 0){
            ll_list_empty.setVisibility(View.VISIBLE);
            rv_goods_list.setVisibility(View.GONE);
        }else {
            ll_list_empty.setVisibility(View.GONE);
            rv_goods_list.setVisibility(View.VISIBLE);
        }

        foodRefundRecordListAdapter.setNewInstance(foodConsumeDetailResponse.getRefundFoodList());

        tv_total_count.setText(String.valueOf(foodConsumeDetailResponse.getRefundTotalFoodCount()));
        tv_total_money.setText(PriceUtils.formatPrice("￥ " + foodConsumeDetailResponse.getRefundTotalPrice().getAmount()));



        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


    }
}
