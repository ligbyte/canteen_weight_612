package com.stkj.plate.weight.pay.ui.weight;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.net.Uri;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.st.flowlayout.FlowDataBean;
import com.st.flowlayout.TagFlowLayout;
import com.stkj.plate.weight.base.greendao.AppGreenDaoOpenHelper;
import com.stkj.plate.weight.base.greendao.GreenDBConstants;
import com.stkj.plate.weight.base.greendao.generate.DaoMaster;
import com.stkj.plate.weight.base.greendao.generate.DaoSession;
import com.stkj.plate.weight.base.greendao.generate.FoodInfoTableDao;
import com.stkj.plate.weight.base.model.BaseNetResponse;
import com.stkj.plate.weight.base.net.ParamsUtils;
import com.stkj.plate.weight.base.ui.widget.SelectExpandListPopWindow;
import com.stkj.plate.weight.pay.helper.GoodsCateSpecHelper;
import com.stkj.plate.weight.pay.model.CategoryBean;
import com.stkj.plate.weight.pay.model.FoodCategoryListInfo;
import com.stkj.plate.weight.pay.model.TTSSpeakEvent;
import com.stkj.plate.weight.pay.service.PayService;
import com.stkj.plate.weight.setting.data.PaymentSettingMMKV;
import com.stkj.plate.weight.setting.model.FoodInfoTable;
import com.stkj.common.core.ActivityHolderFactory;
import com.stkj.common.core.AppManager;
import com.stkj.common.net.retrofit.RetrofitManager;
import com.stkj.common.rx.DefaultObserver;
import com.stkj.common.rx.RxTransformerUtils;
import com.stkj.common.storage.StorageHelper;
import com.stkj.common.ui.activity.BaseActivity;
import com.stkj.common.ui.toast.AppToast;
import com.stkj.common.ui.widget.shapelayout.ShapeEditText;
import com.stkj.common.ui.widget.shapelayout.ShapeLinearLayout;
import com.stkj.common.ui.widget.shapelayout.ShapeTextView;
import com.stkj.common.utils.SpanUtils;
import com.stkj.plate.weight.R;
import com.stkj.plate.weight.base.model.CommonExpandItem;
import com.stkj.plate.weight.base.ui.dialog.CommonAlertDialogFragment;
import com.stkj.plate.weight.base.ui.dialog.CommonInputDialogFragment;
import com.stkj.plate.weight.base.ui.widget.CommonExpandListPopWindow;
import com.stkj.plate.weight.base.upload.UploadFileHelper;
import com.stkj.plate.weight.base.utils.CommonDialogUtils;
import com.stkj.plate.weight.pay.goods.callback.OnAddGoodsCateListener;
import com.stkj.plate.weight.pay.goods.callback.OnAddGoodsSpecListener;
import com.stkj.plate.weight.pay.goods.callback.OnCapturePicListener;
import com.stkj.plate.weight.pay.goods.callback.OnDelGoodsCateListener;
import com.stkj.plate.weight.pay.goods.callback.OnDelGoodsSpecListener;
import com.stkj.plate.weight.pay.goods.callback.OnGetGoodsCateListListener;
import com.stkj.plate.weight.pay.goods.callback.OnGetGoodsSpecListListener;
import com.stkj.plate.weight.pay.goods.data.GoodsConstants;
import com.stkj.plate.weight.pay.goods.model.GoodsCate;
import com.stkj.plate.weight.pay.goods.model.GoodsSaleListInfo;
import com.stkj.plate.weight.pay.goods.model.GoodsSpec;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.query.QueryBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import top.zibin.luban.Luban;

/**
 * 商品信息布局
 */
public class GoodsDetailInfoLayout extends FrameLayout {

