package com.stkj.cashier.machine.bind;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jakewharton.rxbinding4.view.RxView;
import com.stkj.cashier.BuildConfig;
import com.stkj.cashier.R;
import com.stkj.cashier.base.device.DeviceManager;
import com.stkj.cashier.base.net.AppNetManager;
import com.stkj.cashier.base.ui.dialog.CommonBindAlertDialogFragment;
import com.stkj.cashier.base.ui.dialog.CommonBindSignleAlertDialogFragment;
import com.stkj.cashier.base.ui.dialog.FacePassSettingBindAlertFragment;
import com.stkj.cashier.base.utils.CommonDialogUtils;
import com.stkj.cashier.machine.adapter.SettingBindTabInfoViewHolder;
import com.stkj.cashier.machine.model.SettingBindTabInfo;
import com.stkj.cashier.pay.model.BindFragmentBackEvent;
import com.stkj.cashier.pay.model.BindFragmentSwitchEvent;
import com.stkj.cashier.pay.ui.weight.GridSpacingItemDecoration;
import com.stkj.cashier.setting.callback.FacePassSettingCallback;
import com.stkj.cashier.setting.helper.AppUpgradeHelper;
import com.stkj.cashier.setting.model.FacePassPeopleInfo;
import com.stkj.cashier.setting.model.PauseFacePassDetect;
import com.stkj.common.rx.AutoDisposeUtils;
import com.stkj.common.rx.DefaultObserver;
import com.stkj.common.ui.adapter.CommonRecyclerAdapter;
import com.stkj.common.ui.fragment.BaseDialogFragment;
import com.stkj.common.ui.fragment.BaseRecyclerFragment;
import com.stkj.common.utils.AndroidUtils;
import com.stkj.common.utils.SpanUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import kotlin.Unit;

/**
 * 设置页面
 */
public class TabBindSettingFragment extends BaseRecyclerFragment implements View.OnClickListener, FacePassSettingCallback, AppUpgradeHelper.OnAppUpgradeListener {

