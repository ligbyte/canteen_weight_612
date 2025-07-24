package com.stkj.plate.weight.base.ui.dialog;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.stkj.plate.weight.R;
import com.stkj.plate.weight.base.utils.PriceUtils;
import com.stkj.plate.weight.pay.callback.OnCalculateListener;
import com.stkj.plate.weight.pay.ui.weight.CashierCalculator;
import com.stkj.common.ui.fragment.BaseDialogFragment;

/**
 * 收银弹窗
 */
public class CashierPayDialogFragment extends BaseDialogFragment {

    public final static String TAG = "CashierPayDialogFragment";
    public final static int MODE_QUICK = 0;
    public final static int MODE_CASH = 1;
    private  int currentMode = 0;
    private TextView tvTitle;
    private TextView tv_money_input;
    private TextView tv_money_input_hint;
    private TextView tv_goods_price;
    private TextView tv_money_back;
    private double payMoney;
    private CashierCalculator sc_calc;
    private FrameLayout fl_cashier_pay_left;

    public CashierPayDialogFragment(int currentMode,double payMoney) {
        super();
        this.currentMode = currentMode;
        this.payMoney = payMoney;
    }
//    private RecyclerView rvSelectList;
//    private ShapeTextView stvLeftBt;
//    private ShapeTextView stvRightBt;
//    private String alertTitleTxt;
//    private FaceChooseAdapter mSelectItemAdapter;
    private OnSelectListener onSelectListener;
//    private List<? extends FaceChooseItemEntity> mDataList;

    @Override
    protected int getLayoutResId() {
        return R.layout.dialog_cashier_pay;
    }

    @Override
    protected void initViews(View rootView) {
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tv_money_input = (TextView) findViewById(R.id.tv_money_input);
        tv_money_input_hint = (TextView) findViewById(R.id.tv_money_input_hint);
        tv_goods_price = (TextView) findViewById(R.id.tv_goods_price);
        tv_money_back = (TextView) findViewById(R.id.tv_money_back);
        fl_cashier_pay_left = (FrameLayout) findViewById(R.id.fl_cashier_pay_left);
        sc_calc = (CashierCalculator) findViewById(R.id.sc_calc);

        if (currentMode == MODE_CASH){
            tvTitle.setText("现金结算");
            sc_calc.setConfirmTxt("确定");
            sc_calc.getTvConsume().setText(PriceUtils.formatPrice(payMoney));
            tv_money_input.setText("￥" + PriceUtils.formatPrice(payMoney));
            showMoneyInputHint(false);
        }else {
            tvTitle.setText("快速结算");
            sc_calc.setConfirmTxt("确认金额");
        }

        tv_goods_price.setText("￥" + PriceUtils.formatPrice(payMoney));

        if (currentMode == MODE_CASH){
            fl_cashier_pay_left.setVisibility(View.VISIBLE);
        }else {
            fl_cashier_pay_left.setVisibility(View.GONE);
        }

        sc_calc.getTvConsume().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (!TextUtils.isEmpty(s) && (currentMode == MODE_CASH)){
                    tv_money_input.setText("￥" + PriceUtils.formatPrice(s.toString()));
                    showMoneyInputHint(false);
                    double backMoney = Double.parseDouble(s.toString()) - payMoney;
                    if (backMoney > 0){
                    tv_money_back.setText("￥" + PriceUtils.formatPrice(backMoney));
                    } else {
                        tv_money_back.setText("￥0.00");
                    }
                }else {
                    tv_money_input.setText("待输入");
                    showMoneyInputHint(true);
                    tv_money_back.setText("￥0.00");
                }

            }
        });

        sc_calc.setOnCalculateListener(new OnCalculateListener() {
            @Override
            public void onConfirmMoney(String payMoney) {

                if (onSelectListener != null){
                    onSelectListener.onConfirmSelectItem(currentMode,payMoney);
                }

//                if (!TextUtils.isEmpty(payMoney)) {
//                    dismiss();
//                }

            }

            @Override
            public void onCancel() {
                if (onSelectListener != null){
                    onSelectListener.onDismiss();
                }
                dismiss();
            }
        });

//        rvSelectList = (RecyclerView) findViewById(R.id.rv_select_list);
//        stvLeftBt = (ShapeTextView) findViewById(R.id.stv_left_bt);
//        stvRightBt = (ShapeTextView) findViewById(R.id.stv_right_bt);
//        rvSelectList.setLayoutManager(new LinearLayoutManager(getActivity()));
//        mSelectItemAdapter = new FaceChooseAdapter(getActivity());
//        mSelectItemAdapter.setOnItemClickListener((adapter, view, position) -> {
//            // 处理点击事件
//              for (int i = 0; i < mSelectItemAdapter.getData().size(); i++) {
//                    if (i == position) {
//                        mSelectItemAdapter.getData().get(i).setChecked(true);
//                    }else {
//                        mSelectItemAdapter.getData().get(i).setChecked(false);
//                    }
//                }
//
//            mSelectItemAdapter.notifyDataSetChanged();
//
//        });
//
//        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
//        rvSelectList.setLayoutManager(layoutManager);
//        rvSelectList.addItemDecoration(new ItemDecorationH(24));
//        rvSelectList.setAdapter(mSelectItemAdapter);
//
//        stvLeftBt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FaceChooseItemEntity faceChooseItemEntity = null;
//                List<FaceChooseItemEntity> dataList = mSelectItemAdapter.getData();
//                int position = 0;
//                for (int i = 0; i < dataList.size(); i++) {
//                    FaceChooseItemEntity item = (FaceChooseItemEntity) dataList.get(i);
//                    if (item.isChecked()) {
//                        faceChooseItemEntity = item;
//                        position = i;
//                    }
//                }
//                if (faceChooseItemEntity != null) {
//                    onSelectListener.onConfirmSelectItem(faceChooseItemEntity,position);
//                    dismiss();
//                } else {
//                    AppToast.toastMsg("请选择人脸");
//                }
//            }
//        });
//        stvRightBt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                MainApplication.isNeedCache = false;
//                dismiss();
//                if (onSelectListener != null) {
//                    onSelectListener.onDismiss();
//                }
//            }
//        });
//        if (mDataList != null) {
//            mSelectItemAdapter.addData(mDataList);
//        }
    }


    private void showMoneyInputHint(boolean isShow){

        if (isShow){
            tv_money_input.setVisibility(View.GONE);
            tv_money_input_hint.setVisibility(View.VISIBLE);
        } else{
            tv_money_input.setVisibility(View.VISIBLE);
            tv_money_input_hint.setVisibility(View.GONE);
        }

    }


    /**
     * 设置弹窗标题
     */
    public CashierPayDialogFragment setTitle(String alertTitle) {
        if (tvTitle != null) {
            tvTitle.setText(alertTitle);
        }
        return this;
    }

    public CashierPayDialogFragment setOnSelectListener(OnSelectListener onSelectListener) {
        this.onSelectListener = onSelectListener;
        return this;
    }

    public static CashierPayDialogFragment build(int currentMode,double payMoney) {
        return new CashierPayDialogFragment(currentMode,payMoney);
    }

    public interface OnSelectListener {
        void onConfirmSelectItem(int currentMode,String payMoney);

        default void onDismiss() {
        }
    }

    public int getCurrentMode() {
        return currentMode;
    }

    public CashierPayDialogFragment setCurrentMode(int currentMode) {
        this.currentMode = currentMode;
        if (currentMode == MODE_CASH){
            fl_cashier_pay_left.setVisibility(View.VISIBLE);
        }else {
            fl_cashier_pay_left.setVisibility(View.GONE);
        }
        return this;
    }
}