    public final static String TAG = "GoodsDetailInfoLayout";
    //商品添加模式
    public static final int LAYOUT_TYPE_ADD = 0;
    //商品详情模式
    public static final int LAYOUT_TYPE_DETAIL_INFO = 1;
    //商品详情可编辑模式
    public static final int LAYOUT_TYPE_DETAIL_EDIT = 2;
    private int layoutType = LAYOUT_TYPE_ADD;
    private int goodsType = GoodsConstants.TYPE_GOODS_STANDARD;
    //商品规格
    private GoodsSpec mGoodsSpec = new GoodsSpec();
    //商品分类
    private GoodsCate mGoodsCate = new GoodsCate();
    private ShapeEditText setGoodsName;
    private ShapeEditText setGoodsQrcode;
    private UploadPicItemLayout uploadItemPic1;
    private UploadPicItemLayout uploadItemPic2;
    private ShapeEditText setGoodsComments;
    private FrameLayout flGoodsCate;
    private ShapeTextView stvGoodsCate;
    private ImageView ivGoodsCateArrow;
    private ShapeEditText et_goods_canting;
    private ShapeEditText et_goods_chuangkou;
    private ShapeTextView stv_goods_jjfs;
    private ShapeTextView stv_goods_jjgg;
    private ShapeEditText set_goods_qrcode;
    private ShapeTextView goods_fl_summary;
    private ShapeEditText set_goods_comments;
    private ShapeEditText set_goods_sort;
    private ShapeTextView goods_lx_summary;
    private ShapeLinearLayout goods_tc_summary;
    private TagFlowLayout flowlayout;
    private ShapeTextView flowlayout_empty;
    private String[] mTabs = {};
    private FoodInfoTableDao foodInfoTableDao;
    private DaoSession daoSession;
    public static String queryPricingMethod =  "1";
    private List<FoodInfoTable>  foods;
    private List<FlowDataBean> listTagFl = new ArrayList<>();
    SelectExpandListPopWindow selectExpandListPopWindowTc;
    public GoodsDetailInfoLayout(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public GoodsDetailInfoLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public GoodsDetailInfoLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public void init(Context context, @Nullable AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.include_goods_detail_info, this);
        int color = context.getResources().getColor(R.color.color_FF3C30);

        if (TextUtils.isEmpty(PaymentSettingMMKV.getTab1())) {
            foodCategory("2");
        }

        ShapeTextView stvGoodsCateTitle = (ShapeTextView) findViewById(R.id.stv_goods_cate_title);
        SpanUtils.with(stvGoodsCateTitle)
                .append("*")
                .setForegroundColor(color)
                .append("商品分类").create();
//        ShapeTextView stvGoodsNameTitle = (ShapeTextView) findViewById(R.id.stv_goods_name_title);
//        SpanUtils.with(stvGoodsNameTitle)
//                .append("*")
//                .setForegroundColor(color)
//                .append("排序").create();
        ShapeTextView stvGoodsQrcodeTitle = (ShapeTextView) findViewById(R.id.stv_goods_qrcode_title);
        SpanUtils.with(stvGoodsQrcodeTitle)
                .append("*")
                .setForegroundColor(color)
                .append("单价").create();

        ShapeTextView goods_name_summary = (ShapeTextView) findViewById(R.id.goods_name_summary);
        SpanUtils.with(goods_name_summary)
                .append("*")
                .setForegroundColor(color)
                .append("菜品/套餐名称:").create();


        ShapeTextView goods_canting_summary = (ShapeTextView) findViewById(R.id.goods_canting_summary);
        SpanUtils.with(goods_canting_summary)
                .append("*")
                .setForegroundColor(color)
                .append("适用餐厅:").create();


        ShapeTextView goods_chuangkou_summary = (ShapeTextView) findViewById(R.id.goods_chuangkou_summary);
        SpanUtils.with(goods_chuangkou_summary)
                .append("*")
                .setForegroundColor(color)
                .append("适用窗口:").create();


        ShapeTextView stv_goods_cate_title = (ShapeTextView) findViewById(R.id.stv_goods_cate_title);
        SpanUtils.with(stv_goods_cate_title)
                .append("*")
                .setForegroundColor(color)
                .append("适用餐别:").create();


        ShapeTextView stv_goods_jjfs_summary = (ShapeTextView) findViewById(R.id.stv_goods_jjfs_summary);
        SpanUtils.with(stv_goods_jjfs_summary)
                .append("*")
                .setForegroundColor(color)
                .append("计价方式:").create();

        ShapeTextView stv_goods_jjgg_summary = (ShapeTextView) findViewById(R.id.stv_goods_jjgg_summary);
        SpanUtils.with(stv_goods_jjgg_summary)
                .append("*")
                .setForegroundColor(color)
                .append("计价规格:").create();


        LinearLayout layout_tc = (LinearLayout) findViewById(R.id.layout_tc);
         goods_tc_summary = (ShapeLinearLayout) findViewById(R.id.goods_tc_summary);
//        ShapeTextView stv_goods_tc_summary = (ShapeTextView) findViewById(R.id.stv_goods_tc_summary);
//        SpanUtils.with(stv_goods_tc_summary)
//                .append("*")
//                .setForegroundColor(color)
//                .append("套餐:").create();

        ShapeTextView stv_goods_lx_summary = (ShapeTextView) findViewById(R.id.stv_goods_lx_summary);
        SpanUtils.with(stv_goods_lx_summary)
                .append("*")
                .setForegroundColor(color)
                .append("类型:").create();


        ShapeTextView stv_goods_fl_summary = (ShapeTextView) findViewById(R.id.stv_goods_fl_summary);
        SpanUtils.with(stv_goods_fl_summary)
                .append("*")
                .setForegroundColor(color)
                .append("分类:").create();

        flGoodsCate = (FrameLayout) findViewById(R.id.fl_goods_cate);
        stvGoodsCate = (ShapeTextView) findViewById(R.id.stv_goods_cate);
        ivGoodsCateArrow = (ImageView) findViewById(R.id.iv_goods_cate_arrow);
        stvGoodsCate.setOnClickListener(goodsCateClick);
        ivGoodsCateArrow.setOnClickListener(goodsCateClick);

        stv_goods_jjfs = (ShapeTextView) findViewById(R.id.stv_goods_jjfs);
        stv_goods_jjgg = (ShapeTextView) findViewById(R.id.stv_goods_jjgg);
        set_goods_qrcode = (ShapeEditText) findViewById(R.id.set_goods_qrcode);
        goods_fl_summary = (ShapeTextView) findViewById(R.id.goods_fl_summary);
        set_goods_comments =  (ShapeEditText) findViewById(R.id.set_goods_comments);
        set_goods_sort = (ShapeEditText) findViewById(R.id.set_goods_sort);
        goods_lx_summary = (ShapeTextView) findViewById(R.id.goods_lx_summary);
        flowlayout = (TagFlowLayout) findViewById(R.id.flowlayout);
        flowlayout_empty = (ShapeTextView) findViewById(R.id.flowlayout_empty);
//        flowlayout.setOnClickItemListener((v, text,pos) -> {
//            Log.d("TAG", "limepos46 : " + pos);
//            //list.remove(pos);
//
//
//        });

        flowlayout.setOnClickLongDelItemListener((v, text, pos) -> {
            for (int i = 0; i < listTagFl.size(); i++) {
                if (text.equals(listTagFl.get(i).getItemText())){
                    listTagFl.remove(i);
                }
            }
            if (listTagFl.size() > 5) {
                ViewGroup.LayoutParams params = layout_tc.getLayoutParams();
                params.height = 114;
                layout_tc.setLayoutParams(params);
            }else {
                int doubleHeight = 57;
                ViewGroup.LayoutParams params = layout_tc.getLayoutParams();
                params.height = doubleHeight;
                layout_tc.setLayoutParams(params);
            }

            if (listTagFl.size() > 0){
                flowlayout_empty.setVisibility(GONE);
                flowlayout.setVisibility(VISIBLE);
            }else {
                flowlayout_empty.setVisibility(VISIBLE);
                flowlayout.setVisibility(GONE);
            }
            Log.e(TAG, "limeflowlayout height 251: " + flowlayout.getParentHeightTargetSize());
        });
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
        stv_goods_jjfs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ivHeartbeatInterval.setSelected(true);
                CommonExpandListPopWindow commonExpandListPopWindow = new CommonExpandListPopWindow(getContext());
                commonExpandListPopWindow.setWidth(stv_goods_jjfs.getWidth());
                commonExpandListPopWindow.setHeight(FrameLayout.LayoutParams.WRAP_CONTENT);
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

                        listTagFl = new ArrayList<>();
                        flowlayout.setTextList(listTagFl);
                        if (listTagFl.size() > 5) {
                            ViewGroup.LayoutParams params = layout_tc.getLayoutParams();
                            params.height = 114;
                            layout_tc.setLayoutParams(params);
                        }else {
                            int doubleHeight = 57;
                            ViewGroup.LayoutParams params = layout_tc.getLayoutParams();
                            params.height = doubleHeight;
                            layout_tc.setLayoutParams(params);
                        }

                        if (listTagFl.size() > 0){
                            flowlayout_empty.setVisibility(GONE);
                            flowlayout.setVisibility(VISIBLE);
                        }else {
                            flowlayout_empty.setVisibility(VISIBLE);
                            flowlayout.setVisibility(GONE);
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

        stv_goods_jjgg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ivHeartbeatInterval.setSelected(true);
                CommonExpandListPopWindow commonExpandListPopWindow = new CommonExpandListPopWindow(getContext());
                commonExpandListPopWindow.setWidth(stv_goods_jjgg.getWidth());
                commonExpandListPopWindow.setHeight(FrameLayout.LayoutParams.WRAP_CONTENT);
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
                        flowlayout.setTextList(listTagFl);
                        if (listTagFl.size() > 5) {
                            ViewGroup.LayoutParams params = layout_tc.getLayoutParams();
                            params.height = 114;
                            layout_tc.setLayoutParams(params);
                        }else {
                            int doubleHeight = 57;
                            ViewGroup.LayoutParams params = layout_tc.getLayoutParams();
                            params.height = doubleHeight;
                            layout_tc.setLayoutParams(params);
                        }

                        if (listTagFl.size() > 0){
                            flowlayout_empty.setVisibility(GONE);
                            flowlayout.setVisibility(VISIBLE);
                        }else {
                            flowlayout_empty.setVisibility(VISIBLE);
                            flowlayout.setVisibility(GONE);
                        }
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


        goods_fl_summary.setOnClickListener(new View.OnClickListener() {
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
                commonExpandListPopWindow.setHeight(FrameLayout.LayoutParams.WRAP_CONTENT);
                commonExpandListPopWindow.setItemClickListener(new CommonExpandListPopWindow.OnExpandItemClickListener() {
                    @Override
                    public void onClickItem(CommonExpandItem commonExpandItem) {
//                        ivHeartbeatInterval.setSelected(false);
                        goods_fl_summary.setText(commonExpandItem.getName());


                        listTagFl = new ArrayList<>();
                        flowlayout.setTextList(listTagFl);
                        if (listTagFl.size() > 5) {
                            ViewGroup.LayoutParams params = layout_tc.getLayoutParams();
                            params.height = 114;
                            layout_tc.setLayoutParams(params);
                        }else {
                            int doubleHeight = 57;
                            ViewGroup.LayoutParams params = layout_tc.getLayoutParams();
                            params.height = doubleHeight;
                            layout_tc.setLayoutParams(params);
                        }

                        if (listTagFl.size() > 0){
                            flowlayout_empty.setVisibility(GONE);
                            flowlayout.setVisibility(VISIBLE);
                        }else {
                            flowlayout_empty.setVisibility(VISIBLE);
                            flowlayout.setVisibility(GONE);
                        }
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
        goods_lx_summary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ivHeartbeatInterval.setSelected(true);
                CommonExpandListPopWindow commonExpandListPopWindow = new CommonExpandListPopWindow(getContext());
                commonExpandListPopWindow.setWidth(goods_lx_summary.getWidth());
                commonExpandListPopWindow.setHeight(FrameLayout.LayoutParams.WRAP_CONTENT);
                commonExpandListPopWindow.setItemClickListener(new CommonExpandListPopWindow.OnExpandItemClickListener() {
                    @Override
                    public void onClickItem(CommonExpandItem commonExpandItem) {
//                        ivHeartbeatInterval.setSelected(false);
                        goods_lx_summary.setText(commonExpandItem.getName());
                        if (commonExpandItem.getTypeInt() == 2){
                            layout_tc.setVisibility(View.VISIBLE);
                        }else {
                            layout_tc.setVisibility(View.GONE);
                        }
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



        goods_tc_summary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(stv_goods_jjfs.getText().toString().trim())){
                    AppToast.toastMsg("请选择计价方式!");
                    return;
                }

                if (TextUtils.isEmpty(goods_fl_summary.getText().toString().trim())){
                    AppToast.toastMsg("请选择分类!");
                    return;
                }

                if (foodInfoTableDao == null){
                    initFoodInfoTableDao();
                }
                QueryBuilder<FoodInfoTable> qbCount   = foodInfoTableDao.queryBuilder();
                qbCount.where(FoodInfoTableDao.Properties.Status.eq(1));
                qbCount.where(FoodInfoTableDao.Properties.DeleteFlag.eq("NOT_DELETE"));
                qbCount.where(FoodInfoTableDao.Properties.Type.eq(1));

                if (stv_goods_jjfs.getText().toString().trim().equals("按份")){
                    queryPricingMethod =  "1";
                }else if (stv_goods_jjfs.getText().toString().trim().equals("计重")){
                    queryPricingMethod =  "2";
                }else {
                    AppToast.toastMsg("请选择计价方式!");
                    return;
                }

                if (!TextUtils.isEmpty(queryPricingMethod)) {
                    qbCount.where(FoodInfoTableDao.Properties.PricingMethod.eq(queryPricingMethod));
                }

                String queryTab = goods_fl_summary.getText().toString().trim();
                qbCount.where(FoodInfoTableDao.Properties.CategoryMap.like("%\"" + queryTab + "\"%"));
//                qbCount.where(new WhereCondition.StringCondition(
//                        FoodInfoTableDao.Properties.CategoryMap.columnName + " LIKE ? OR " +
//                                FoodInfoTableDao.Properties.CategoryMap.columnName + " LIKE ?",
//                        queryTab + "\"}", queryTab + "\","
//                ));
//                for (String tab:mTabs){
//                    if (!TextUtils.isEmpty(queryTab) && !tab.equals(queryTab) && tab.contains(queryTab)){
//                        if (!tab.equals("全部")) {
//                            qbCount.where(new WhereCondition.StringCondition(
//                                    FoodInfoTableDao.Properties.CategoryMap.columnName + " LIKE ? AND " +
//                                            FoodInfoTableDao.Properties.CategoryMap.columnName + " NOT LIKE ?",
//                                    queryTab, tab
//                            ));
//                        }
//                    }
//                }

                foods   =  qbCount.list();
                for (int i = 0; i < foods.size(); i++) {
                    foods.get(i).setSelected(false);
                }

                if  (listTagFl != null || listTagFl.size() > 0){
                    for (int i = 0; i < listTagFl.size(); i++) {
                        for (int j = 0; j < foods.size(); j++) {
                            if (listTagFl.get(i).getItemText().toString().trim().equals(foods.get(j).getName())) {
                                foods.get(j).setSelected(true);
                                break;
                            }
                        }
                    }


                }

                List<FoodInfoTable> heartBeatExpandListTc = new ArrayList<>();
                if (foods != null && foods.size() > 0) {
                    for (FoodInfoTable item : foods) {
                        heartBeatExpandListTc.add(item);
                    }
                }else {
                    AppToast.toastMsg("没有找到相关套餐单品!");
                    EventBus.getDefault().post(new TTSSpeakEvent("没有找到相关套餐单品"));
                    return;
                }
                selectExpandListPopWindowTc = new SelectExpandListPopWindow(getContext());
                selectExpandListPopWindowTc.setWidth(goods_tc_summary.getWidth());
                selectExpandListPopWindowTc.setHeight(FrameLayout.LayoutParams.WRAP_CONTENT);
                selectExpandListPopWindowTc.getSelectListShowAdapter().setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {

                        if (!selectExpandListPopWindowTc.getDataList().get(position).isSelected()) {
                            selectExpandListPopWindowTc.getDataList().get(position).setSelected(true);
                            listTagFl.add(new FlowDataBean(selectExpandListPopWindowTc.getDataList().get(position).getName(), 0));
                            flowlayout.setVerticalMargin(7);
                            flowlayout.setHorizontalMargin(7);
                            flowlayout.setMaxLines(2);
                            flowlayout.setTextList(listTagFl);
                        }else {
                            selectExpandListPopWindowTc.getDataList().get(position).setSelected(false);
                            for (int i = 0; i < listTagFl.size(); i++) {
                                if (selectExpandListPopWindowTc.getDataList().get(position).getName().equals(listTagFl.get(i).getItemText())){
                                    listTagFl.remove(i);
                                }
                            }
                            flowlayout.setTextList(listTagFl);
                        }
                        selectExpandListPopWindowTc.getSelectListShowAdapter().notifyDataSetChanged();




                        if (listTagFl.size() > 5) {
                            ViewGroup.LayoutParams params = layout_tc.getLayoutParams();
                            params.height = 114;
                            layout_tc.setLayoutParams(params);
                        }else {
                            int doubleHeight = 57;
                            ViewGroup.LayoutParams params = layout_tc.getLayoutParams();
                            params.height = doubleHeight;
                            layout_tc.setLayoutParams(params);
                        }

                        if (listTagFl.size() > 0){
                            flowlayout_empty.setVisibility(GONE);
                            flowlayout.setVisibility(VISIBLE);
                        }else {
                            flowlayout_empty.setVisibility(VISIBLE);
                            flowlayout.setVisibility(GONE);
                        }

                    }
                });

                selectExpandListPopWindowTc.setExpandItemList(heartBeatExpandListTc);
                selectExpandListPopWindowTc.showAsDropDown(goods_tc_summary);
                selectExpandListPopWindowTc.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        //ivHeartbeatInterval.setSelected(false);
                    }
                });
            }
        });

        setGoodsName = (ShapeEditText) findViewById(R.id.et_goods_name);
        setGoodsQrcode = (ShapeEditText) findViewById(R.id.set_goods_qrcode);
        uploadItemPic1 = (UploadPicItemLayout) findViewById(R.id.upload_item_pic1);
        uploadItemPic1.setCapturePicListener(buildOnCaptureClickListener(1));
        uploadItemPic2 = (UploadPicItemLayout) findViewById(R.id.upload_item_pic2);
        uploadItemPic2.setCapturePicListener(buildOnCaptureClickListener(2));
        setGoodsComments = (ShapeEditText) findViewById(R.id.set_goods_comments);

        et_goods_canting = (ShapeEditText) findViewById(R.id.et_goods_canting);
        et_goods_chuangkou = (ShapeEditText) findViewById(R.id.et_goods_chuangkou);


        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.GoodsDetailInfoLayout);
            layoutType = a.getInt(R.styleable.GoodsDetailInfoLayout_layout_type, LAYOUT_TYPE_ADD);
            setGoodsLayoutType(layoutType);
        }
    }

    /**
     * 刷新当前规格和分类
     */
    private void resetGoodsCateSpec() {
        mGoodsCate.reset();
        stvGoodsCate.setText("");
        mGoodsSpec.reset();
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

    private OnClickListener goodsCateClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            ivGoodsCateArrow.setSelected(true);
            if (goodsCateExpandListPopWindow == null) {
                goodsCateExpandListPopWindow = new CommonExpandListPopWindow(getContext());
                goodsCateExpandListPopWindow.setWidth(flGoodsCate.getWidth());
                goodsCateExpandListPopWindow.setHeight(LayoutParams.WRAP_CONTENT);
                goodsCateExpandListPopWindow.setCustomItemClickListener(new CommonExpandListPopWindow.OnCustomItemClickListener() {
                    @Override
                    public void onClickCustom() {
                        CommonInputDialogFragment.build()
                                .setTitle("自定义商品分类")
                                .setOnInputListener(new CommonInputDialogFragment.OnInputListener() {
                                    @Override
                                    public void onInputEnd(String input) {
                                        //添加商品分类
                                        if (!TextUtils.isEmpty(input)) {
                                            addGoodsCate(input);
                                        }
                                    }
                                }).show(getContext());
                    }
                });
                goodsCateExpandListPopWindow.setItemClickListener(new CommonExpandListPopWindow.OnExpandItemClickListener() {
                    @Override
                    public void onClickItem(CommonExpandItem commonExpandItem) {
                        ivGoodsCateArrow.setSelected(false);
                        stvGoodsCate.setText(commonExpandItem.getName());
                        mGoodsCate.setId(commonExpandItem.getType());
                        mGoodsCate.setName(commonExpandItem.getName());
                    }

                    @Override
                    public void onDelItem(CommonExpandItem commonExpandItem) {
                        CommonAlertDialogFragment.build()
                                .setAlertTitleTxt("提示")
                                .setAlertContentTxt("确认删除 '" + commonExpandItem.getName() + "' 分类吗?")
                                .setLeftNavTxt("删除")
                                .setLeftNavClickListener(new CommonAlertDialogFragment.OnSweetClickListener() {
                                    @Override
                                    public void onClick(CommonAlertDialogFragment alertDialogFragment) {
                                        delGoodsCate(commonExpandItem.getType(), commonExpandItem.getName());
                                    }
                                }).setRightNavTxt("取消")
                                .show(getContext());
                    }
                });
            }
            goodsCateExpandListPopWindow.setNeedCustomItem(true);
            goodsCateExpandListPopWindow.setAllowDelItem(true);
            goodsCateExpandListPopWindow.setExpandItemList(getGoodsCateExpandList(goodsType));
            goodsCateExpandListPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    ivGoodsCateArrow.setSelected(false);
                }
            });
            goodsCateExpandListPopWindow.showAsDropDown(flGoodsCate);
        }
    };

    private CommonExpandListPopWindow goodsSpecExpandListPopWindow;

    private OnClickListener goodsSpecClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (goodsSpecExpandListPopWindow == null) {
                goodsSpecExpandListPopWindow = new CommonExpandListPopWindow(getContext());
                goodsSpecExpandListPopWindow.setHeight(LayoutParams.WRAP_CONTENT);
                goodsSpecExpandListPopWindow.setCustomItemClickListener(new CommonExpandListPopWindow.OnCustomItemClickListener() {
                    @Override
                    public void onClickCustom() {
                        CommonInputDialogFragment.build()
                                .setTitle("自定义商品规格")
                                .setOnInputListener(new CommonInputDialogFragment.OnInputListener() {
                                    @Override
                                    public void onInputEnd(String input) {
                                        //添加商品规格
                                        if (!TextUtils.isEmpty(input)) {
                                            addGoodsSpec(input);
                                        }
                                    }
                                }).show(getContext());
                    }
                });
                goodsSpecExpandListPopWindow.setItemClickListener(new CommonExpandListPopWindow.OnExpandItemClickListener() {
                    @Override
                    public void onClickItem(CommonExpandItem commonExpandItem) {
                        mGoodsSpec.setId(commonExpandItem.getType());
                        mGoodsSpec.setName(commonExpandItem.getName());
                    }

                    @Override
                    public void onDelItem(CommonExpandItem commonExpandItem) {
                        CommonAlertDialogFragment.build()
                                .setAlertTitleTxt("提示")
                                .setAlertContentTxt("确认删除 '" + commonExpandItem.getName() + "' 规格吗?")
                                .setLeftNavTxt("删除")
                                .setLeftNavClickListener(new CommonAlertDialogFragment.OnSweetClickListener() {
                                    @Override
                                    public void onClick(CommonAlertDialogFragment alertDialogFragment) {
                                        delGoodsSpec(commonExpandItem.getType(), commonExpandItem.getName());
                                    }
                                }).setRightNavTxt("取消")
                                .show(getContext());
                    }
                });
            }
            goodsSpecExpandListPopWindow.setNeedCustomItem(goodsType == GoodsConstants.TYPE_GOODS_STANDARD);
            goodsSpecExpandListPopWindow.setAllowDelItem(goodsType == GoodsConstants.TYPE_GOODS_STANDARD);
            goodsSpecExpandListPopWindow.setExpandItemList(getGoodsSpecExpandList(goodsType));
            goodsSpecExpandListPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                }
            });
        }
    };

    /**
     * 添加会删除成功后重新获取商品分类
     */
    private void requestGoodsCateList() {
        Context context = getContext();
        if (context instanceof BaseActivity) {
            BaseActivity baseActivity = (BaseActivity) context;
            GoodsCateSpecHelper goodsCateSpecHelper = baseActivity.getWeakRefHolder(GoodsCateSpecHelper.class);
            goodsCateSpecHelper.requestCateList(goodsType, new OnGetGoodsCateListListener() {
                @Override
                public void onGetCateListSuccess(int goodsType, List<GoodsCate> goodsCateList) {
                    baseActivity.hideLoadingDialog();
                }

                @Override
                public void onGetCateListError(int goodsType, String msg) {
                    baseActivity.hideLoadingDialog();
                }
            });
        }
    }

    /**
     * 添加会删除成功后重新获取商品规格
     */
    private void requestGoodsSpecList() {
        Context context = getContext();
        if (context instanceof BaseActivity) {
            BaseActivity baseActivity = (BaseActivity) context;
            GoodsCateSpecHelper goodsCateSpecHelper = baseActivity.getWeakRefHolder(GoodsCateSpecHelper.class);
            goodsCateSpecHelper.requestSpecList(goodsType, new OnGetGoodsSpecListListener() {
                @Override
                public void onGetSpecListSuccess(int goodsType, List<GoodsSpec> goodsSpecList) {
                    baseActivity.hideLoadingDialog();
                }

                @Override
                public void onGetSpecListError(int goodsType, String msg) {
                    baseActivity.hideLoadingDialog();
                }
            });
        }
    }

    /**
     * 删除商品规格
     */
    private void delGoodsCate(String id, String goodsCate) {
        Context context = getContext();
        if (context instanceof BaseActivity) {
            BaseActivity baseActivity = (BaseActivity) context;
            GoodsCateSpecHelper goodsCateSpecHelper = baseActivity.getWeakRefHolder(GoodsCateSpecHelper.class);
            baseActivity.showLoadingDialog();
            goodsCateSpecHelper.delGoodsCate(id, goodsType, goodsCate, new OnDelGoodsCateListener() {
                @Override
                public void onDelCateSuccess(String cateName) {
                    if (TextUtils.equals(id, mGoodsCate.getId())) {
                        mGoodsCate.reset();
                        stvGoodsCate.setText("");
                    }
                    AppToast.toastMsg("删除成功");
                    requestGoodsCateList();
                }

                @Override
                public void onDelCateError(String cateName, String msg) {
                    CommonDialogUtils.showTipsDialog(baseActivity, "删除分类失败: " + msg);
                    baseActivity.hideLoadingDialog();
                }
            });
        }
    }

    /**
     * 删除商品规格
     */
    private void delGoodsSpec(String id, String goodsSpec) {
        Context context = getContext();
        if (context instanceof BaseActivity) {
            BaseActivity baseActivity = (BaseActivity) context;
            GoodsCateSpecHelper goodsCateSpecHelper = baseActivity.getWeakRefHolder(GoodsCateSpecHelper.class);
            baseActivity.showLoadingDialog();
            goodsCateSpecHelper.delGoodsSpec(id, goodsType, goodsSpec, new OnDelGoodsSpecListener() {
                @Override
                public void onDelSpecSuccess(String specName) {
                    if (TextUtils.equals(id, mGoodsSpec.getId())) {
                        mGoodsSpec.reset();
                    }
                    AppToast.toastMsg("删除成功");
                    requestGoodsSpecList();
                }

                @Override
                public void onDelSpecError(String specName, String msg) {
                    CommonDialogUtils.showTipsDialog(baseActivity, "删除规格失败: " + msg);
                    baseActivity.hideLoadingDialog();
                }
            });
        }
    }

    /**
     * 添加商品规格
     */
    private void addGoodsSpec(String goodsSpec) {
        Context context = getContext();
        if (context instanceof BaseActivity) {
            BaseActivity baseActivity = (BaseActivity) context;
            GoodsCateSpecHelper goodsCateSpecHelper = baseActivity.getWeakRefHolder(GoodsCateSpecHelper.class);
            baseActivity.showLoadingDialog();
            goodsCateSpecHelper.addGoodsSpec(goodsSpec, goodsType, new OnAddGoodsSpecListener() {
                @Override
                public void onAddSpecSuccess(GoodsSpec spec) {
                    mGoodsSpec = spec;
                    AppToast.toastMsg("添加成功");
                    requestGoodsSpecList();
                }

                @Override
                public void onAddSpecError(String specName, String msg) {
                    CommonDialogUtils.showTipsDialog(baseActivity, "添加规格失败: " + msg);
                    baseActivity.hideLoadingDialog();
                }
            });
        }
    }

    /**
     * 添加商品分类
     */
    private void addGoodsCate(String goodsCate) {
        Context context = getContext();
        if (context instanceof BaseActivity) {
            BaseActivity baseActivity = (BaseActivity) context;
            GoodsCateSpecHelper goodsCateSpecHelper = baseActivity.getWeakRefHolder(GoodsCateSpecHelper.class);
            baseActivity.showLoadingDialog();
            goodsCateSpecHelper.addGoodsCate(goodsCate, goodsType, new OnAddGoodsCateListener() {

                @Override
                public void onAddCateSuccess(GoodsCate cate) {
                    mGoodsCate = cate;
                    stvGoodsCate.setText(cate.getName());
                    AppToast.toastMsg("添加成功");
                    requestGoodsCateList();
                }

                @Override
                public void onAddCateError(String cateName, String msg) {
                    CommonDialogUtils.showTipsDialog(baseActivity, "添加分类失败: " + msg);
                    baseActivity.hideLoadingDialog();
                }
            });
        }
    }

    /**
     * 获取商品规格
     */
    private List<CommonExpandItem> getGoodsSpecExpandList(int goodsType) {
        GoodsCateSpecHelper goodsCateSpecHelper = ActivityHolderFactory.get(GoodsCateSpecHelper.class, getContext());
        if (goodsCateSpecHelper != null) {
            List<GoodsSpec> goodsSpecByType = goodsCateSpecHelper.getGoodsSpecByType(goodsType);
            if (goodsSpecByType != null && !goodsSpecByType.isEmpty()) {
                List<CommonExpandItem> expandItemList = new ArrayList<>();
                for (int i = 0; i < goodsSpecByType.size(); i++) {
                    GoodsSpec spec = goodsSpecByType.get(i);
                    CommonExpandItem expandItem = new CommonExpandItem(spec.getId(), spec.getName());
                    expandItemList.add(expandItem);
                }
                return expandItemList;
            }
        }
        return null;
    }

    /**
     * 获取商品分类
     */
    private List<CommonExpandItem> getGoodsCateExpandList(int goodsType) {
        GoodsCateSpecHelper goodsCateSpecHelper = ActivityHolderFactory.get(GoodsCateSpecHelper.class, getContext());
        if (goodsCateSpecHelper != null) {
            List<GoodsCate> goodsCateByType = goodsCateSpecHelper.getGoodsCateByType(goodsType);
            if (goodsCateByType != null && !goodsCateByType.isEmpty()) {
                List<CommonExpandItem> expandItemList = new ArrayList<>();
                for (int i = 0; i < goodsCateByType.size(); i++) {
                    GoodsCate cate = goodsCateByType.get(i);
                    CommonExpandItem expandItem = new CommonExpandItem(cate.getId(), cate.getName());
                    expandItemList.add(expandItem);
                }
                return expandItemList;
            }
        }
        return null;
    }

    private OnClickListener expireDateClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            CommonExpandListPopWindow commonExpandListPopWindow = new CommonExpandListPopWindow(getContext());
            commonExpandListPopWindow.setHeight(LayoutParams.WRAP_CONTENT);
            commonExpandListPopWindow.setItemClickListener(new CommonExpandListPopWindow.OnExpandItemClickListener() {
                @Override
                public void onClickItem(CommonExpandItem commonExpandItem) {
                }
            });
            commonExpandListPopWindow.setExpandItemList(GoodsConstants.getGoodsExpireSpecList());
            commonExpandListPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                }
            });
        }
    };

    private OnCapturePicListener buildOnCaptureClickListener(int tag) {
        return new OnCapturePicListener() {
            @Override
            public void onCapturePic(Uri fileUri) {
                try {
                    List<File> fileList = Luban.with(AppManager.INSTANCE.getApplication())
                            .load(fileUri)
                            .setTargetDir(StorageHelper.getExternalShareDirPath())
                            .ignoreBy(250)
                            .get();
                    if (fileList != null && !fileList.isEmpty()) {
                        File cacheFile = fileList.get(0);
                        Context context = getContext();
                        UploadFileHelper uploadFileHelper = new UploadFileHelper((Activity) context);
                        uploadFileHelper.setUploadFileListener(new UploadFileHelper.UploadFileListener() {
                            @Override
                            public void onStart() {
                                if (context instanceof BaseActivity) {
                                    ((BaseActivity) context).showLoadingDialog();
                                }
                            }

                            @Override
                            public void onSuccess(String fileUrl) {
                                if (context instanceof BaseActivity) {
                                    ((BaseActivity) context).hideLoadingDialog();
                                }
                                if (tag == 1) {
                                    uploadItemPic1.loadGoodsPic(fileUrl);
                                } else if (tag == 2) {
                                    uploadItemPic2.loadGoodsPic(fileUrl);
                                }
                            }

                            @Override
                            public void onError(String msg) {
                                if (context instanceof BaseActivity) {
                                    ((BaseActivity) context).hideLoadingDialog();
                                }
                                CommonAlertDialogFragment.build()
                                        .setAlertTitleTxt("提示")
                                        .setAlertContentTxt("上传失败!原因: " + msg)
                                        .show(context);
                            }
                        });
                        uploadFileHelper.uploadFile(cacheFile);
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        };
    }

    public void setGoodsLayoutType(int goodsLayoutType) {
        Context context = getContext();
        layoutType = goodsLayoutType;
        if (layoutType == LAYOUT_TYPE_DETAIL_INFO) {
            //商品详情模式
            //销售规格
            stvGoodsCate.setStrokeColor(0);
            stvGoodsCate.setHint("");
            stvGoodsCate.setPadding(0, 0, 0, 0);
            ivGoodsCateArrow.setVisibility(GONE);
            stvGoodsCate.setOnClickListener(null);
            ivGoodsCateArrow.setOnClickListener(null);
            //商品名称
            setGoodsName.setEnabled(false);
            setGoodsName.setHint("");
            setGoodsName.setPadding(0, 0, 0, 0);
            setGoodsName.setStrokeColor(0);
            //条形码号
            setGoodsQrcode.setEnabled(false);
            setGoodsQrcode.setHint("");
            setGoodsQrcode.setPadding(0, 0, 0, 0);
            setGoodsQrcode.setStrokeColor(0);

            //商品图片
            uploadItemPic1.setEditMode(false);
            uploadItemPic2.setEditMode(false);



            //备注
            setGoodsComments.setEnabled(false);
            setGoodsComments.setHint("");
            setGoodsComments.setPadding(0, 0, 0, 0);
            setGoodsComments.setStrokeColor(0);
        } else if (layoutType == LAYOUT_TYPE_DETAIL_EDIT) {
            //商品详情可编辑模式
            //公用圆角属性
            int strokeColor = context.getResources().getColor(R.color.color_E8EAED);
            int padding = (int) context.getResources().getDimension(com.stkj.common.R.dimen.dp_6);
            int halfPadding = (int) context.getResources().getDimension(com.stkj.common.R.dimen.dp_3);
            //商品规格
            //商品分类
            stvGoodsCate.setStrokeColor(strokeColor);
            stvGoodsCate.setHint("请选择");
            stvGoodsCate.setPadding(padding, 0, padding, 0);
            ivGoodsCateArrow.setVisibility(VISIBLE);
            stvGoodsCate.setOnClickListener(goodsCateClick);
            ivGoodsCateArrow.setOnClickListener(goodsCateClick);
            //*菜品/套餐名称
            setGoodsName.setEnabled(true);
            setGoodsName.setHint("请输入菜品/套餐名称，限定20字以内");
            setGoodsName.setPadding(padding, 0, padding, 0);
            setGoodsName.setStrokeColor(strokeColor);
            //单价
            setGoodsQrcode.setEnabled(true);
            setGoodsQrcode.setHint("请输入单价");
            setGoodsQrcode.setPadding(padding, 0, padding, 0);
            setGoodsQrcode.setStrokeColor(strokeColor);

            //商品图片
            uploadItemPic1.setEditMode(true);
            uploadItemPic2.setEditMode(true);


            //备注
            setGoodsComments.setEnabled(true);
            setGoodsComments.setHint("请填写备注，限定100字以内");
            setGoodsComments.setPadding(padding, halfPadding, padding, halfPadding);
            setGoodsComments.setStrokeColor(strokeColor);
        } else {
            //添加商品模式
            //公用圆角属性
            int strokeColor = context.getResources().getColor(R.color.color_E8EAED);
            int padding = (int) context.getResources().getDimension(com.stkj.common.R.dimen.dp_6);
            int halfPadding = (int) context.getResources().getDimension(com.stkj.common.R.dimen.dp_3);
            //商品规格
            //商品分类
            stvGoodsCate.setStrokeColor(strokeColor);
            stvGoodsCate.setHint("请选择");
            stvGoodsCate.setPadding(padding, 0, padding, 0);
            ivGoodsCateArrow.setVisibility(VISIBLE);
            stvGoodsCate.setOnClickListener(goodsCateClick);
            ivGoodsCateArrow.setOnClickListener(goodsCateClick);
            //菜品/套餐名称
            setGoodsName.setEnabled(true);
            setGoodsName.setHint("请输入菜品/套餐名称，限定20字以内");
            setGoodsName.setPadding(padding, 0, padding, 0);
            setGoodsName.setStrokeColor(strokeColor);
            //条形码号
            setGoodsQrcode.setEnabled(true);
            setGoodsQrcode.setHint("请输入单价");
            setGoodsQrcode.setPadding(padding, 0, padding, 0);
            setGoodsQrcode.setStrokeColor(strokeColor);

            //商品图片
            uploadItemPic1.setEditMode(true);
            uploadItemPic2.setEditMode(true);






            //备注
            setGoodsComments.setEnabled(true);
            setGoodsComments.setHint("请填写备注，限定100字以内");
            setGoodsComments.setPadding(padding, halfPadding, padding, halfPadding);
            setGoodsComments.setStrokeColor(strokeColor);
        }
    }

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

    //备注
    public String getGoodsNote() {
        return setGoodsComments.getText().toString().trim();
    }

    //销售规格
    public GoodsSpec getGoodsSpec() {
        return mGoodsSpec;
    }

    //商品分类
    public GoodsCate getGoodsCate() {
        return mGoodsCate;
    }



    //上传商品图片
    public String getUploadPicUrl() {
        String uploadPicUrl1 = uploadItemPic1.getUploadPicUrl();
        String uploadPicUrl2 = uploadItemPic2.getUploadPicUrl();
        StringBuilder builder = new StringBuilder();
        if (!TextUtils.isEmpty(uploadPicUrl1)) {
            builder.append(uploadPicUrl1).append(",");
        }
        if (!TextUtils.isEmpty(uploadPicUrl2)) {
            builder.append(uploadPicUrl2);
        }
        return builder.toString();
    }

    public void loadGoodsBaseInfo(@NonNull GoodsSaleListInfo goodsBaseInfo) {
        setGoodsLayoutType(LAYOUT_TYPE_DETAIL_INFO);
        goodsType = goodsBaseInfo.getGoodsType();
        if (goodsType == GoodsConstants.TYPE_GOODS_STANDARD) {
            selectGoodsStandardType();
        } else {
            selectGoodsWeightType();
        }
        mGoodsCate.reset();
        mGoodsCate.setId(goodsBaseInfo.getGoodsCategory());
        mGoodsCate.setName(goodsBaseInfo.getGoodsCategoryStr());
        //商品分类
        stvGoodsCate.setText(goodsBaseInfo.getGoodsCategoryStr());
        //排序
        setGoodsName.setText(goodsBaseInfo.getGoodsName());
        //条形码号
        setGoodsQrcode.setText(goodsBaseInfo.getGoodsCode());
        //商品图片
        String goodsImg = goodsBaseInfo.getGoodsImg();
        String[] pics = goodsImg.split(",");
        if (pics.length > 0) {
            uploadItemPic1.loadGoodsPic(pics[0]);
            if (pics.length > 1) {
                uploadItemPic2.loadGoodsPic(pics[1]);
            }
        }

        //备注
        setGoodsComments.setText(goodsBaseInfo.getGoodsNote());
    }

    /**
     * 重置商品可编辑和清空数据
     */
    public void resetAllLayoutAndData() {
        goodsType = GoodsConstants.TYPE_GOODS_STANDARD;
        setGoodsLayoutType(LAYOUT_TYPE_ADD);
        //添加商品模式 清空输入框数据
        //商品规格
        selectGoodsStandardType();
        //商品分类
        mGoodsCate.reset();
        stvGoodsCate.setText("");
        //商品名称
        setGoodsName.setText("");
        //条形码号
        setGoodsQrcode.setText("");

        //备注
        setGoodsComments.setText("");
        //商品图片
        uploadItemPic1.resetUploadPic();
        uploadItemPic2.resetUploadPic();
//        setGoodsName.requestFocus();
//        KeyBoardUtils.showSoftKeyboard(getContext(), setGoodsName);
    }

    public void setGoodsName(String goodsName) {
        setGoodsName.setText(goodsName);
    }

    public void setGoodsCode(String goodsCode) {
        setGoodsQrcode.setText(goodsCode);
    }

    public void setGoodsPic(String goodsPic) {
        if (TextUtils.isEmpty(goodsPic)) {
            uploadItemPic1.resetUploadPic();
        } else {
            uploadItemPic1.loadGoodsPic(goodsPic);
        }
    }

    public void setGoodsComment(String comment) {
        setGoodsComments.setText(comment);
    }

    public ShapeEditText getEt_goods_canting() {
        return et_goods_canting;
    }

    public void setEt_goods_canting(ShapeEditText et_goods_canting) {
        this.et_goods_canting = et_goods_canting;
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

    public ShapeEditText getSet_goods_comments() {
        return set_goods_comments;
    }

    public ShapeEditText getSet_goods_sort() {
        return set_goods_sort;
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
