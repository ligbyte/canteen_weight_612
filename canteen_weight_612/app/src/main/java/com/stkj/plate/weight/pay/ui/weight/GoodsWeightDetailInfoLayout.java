package com.stkj.plate.weight.pay.ui.weight;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSON;
import com.st.flowlayout.FlowDataBean;
import com.stkj.plate.weight.R;
import com.stkj.plate.weight.base.greendao.AppGreenDaoOpenHelper;
import com.stkj.plate.weight.base.greendao.GreenDBConstants;
import com.stkj.plate.weight.base.greendao.generate.DaoMaster;
import com.stkj.plate.weight.base.greendao.generate.DaoSession;
import com.stkj.plate.weight.base.greendao.generate.FoodInfoTableDao;
import com.stkj.plate.weight.base.model.BaseNetResponse;
import com.stkj.plate.weight.base.model.CommonExpandItem;
import com.stkj.plate.weight.base.net.ParamsUtils;
import com.stkj.plate.weight.base.ui.widget.CommonExpandListPopWindow;
import com.stkj.plate.weight.base.ui.widget.SelectExpandListPopWindow;
import com.stkj.plate.weight.pay.goods.data.GoodsConstants;
import com.stkj.plate.weight.pay.model.CategoryBean;
import com.stkj.plate.weight.pay.model.FoodCategoryListInfo;
import com.stkj.plate.weight.pay.service.PayService;
import com.stkj.plate.weight.setting.data.PaymentSettingMMKV;
import com.stkj.plate.weight.setting.model.FoodInfoTable;
import com.stkj.common.core.AppManager;
import com.stkj.common.net.retrofit.RetrofitManager;
import com.stkj.common.rx.DefaultObserver;
import com.stkj.common.rx.RxTransformerUtils;
import com.stkj.common.ui.toast.AppToast;
import com.stkj.common.ui.widget.shapelayout.ShapeEditText;
import com.stkj.common.ui.widget.shapelayout.ShapeTextView;
import com.stkj.common.utils.SpanUtils;

import org.greenrobot.greendao.database.Database;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 商品信息布局
 */
public class GoodsWeightDetailInfoLayout extends FrameLayout {

