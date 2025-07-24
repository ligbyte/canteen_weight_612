package com.stkj.plate.weight.pay.ui.weight;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.stkj.plate.weight.base.greendao.AppGreenDaoOpenHelper;
import com.stkj.plate.weight.base.greendao.GreenDBConstants;
import com.stkj.plate.weight.base.greendao.generate.DaoMaster;
import com.stkj.plate.weight.base.greendao.generate.DaoSession;
import com.stkj.plate.weight.base.greendao.generate.FoodInfoTableDao;
import com.stkj.plate.weight.pay.goods.callback.GoodsAutoSearchListener;
import com.stkj.plate.weight.pay.ui.adapter.GoodsAutoSearchInfoViewHolder;
import com.stkj.plate.weight.pay.ui.fragment.GoodsConsumerFragment;
import com.stkj.plate.weight.setting.model.FoodInfoTable;
import com.stkj.common.core.AppManager;
import com.stkj.common.ui.adapter.CommonRecyclerAdapter;
import com.stkj.common.ui.adapter.holder.placeholder.PlaceModel;
import com.stkj.common.ui.adapter.holder.placeholder.PlaceRecyclerViewHolder;
import com.stkj.common.utils.KeyBoardUtils;
import com.stkj.plate.weight.R;
import com.stkj.plate.weight.base.ui.widget.CommonActionDoneEditText;

import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

public class GoodsAutoSearchLayout extends FrameLayout {
    private RecyclerView rvSearchContent;
    private CommonActionDoneEditText etGoodsSearch;
    private CommonRecyclerAdapter commonRecyclerAdapter;
    private LifecycleOwner lifecycleOwner;
    private GoodsAutoSearchListener goodsAutoSearchListener;
    private String mLastSearchKey;
    private ImageView ivClearSearch;
    private FoodInfoTableDao foodInfoTableDao;
    private DaoSession daoSession;

    public GoodsAutoSearchLayout(@NonNull Context context) {
        super(context);
        init(context);
    }