    public final static String TAG = "TabBindSettingFragment";
    private RecyclerView rvTopTab;
    private ImageView iv_back;
    private LinearLayout ll_app_settings;
    private LinearLayout ll_app_face;
    private TextView tv_title_name;
    private RelativeLayout rl_server_addr;
    private RelativeLayout rl_version;
    private RelativeLayout rl_restart_app;
    private RelativeLayout rl_face_value;
    private RelativeLayout rl_face_sync;
    private TextView tv_face_count;
    private TextView tv_app_version;
    private TextView tv_server_addr;
    private BaseDialogFragment dialogFragment;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_tab_bind_setting;
    }

    @Override
    protected void initViews(View rootView) {
        rvTopTab = (RecyclerView) findViewById(R.id.rv_top_tab);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        ll_app_settings = (LinearLayout) findViewById(R.id.ll_app_settings);
        ll_app_face = (LinearLayout) findViewById(R.id.ll_app_face);
        tv_title_name = (TextView) findViewById(R.id.tv_title_name);
        tv_face_count = (TextView) findViewById(R.id.tv_face_count);
        rl_server_addr = (RelativeLayout) findViewById(R.id.rl_server_addr);
        rl_version = (RelativeLayout) findViewById(R.id.rl_version);
        rl_restart_app = (RelativeLayout) findViewById(R.id.rl_restart_app);
        rl_face_value = (RelativeLayout) findViewById(R.id.rl_face_value);
        rl_face_sync = (RelativeLayout) findViewById(R.id.rl_face_sync);
        tv_app_version = (TextView) findViewById(R.id.tv_app_version);
        tv_server_addr = (TextView) findViewById(R.id.tv_server_addr);
        if (BuildConfig.DEBUG) {
            tv_server_addr.setText(AppNetManager.API_TEST_URL);
        } else {
            tv_server_addr.setText(AppNetManager.API_OFFICIAL_URL);
        }
        RxView.clicks(rl_version)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .to(AutoDisposeUtils.onDestroyDispose(this))
                .subscribe(new DefaultObserver<Unit>() {
                    @Override
                    protected void onSuccess(Unit unit) {
                        AppUpgradeHelper appUpgradeHelper = mActivity.getWeakRefHolder(AppUpgradeHelper.class);
                        appUpgradeHelper.setOnAppUpgradeListener(TabBindSettingFragment.this);
                        appUpgradeHelper.checkAppVersion();
                    }
                });
        rl_server_addr.setOnClickListener(this);
        rl_version.setOnClickListener(this);
        rl_restart_app.setOnClickListener(this);
        rl_face_value.setOnClickListener(this);
        rl_face_sync.setOnClickListener(this);
        tv_app_version.setText("慧餐宝v" + BuildConfig.VERSION_NAME);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackEvent();
            }
        });
    }

    public void onBackEvent(){

        if (dialogFragment != null && dialogFragment.isVisible()){
            dialogFragment.dismiss();
            return ;
        }

        if (ll_app_settings.getVisibility() == View.VISIBLE || ll_app_face.getVisibility() == View.VISIBLE){
            ll_app_face.setVisibility(View.GONE);
            ll_app_settings.setVisibility(View.GONE);
            rvTopTab.setVisibility(View.VISIBLE);
            tv_title_name.setText("后台管理");
        }else {
            EventBus.getDefault().post(new BindFragmentSwitchEvent(0));
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBindFragmentBackEvent(BindFragmentBackEvent eventBus) {
        onBackEvent();
    }


    @Override
    protected void onFragmentResume(boolean isFirstOnResume) {
        if (isFirstOnResume) {
            EventBus.getDefault().register(this);
            initData();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }







    /**
     * 统一处理删除人脸库成功和失败
     */
    private void handleDeleteAllFacePass(boolean needRequestAllFace) {
        refreshFacePassTotalCount(0);
    }




    private void initData() {
        Log.d(TAG, "limeTabBindSettingFragment: " + 48);
        //添加设置tab
        List<SettingBindTabInfo> settingTabInfoList = new ArrayList<>();
        settingTabInfoList.add(new SettingBindTabInfo(SettingBindTabInfo.TAB_NAME_SERVER_ADDRESS, SettingBindTabInfo.TAB_TYPE_SERVER_ADDRESS,R.mipmap.ic_bind_settings_face));
        settingTabInfoList.add(new SettingBindTabInfo(SettingBindTabInfo.TAB_NAME_DEVICE_SETTING, SettingBindTabInfo.TAB_TYPE_DEVICE_SETTING,R.mipmap.ic_bind_settings_id));
        settingTabInfoList.add(new SettingBindTabInfo(SettingBindTabInfo.TAB_NAME_WIFI_CONNECT, SettingBindTabInfo.TAB_TYPE_WIFI_CONNECT,R.mipmap.ic_bind_settings));
        settingTabInfoList.add(new SettingBindTabInfo(SettingBindTabInfo.TAB_NAME_PAYMENT_SETTING, SettingBindTabInfo.TAB_TYPE_PAYMENT_SETTING,R.mipmap.ic_bind_settings_canting));
        CommonRecyclerAdapter tabInfoAdapter = new CommonRecyclerAdapter(false);
        tabInfoAdapter.addViewHolderFactory(new SettingBindTabInfoViewHolder.Factory());

        tabInfoAdapter.addDataList(settingTabInfoList);
        rvTopTab.setAdapter(tabInfoAdapter);
        rvTopTab.setItemAnimator(null);

        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), settingTabInfoList.size());
        rvTopTab.setLayoutManager(layoutManager);
        rvTopTab.addItemDecoration(new GridSpacingItemDecoration(settingTabInfoList.size(), 40, false));

        tabInfoAdapter.addItemEventListener(new CommonRecyclerAdapter.OnItemEventListener() {
            @Override
            public void onClickItemView(View view, Object obj) {
                SettingBindTabInfo settingTabInfo = (SettingBindTabInfo) obj;

                if (settingTabInfo.getTabName().equals(SettingBindTabInfo.TAB_NAME_DEVICE_SETTING)){
                    CommonDialogUtils.showTipsBindDialog(getActivity(), "设备ID",DeviceManager.INSTANCE.getDeviceInterface().getMachineNumber(), "取消", new CommonBindSignleAlertDialogFragment.OnSweetClickListener() {
                        @Override
                        public void onClick(CommonBindSignleAlertDialogFragment alertDialogFragment) {

                        }
                    });
                } else if (settingTabInfo.getTabName().equals(SettingBindTabInfo.TAB_NAME_PAYMENT_SETTING)){
                    CommonDialogUtils.showTipsBindDialog(getActivity(), "用餐设置","敬请期待", "取消", new CommonBindSignleAlertDialogFragment.OnSweetClickListener() {
                        @Override
                        public void onClick(CommonBindSignleAlertDialogFragment alertDialogFragment) {

                        }
                    });
                }else if (settingTabInfo.getTabName().equals(SettingBindTabInfo.TAB_NAME_WIFI_CONNECT)){
                    ll_app_settings.setVisibility(View.VISIBLE);
                    rvTopTab.setVisibility(View.GONE);
                    tv_title_name.setText("应用设置");
                }else if (settingTabInfo.getTabName().equals(SettingBindTabInfo.TAB_NAME_SERVER_ADDRESS)){
                    ll_app_face.setVisibility(View.VISIBLE);
                    rvTopTab.setVisibility(View.GONE);
                    tv_title_name.setText("人脸管理");
                }

            }
        });


    }


    private void refreshFacePassTotalCount(long count) {
        SpanUtils.with(tv_face_count)
                .append(String.valueOf(count))
                .setForegroundColor(mResources.getColor(R.color.color_3489F5))
                .append("人已入库")
                .create();
    }

    private void deleteAllFacePass() {
    }

    @Override
    public void needUpdateFacePass() {

    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.rl_server_addr){

        }else if (v.getId() == R.id.rl_version){

        }else if (v.getId() == R.id.rl_restart_app){
            dialogFragment = CommonBindAlertDialogFragment.build()
                    .setAlertTitleTxt("提示")
                    .setAlertContentTxt("确定重启应用?")
                    .setLeftNavTxt("确认重启")
                    .setLeftNavClickListener(new CommonBindAlertDialogFragment.OnSweetClickListener() {
                        @Override
                        public void onClick(CommonBindAlertDialogFragment alertDialogFragment) {
                            DeviceManager.INSTANCE.getDeviceInterface().release();
                            AndroidUtils.restartApp();
                        }
                    }).setRightNavTxt("取消");
            dialogFragment.show(mActivity);
        }else if (v.getId() == R.id.rl_face_value){
            //暂停人脸识别功能
            EventBus.getDefault().post(new PauseFacePassDetect());
            dialogFragment = FacePassSettingBindAlertFragment.build();
            dialogFragment.show(mActivity);
        }else if (v.getId() == R.id.rl_face_sync){
            dialogFragment = CommonBindAlertDialogFragment.build()
                    .setAlertTitleTxt("提示")
                    .setAlertContentTxt("确定清空人脸数据库吗")
                    .setLeftNavTxt("确定")
                    .setLeftNavClickListener(new CommonBindAlertDialogFragment.OnSweetClickListener() {
                        @Override
                        public void onClick(CommonBindAlertDialogFragment alertDialogFragment) {
                            deleteAllFacePass();
                        }
                    })
                    .setRightNavTxt("取消");
            dialogFragment.show(mActivity);
        }


    }


    @Override
    public void onCheckVersionEnd(String msg) {

    }

    @Override
    public void onCheckVersionStart() {

    }

    @Override
    public void onCheckVersionError(String msg) {

    }

    @Override
    public void onNoVersionUpgrade() {

    }
}