    public final static String TAG = "GoodsWeightDetailInfoLayout";
    //商品添加模式
    public static final int LAYOUT_TYPE_ADD = 0;
    //商品详情模式
    public static final int LAYOUT_TYPE_DETAIL_INFO = 1;
    //商品详情可编辑模式
    public static final int LAYOUT_TYPE_DETAIL_EDIT = 2;
    private int layoutType = LAYOUT_TYPE_ADD;
    private int goodsType = GoodsConstants.TYPE_GOODS_STANDARD;
    private ShapeEditText setGoodsName;
    private ShapeEditText setGoodsQrcode;
    private ShapeEditText et_goods_chuangkou;
    private ShapeTextView stv_goods_jjfs;
    private ShapeTextView stv_goods_jjgg;
    private ShapeEditText set_goods_qrcode;
    private ShapeTextView goods_fl_summary;
    private ShapeTextView goods_lx_summary;
    private String[] mTabs = {};
    private FoodInfoTableDao foodInfoTableDao;
    private DaoSession daoSession;
    public static String queryPricingMethod =  "1";
    private List<FoodInfoTable>  foods;
    private List<FlowDataBean> listTagFl = new ArrayList<>();
    SelectExpandListPopWindow selectExpandListPopWindowTc;
    public GoodsWeightDetailInfoLayout(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public GoodsWeightDetailInfoLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public GoodsWeightDetailInfoLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public void init(Context context, @Nullable AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.include_goods_weight_info, this);
        int color = context.getResources().getColor(R.color.color_FF3C30);

        if (TextUtils.isEmpty(PaymentSettingMMKV.getTab1())) {
            foodCategory("2");
        }


        ShapeTextView stvGoodsQrcodeTitle = (ShapeTextView) findViewById(R.id.stv_goods_qrcode_title);
        SpanUtils.with(stvGoodsQrcodeTitle)
                .append("* ")
                .setForegroundColor(color)
                .append("单价:").create();

        ShapeTextView goods_name_summary = (ShapeTextView) findViewById(R.id.goods_name_summary);
        SpanUtils.with(goods_name_summary)
                .append("* ")
                .setForegroundColor(color)
                .append("菜品名称:").create();


        ShapeTextView goods_chuangkou_summary = (ShapeTextView) findViewById(R.id.goods_chuangkou_summary);
        SpanUtils.with(goods_chuangkou_summary)
                .append("* ")
                .setForegroundColor(color)
                .append("适用窗口:").create();



        ShapeTextView stv_goods_jjfs_summary = (ShapeTextView) findViewById(R.id.stv_goods_jjfs_summary);
        SpanUtils.with(stv_goods_jjfs_summary)
                .append("* ")
                .setForegroundColor(color)
                .append("计价方式:").create();

        ShapeTextView stv_goods_jjgg_summary = (ShapeTextView) findViewById(R.id.stv_goods_jjgg_summary);
        SpanUtils.with(stv_goods_jjgg_summary)
                .append("* ")
                .setForegroundColor(color)
                .append("计价规格:").create();





        ShapeTextView stv_goods_lx_summary = (ShapeTextView) findViewById(R.id.stv_goods_lx_summary);
        SpanUtils.with(stv_goods_lx_summary)
                .append("* ")
                .setForegroundColor(color)
                .append("类型:").create();


        ShapeTextView stv_goods_fl_summary = (ShapeTextView) findViewById(R.id.stv_goods_fl_summary);
        SpanUtils.with(stv_goods_fl_summary)
                .append("* ")
                .setForegroundColor(color)
                .append("分类:").create();


        stv_goods_jjfs = (ShapeTextView) findViewById(R.id.stv_goods_jjfs);
        stv_goods_jjgg = (ShapeTextView) findViewById(R.id.stv_goods_jjgg);
        set_goods_qrcode = (ShapeEditText) findViewById(R.id.set_goods_qrcode);
        goods_fl_summary = (ShapeTextView) findViewById(R.id.goods_fl_summary);
        goods_lx_summary = (ShapeTextView) findViewById(R.id.goods_lx_summary);
//        flowlayout.setOnClickItemListener((v, text,pos) -> {
//            Log.d("TAG", "limepos46 : " + pos);
//            //list.remove(pos);
//
//
//        });


        InputFilter filter = new InputFilter() {
            final Pattern pattern = Pattern.compile("^\\d+(\\.\\d{0,2})?$"); // 匹配数字，且小数部分最多两位

            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                // 将已有的文本和输入的新文本拼接
                String newString = dest.toString().substring(0, dstart) + source.subSequence(start, end) + dest.toString().substring(dend);
                // 如果新字符串为空（也就是删除操作）或者符合正则表达式，则允许输入
                if (newString.isEmpty() || newString.equals(".")) {
                    return null; // 允许输入（因为可能是第一个字符输入小数点，但后面会通过TextWatcher修正）
                }
                Matcher matcher = pattern.matcher(newString);
                if (!matcher.matches()) {
                    return ""; // 不符合条件，不允许输入
                }
                return null; // 允许输入
            }
        };

        set_goods_qrcode.setFilters(new InputFilter[]{filter});