    public GoodsAutoSearchLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GoodsAutoSearchLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.include_goods_auto_search, this);
        ivClearSearch = (ImageView) findViewById(R.id.iv_clear_search);
        ivClearSearch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                etGoodsSearch.setText("");
            }
        });
        etGoodsSearch = (CommonActionDoneEditText) findViewById(R.id.et_goods_search);
        etGoodsSearch.setOnEditWatchListener(new CommonActionDoneEditText.OnEditWatchListener() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s == null || s.length() == 0) {
                    ivClearSearch.setVisibility(View.GONE);
                } else {
                    ivClearSearch.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAfterTextChanged(String input) {
                if (TextUtils.isEmpty(input)) {
                    mLastSearchKey = "";
                    rvSearchContent.setVisibility(GONE);
                } else {
                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            autoSearch(input, false);
                        }
                    }, 100);
                }
            }

            @Override
            public void onActionDone() {
                KeyBoardUtils.hideSoftKeyboard(getContext(), etGoodsSearch);
            }

            @Override
            public void onFocusChange(boolean hasFocus) {
                if (hasFocus) {
                    String currentTxt = etGoodsSearch.getText().toString();
                    if (!TextUtils.isEmpty(currentTxt)) {
                        rvSearchContent.setVisibility(VISIBLE);
                    } else {
                        rvSearchContent.setVisibility(GONE);
                    }
                } else {
                    // 此处为失去焦点时的处理内容
//                    hideSearchGoodsList();
                }
            }
        });
        rvSearchContent = (RecyclerView) findViewById(R.id.rv_search_content);
        commonRecyclerAdapter = new CommonRecyclerAdapter(false);
        commonRecyclerAdapter.addViewHolderFactory(new GoodsAutoSearchInfoViewHolder.Factory());
        commonRecyclerAdapter.addViewHolderFactory(new PlaceRecyclerViewHolder.Factory(R.layout.item_goods_auto_search_empty));
        commonRecyclerAdapter.addItemEventListener(new CommonRecyclerAdapter.OnItemEventListener() {
            @Override
            public void onClickItemView(View view, Object obj) {
                if (obj instanceof FoodInfoTable) {
                    if (goodsAutoSearchListener != null) {
                        goodsAutoSearchListener.onStartGetGoodsItemDetail((FoodInfoTable) obj);
                    }
                    KeyBoardUtils.hideSoftKeyboard(getContext(), etGoodsSearch);
                    hideSearchGoodsList();
                } else if (obj instanceof PlaceModel) {
                    hideSearchGoodsList();
                }
            }
        });
        rvSearchContent.setAdapter(commonRecyclerAdapter);
    }

    public void setGoodsAutoSearchListener(LifecycleOwner lifecycleOwner, GoodsAutoSearchListener autoSearchItemListener) {
        this.lifecycleOwner = lifecycleOwner;
        this.goodsAutoSearchListener = autoSearchItemListener;
    }

    /**
     * @param key                搜索 key
     * @param autoGetGoodsDetail 自动调用获取商品详细信息
     */
    public void autoSearch(String key, boolean autoGetGoodsDetail) {
        if (lifecycleOwner == null) {
            return;
        }
        if (TextUtils.isEmpty(key)) {
            return;
        }
        //已经在搜索了
        if (!autoGetGoodsDetail && TextUtils.equals(key, mLastSearchKey)) {
            return;
        }
        //注释掉扫码枪扫商品条码不填充数据
//        if (autoGetGoodsDetail) {
//            etGoodsSearch.setText(key);
//        }
        mLastSearchKey = key;
        if (foodInfoTableDao == null){
            initFoodInfoTableDao();
        }
        foodInfoTableDao.detachAll();
        QueryBuilder<FoodInfoTable> qbCount   = foodInfoTableDao.queryBuilder();
        qbCount.where(FoodInfoTableDao.Properties.Status.eq(1));
        qbCount.where(FoodInfoTableDao.Properties.DeleteFlag.eq("NOT_DELETE"));

        if (!TextUtils.isEmpty(GoodsConsumerFragment.queryPricingMethod)) {
            qbCount.where(FoodInfoTableDao.Properties.PricingMethod.eq(GoodsConsumerFragment.queryPricingMethod));
        }

        if (!TextUtils.isEmpty(GoodsConsumerFragment.queryTab)) {
            qbCount.where(FoodInfoTableDao.Properties.CategoryMap.like("%" + GoodsConsumerFragment.queryTab + "%"));
        }

        if (!TextUtils.isEmpty(key)) {
            qbCount.where(FoodInfoTableDao.Properties.Name.like("%" + key + "%"));
        }
        qbCount.limit(20);
        List<FoodInfoTable>  foods   =  qbCount.list();
        showSearchGoodsList(foods);
    }

    private void initFoodInfoTableDao() {
        AppGreenDaoOpenHelper daoOpenHelper = new AppGreenDaoOpenHelper(AppManager.INSTANCE.getApplication(), GreenDBConstants.FACE_DB_NAME, null);
        Database database = daoOpenHelper.getWritableDb();
        DaoMaster daoMaster = new DaoMaster(database);
        daoSession = daoMaster.newSession();
        foodInfoTableDao = daoSession.getFoodInfoTableDao();
    }


    private void showSearchGoodsList(List<FoodInfoTable> dataList) {
        rvSearchContent.setVisibility(VISIBLE);
        commonRecyclerAdapter.removeAllData();
        if (dataList == null || dataList.isEmpty()) {
            commonRecyclerAdapter.addData(new PlaceModel(R.layout.item_goods_auto_search_empty, true));
        } else {
            commonRecyclerAdapter.addDataList(dataList);
        }
    }

    public CommonActionDoneEditText getEtGoodsSearch() {
        return etGoodsSearch;
    }

    public void hideSearchGoodsList() {
        rvSearchContent.setVisibility(GONE);
        etGoodsSearch.clearFocus();
    }

    public void clearSearchKey() {
        etGoodsSearch.setText("");
        etGoodsSearch.clearFocus();
        rvSearchContent.setVisibility(GONE);
    }

}