        List<CommonExpandItem> heartBeatExpandListjjgg = new ArrayList<>();
        List<CommonExpandItem> heartBeatExpandListjjfs = new ArrayList<>();
        heartBeatExpandListjjfs.add(new CommonExpandItem(1, "按份"));
        heartBeatExpandListjjfs.add(new CommonExpandItem(2, "计重"));
        stv_goods_jjfs.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                ivHeartbeatInterval.setSelected(true);
                CommonExpandListPopWindow commonExpandListPopWindow = new CommonExpandListPopWindow(getContext());
                commonExpandListPopWindow.setWidth(stv_goods_jjfs.getWidth());
                commonExpandListPopWindow.setHeight(LayoutParams.WRAP_CONTENT);
                commonExpandListPopWindow.setItemClickListener(new CommonExpandListPopWindow.OnExpandItemClickListener() {
                    @Override
                    public void onClickItem(CommonExpandItem commonExpandItem) {
//                        ivHeartbeatInterval.setSelected(false);
                        stv_goods_jjfs.setText(commonExpandItem.getName());
                        if (commonExpandItem.getTypeInt() == 1){
                            stv_goods_jjgg.setText("份");
                            goods_fl_summary.setText("");
                            heartBeatExpandListjjgg.clear();
                            heartBeatExpandListjjgg.add(new CommonExpandItem(1, "份"));
                        }else {
                            stv_goods_jjgg.setText("1kg");
                            goods_fl_summary.setText("");
                            heartBeatExpandListjjgg.clear();
                            heartBeatExpandListjjgg.add(new CommonExpandItem(2, "1kg"));
                        }



                    }
                });
                commonExpandListPopWindow.setExpandItemList(heartBeatExpandListjjfs);
                commonExpandListPopWindow.showAsDropDown(stv_goods_jjfs);
                commonExpandListPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {

                        //ivHeartbeatInterval.setSelected(false);
                    }
                });
            }
        });

        stv_goods_jjgg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                ivHeartbeatInterval.setSelected(true);
                CommonExpandListPopWindow commonExpandListPopWindow = new CommonExpandListPopWindow(getContext());
                commonExpandListPopWindow.setWidth(stv_goods_jjgg.getWidth());
                commonExpandListPopWindow.setHeight(LayoutParams.WRAP_CONTENT);
                commonExpandListPopWindow.setItemClickListener(new CommonExpandListPopWindow.OnExpandItemClickListener() {
                    @Override
                    public void onClickItem(CommonExpandItem commonExpandItem) {
//                        ivHeartbeatInterval.setSelected(false);
                        stv_goods_jjgg.setText(commonExpandItem.getName());
                        if (commonExpandItem.getTypeInt() == 1){
                            stv_goods_jjfs.setText("按份");
                            queryPricingMethod =  "1";
                        }else {
                            stv_goods_jjfs.setText("计重");
                            queryPricingMethod =  "2";
                        }

                        listTagFl = new ArrayList<>();

                    }
                });
                commonExpandListPopWindow.setExpandItemList(heartBeatExpandListjjgg);
                commonExpandListPopWindow.showAsDropDown(stv_goods_jjgg);
                commonExpandListPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {

                        //ivHeartbeatInterval.setSelected(false);
                    }
                });
            }
        });


        goods_fl_summary.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                ivHeartbeatInterval.setSelected(true);
                if (stv_goods_jjfs.getText().toString().trim().equals("按份")){
                    if (!TextUtils.isEmpty(PaymentSettingMMKV.getTab0())){
                        mTabs = JSON.parseObject(PaymentSettingMMKV.getTab0().replace("\"全部\",",""), String[].class);
                    }
                } else if (stv_goods_jjfs.getText().toString().trim().equals("计重")){
                    Log.d(TAG, "limePaymentSettingMMKV.getTab1(): " + PaymentSettingMMKV.getTab1());
                    if (!TextUtils.isEmpty(PaymentSettingMMKV.getTab1())){
                        mTabs = JSON.parseObject(PaymentSettingMMKV.getTab1().replace("\"全部\",",""), String[].class);
                    }
                }else {
                    AppToast.toastMsg("请选择计价方式!");
                    return;
                }

                if (mTabs.length < 1){
                    foodCategory("2");
                    AppToast.toastMsg("无分类信息，请稍后再试!");
                    return;
                }

                List<CommonExpandItem> heartBeatExpandListtab = new ArrayList<>();

                for (int i = 0; i < mTabs.length; i++) {
                    heartBeatExpandListtab.add(new CommonExpandItem(i, mTabs[i]));
                }

                CommonExpandListPopWindow commonExpandListPopWindow = new CommonExpandListPopWindow(getContext());
                commonExpandListPopWindow.setWidth(goods_fl_summary.getWidth());
                commonExpandListPopWindow.setHeight(LayoutParams.WRAP_CONTENT);
                commonExpandListPopWindow.setItemClickListener(new CommonExpandListPopWindow.OnExpandItemClickListener() {
                    @Override
                    public void onClickItem(CommonExpandItem commonExpandItem) {
//                        ivHeartbeatInterval.setSelected(false);
                        goods_fl_summary.setText(commonExpandItem.getName());


                        listTagFl = new ArrayList<>();

                    }
                });
                commonExpandListPopWindow.setExpandItemList(heartBeatExpandListtab);
                commonExpandListPopWindow.showAsDropDown(goods_fl_summary);
                commonExpandListPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {

                        //ivHeartbeatInterval.setSelected(false);
                    }
                });
            }
        });


        List<CommonExpandItem> heartBeatExpandListlx = new ArrayList<>();
        heartBeatExpandListlx.add(new CommonExpandItem(1, "单品"));
        heartBeatExpandListlx.add(new CommonExpandItem(2, "套餐"));
        goods_lx_summary.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                ivHeartbeatInterval.setSelected(true);
                CommonExpandListPopWindow commonExpandListPopWindow = new CommonExpandListPopWindow(getContext());
                commonExpandListPopWindow.setWidth(goods_lx_summary.getWidth());
                commonExpandListPopWindow.setHeight(LayoutParams.WRAP_CONTENT);
                commonExpandListPopWindow.setItemClickListener(new CommonExpandListPopWindow.OnExpandItemClickListener() {
                    @Override
                    public void onClickItem(CommonExpandItem commonExpandItem) {
//                        ivHeartbeatInterval.setSelected(false);
                        goods_lx_summary.setText(commonExpandItem.getName());

                    }
                });
                commonExpandListPopWindow.setExpandItemList(heartBeatExpandListlx);
                commonExpandListPopWindow.showAsDropDown(goods_lx_summary);
                commonExpandListPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {

                        //ivHeartbeatInterval.setSelected(false);
                    }
                });
            }
        });



        setGoodsName = (ShapeEditText) findViewById(R.id.et_goods_name);
        setGoodsQrcode = (ShapeEditText) findViewById(R.id.set_goods_qrcode);
        et_goods_chuangkou = (ShapeEditText) findViewById(R.id.et_goods_chuangkou);
    }

    /**
     * 刷新当前规格和分类
     */
    private void resetGoodsCateSpec() {

    }

    /**
     * 选中称重商品
     */
    private void selectGoodsWeightType() {
        goodsType = GoodsConstants.TYPE_GOODS_WEIGHT;
    }

    /**
     * 选中标准商品
     */
    private void selectGoodsStandardType() {
        goodsType = GoodsConstants.TYPE_GOODS_STANDARD;
    }

    /**
     * 商品分类弹窗
     */
    private CommonExpandListPopWindow goodsCateExpandListPopWindow;


    private CommonExpandListPopWindow goodsSpecExpandListPopWindow;


    public String getGoodsName() {
        return setGoodsName.getText().toString().trim();
    }

    // 商品类型（0 标准商品 1 称重商品)
    public int getGoodsType() {
        return goodsType;
    }

    //条码
    public String getGoodsCode() {
        return setGoodsQrcode.getText().toString().trim();
    }






    //有效天数
    public String getExpireDays() {
        int totalDay = 0;
        return String.valueOf(totalDay);
    }




    /**
     * 重置商品可编辑和清空数据
     */
    public void resetAllLayoutAndData() {
        goodsType = GoodsConstants.TYPE_GOODS_STANDARD;
        //添加商品模式 清空输入框数据
        //商品规格
        selectGoodsStandardType();
        //商品名称
        setGoodsName.setText("");
        //条形码号
        setGoodsQrcode.setText("");

    }

    public void setGoodsName(String goodsName) {
        setGoodsName.setText(goodsName);
    }

    public void setGoodsCode(String goodsCode) {
        setGoodsQrcode.setText(goodsCode);
    }

    public void setGoodsPic(String goodsPic) {

    }


    public ShapeEditText getEt_goods_chuangkou() {
        return et_goods_chuangkou;
    }

    public void setEt_goods_chuangkou(ShapeEditText et_goods_chuangkou) {
        this.et_goods_chuangkou = et_goods_chuangkou;
    }

    /**
     * 菜品分类
     */
    @SuppressLint("AutoDispose")
    public void foodCategory(String pricingMethod) {

        TreeMap<String, String> paramsMap = ParamsUtils.newSortParamsMapWithMode("foodCategory");
        paramsMap.put("pricingMethod", pricingMethod);
        paramsMap.put("pageIndex", String.valueOf(0));
        paramsMap.put("pageSize", String.valueOf(100));
        RetrofitManager.INSTANCE.getDefaultRetrofit()
                .create(PayService.class)
                .foodCategory(ParamsUtils.signSortParamsMap(paramsMap))
                .compose(RxTransformerUtils.mainSchedulers())
                .subscribe(new DefaultObserver<BaseNetResponse<FoodCategoryListInfo>>() {
                    @Override
                    protected void onSuccess(BaseNetResponse<FoodCategoryListInfo> baseNetResponse) {
                        if (baseNetResponse != null && baseNetResponse.getData()  != null && baseNetResponse.getData().getRecords() != null && !baseNetResponse.getData().getRecords().isEmpty()) {
                            List<String> tabList = new ArrayList<>();
                            List<CategoryBean> categroyList = baseNetResponse.getData().getRecords();
                            //categroyList.sort(new CategoryComparator());
                            for (int i = 0; i < categroyList.size(); i++) {
                                tabList.add(categroyList.get(i).getName());
                            }
                            tabList.add(0, "全部");
                            String[] mTitles = tabList.toArray(new String[tabList.size()]);
                            if (pricingMethod.equals("2")){
                                PaymentSettingMMKV.putTab1(JSON.toJSONString(mTitles));
                                PaymentSettingMMKV.putTab1List(JSON.toJSONString(categroyList));
                            }else {
                                PaymentSettingMMKV.putTab0(JSON.toJSONString(mTitles));
                                PaymentSettingMMKV.putTab0List(JSON.toJSONString(categroyList));
                            }

                        } else {

                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }


    private void initFoodInfoTableDao() {
        AppGreenDaoOpenHelper daoOpenHelper = new AppGreenDaoOpenHelper(AppManager.INSTANCE.getApplication(), GreenDBConstants.FACE_DB_NAME, null);
        Database database = daoOpenHelper.getWritableDb();
        DaoMaster daoMaster = new DaoMaster(database);
        daoSession = daoMaster.newSession();
        foodInfoTableDao = daoSession.getFoodInfoTableDao();
    }


    public List<FoodInfoTable> getFoods() {
        return foods;
    }

    public ShapeTextView getStv_goods_jjfs() {
        return stv_goods_jjfs;
    }

    public ShapeTextView getStv_goods_jjgg() {
        return stv_goods_jjgg;
    }

    public ShapeTextView getGoods_fl_summary() {
        return goods_fl_summary;
    }

//    public ShapeTextView getGoods_tc_summary() {
//        return goods_tc_summary;
//    }

    public ShapeEditText getSet_goods_qrcode() {
        return set_goods_qrcode;
    }



    public ShapeTextView getGoods_lx_summary() {
        return goods_lx_summary;
    }

    public List<FlowDataBean> getListTagFl() {
        return listTagFl;
    }

    public void setListTagFl(List<FlowDataBean> listTagFl) {
        this.listTagFl = listTagFl;
    }
}
