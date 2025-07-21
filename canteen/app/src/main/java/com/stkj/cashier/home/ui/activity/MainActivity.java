package com.stkj.cashier.home.ui.activity;

import static com.youxin.myseriallib.base.Constants.ReadDeviceType.READ_DEVICE3;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Rect;
import android.graphics.Shader;
import android.hardware.usb.UsbDevice;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.stkj.cashier.BuildConfig;
import com.stkj.cashier.MainApplication;
import com.stkj.cashier.R;
import com.stkj.cashier.base.callback.AppNetCallback;
import com.stkj.cashier.base.device.DeviceManager;
import com.stkj.cashier.base.net.AppNetManager;
import com.stkj.cashier.base.tts.TTSVoiceHelper;
import com.stkj.cashier.base.ui.dialog.CommonAlertDialogFragment;
import com.stkj.cashier.base.ui.dialog.CommonInputDialogFragment;
import com.stkj.cashier.base.utils.PriceUtils;
import com.stkj.cashier.consumer.ConsumerManager;
import com.stkj.cashier.consumer.callback.ConsumerListener;
import com.stkj.cashier.home.callback.OnGetStoreInfoListener;
import com.stkj.cashier.home.helper.CBGCameraHelper;
import com.stkj.cashier.home.helper.HeartBeatHelper;
import com.stkj.cashier.home.helper.ScreenProtectHelper;
import com.stkj.cashier.home.helper.SystemEventWatcherHelper;
import com.stkj.cashier.home.helper.WarningTipsHelper;
import com.stkj.cashier.home.model.HomeMenuList;
import com.stkj.cashier.home.model.HomeTabInfo;
import com.stkj.cashier.home.model.StoreInfo;
import com.stkj.cashier.home.ui.adapter.HomeTabPageAdapter;
import com.stkj.cashier.home.ui.widget.BindingHomeTitleLayout;
import com.stkj.cashier.home.ui.widget.HomeTitleLayout;
import com.stkj.cashier.home.ui.widget.WarningTipsView;
import com.stkj.cashier.machine.utils.LedCtrlUtil;
import com.stkj.cashier.pay.helper.ConsumerModeHelper;
import com.stkj.cashier.pay.model.BindFragmentBackEvent;
import com.stkj.cashier.pay.model.BindFragmentSwitchEvent;
import com.stkj.cashier.pay.model.RefreshBindModeEvent;
import com.stkj.cashier.pay.model.TTSSpeakEvent;
import com.stkj.cashier.setting.data.DeviceSettingMMKV;
import com.stkj.cashier.setting.data.ServerSettingMMKV;
import com.stkj.cashier.setting.helper.AppUpgradeHelper;
import com.stkj.cashier.setting.helper.StoreInfoHelper;
import com.stkj.common.core.AppManager;
import com.stkj.common.core.CountDownHelper;
import com.stkj.common.log.LogHelper;
import com.stkj.common.ui.activity.BaseActivity;
import com.stkj.common.ui.toast.AppToast;
import com.stkj.common.utils.ActivityUtils;
import com.stkj.common.utils.AndroidUtils;
import com.stkj.common.utils.FileUtils;
import com.stkj.common.utils.KeyBoardUtils;
import com.stkj.deviceinterface.UsbDeviceHelper;
import com.stkj.deviceinterface.callback.UsbDeviceListener;
import com.youxin.myseriallib.base.Constants;
import com.youxin.myseriallib.bean.DeviceDataCallBlack;
import com.youxin.myseriallib.bean.DeviceInitCallBlack;
import com.youxin.myseriallib.bean.DeviceStatusListener;
import com.youxin.myseriallib.bean.ReadCardResulBean;
import com.youxin.myseriallib.serialDevices.YxDevicePortCtrl;
import com.youxin.myseriallib.serialDevices.YxDeviceSDK;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.rxjava3.schedulers.Schedulers;
import static com.youxin.myseriallib.deviceIoCtrl.LedCtrlApi.LED_ALARM;
import static com.youxin.myseriallib.deviceIoCtrl.LedCtrlApi.LED_BLUE_TYPE;
import static com.youxin.myseriallib.deviceIoCtrl.LedCtrlApi.LED_GB_TYPE;
import static com.youxin.myseriallib.deviceIoCtrl.LedCtrlApi.LED_GREEN_TYPE;
import static com.youxin.myseriallib.deviceIoCtrl.LedCtrlApi.LED_RB_TYPE;
import static com.youxin.myseriallib.deviceIoCtrl.LedCtrlApi.LED_RED_TYPE;
import static com.youxin.myseriallib.deviceIoCtrl.LedCtrlApi.LED_RG_TYPE;
import static com.youxin.myseriallib.deviceIoCtrl.LedCtrlApi.LED_WHITE_TYPE;



public class MainActivity extends BaseActivity implements AppNetCallback, ConsumerListener , DeviceDataCallBlack<ReadCardResulBean>, DeviceStatusListener {

    public final static String TAG = "MainActivity";
    //当前TAB界面
    private static final String TAB_CURRENT_PAGE = "currentTabPage";
    private View scanHolderView;
    private ViewPager2 vp2Content;
    private FrameLayout flScreenWelcom;
    private TextView tv_food_name;
    private TextView tv_price_flag;
    private TextView tv_price;
    private TextView tv_price_unit;
    private TextView tv_account_info;
    private FrameLayout fl_screen_success;
    private WarningTipsView warning_tips_view;
    private HomeTabPageAdapter homeTabPageAdapter;
    private static BindingHomeTitleLayout htlConsumer;
    //是否需要重新恢复消费者页面
    private boolean needRestartConsumer;
    //是否初始化了菜单数据
    private boolean hasInitMenuData;
    private boolean isSoftKeyboardShow;
    private int saveStateCurrentTabPage;
    private long lastBackClickTime = 0;
    private CBGCameraHelper cbgCameraHelper;
    private YxDeviceSDK yxDeviceSDK;
    private YxDevicePortCtrl yxDevicePortCtrl;
    private String currentTrayNo;
    private int lossCount;
    private int totalCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

//        Log.d(TAG, "limeMD5Utils: " + MD5Utils.encrypt("ly0379"));

//        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
//
//        Runnable task = new Runnable() {
//            private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//            @Override
//            public void run() {
//                String currentTime = sdf.format(new Date());
//            }
//        };
//        scheduler.scheduleAtFixedRate(task, 0, 60, TimeUnit.SECONDS);

        //异步处理人脸识别照片缓存目录
        Schedulers.io().scheduleDirect(new Runnable() {
            @Override
            public void run() {
                try {
//                    Log.d(TAG, "limeFilePath onCreate: " + FileUtils.getFaceCachePath());
//                    FileUtils.putKeyFaceCachePathsValue("");
//                    FileUtils.createDir(new File(FileUtils.getFaceCachePathParent() + "20250201"));
//                    FileUtils.createDir(new File(FileUtils.getFaceCachePathParent() + "20250202"));
//                    FileUtils.createDir(new File(FileUtils.getFaceCachePathParent() + "20250203"));
//                    FileUtils.createDir(new File(FileUtils.getFaceCachePathParent() + "20250204"));

                    FileUtils.createDir(new File(FileUtils.getFaceCachePath()));
                    FileUtils.clearFaceCache(FileUtils.getKeyFaceCachePathsValue());
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });



        // 避免从桌面启动程序后，会重新实例化入口类的activity
        // 判断当前activity是不是所在任务栈的根
        Intent intent = getIntent();
        if (intent != null) {
            String action = intent.getAction();
            //1.避免从桌面启动程序后，会重新实例化入口类的activity , 判断当前activity是不是所在任务栈的根
            if (!isTaskRoot()) {
                if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(action)) {
                    finish();
                    return;
                }
            }
            //2.经过路由跳转的，判断当前应用是否已经初始化过，首页是否存在并且未销毁
            if (Intent.ACTION_VIEW.equals(action)) {
                Activity homeActivity = AppManager.INSTANCE.getMainActivity();
                if (!ActivityUtils.isActivityFinished(homeActivity)) {
                    finish();
                    return;
                }
            }
        }
        AppManager.INSTANCE.setMainActivity(this);
        readSaveInstanceState(savedInstanceState);
        setContentView(com.stkj.cashier.R.layout.activity_main);

        findViews();
        initApp();
        LogHelper.print("-main--getDisplayMetrics--" + getResources().getDisplayMetrics());

    }

    private void openYxDeviceSDK() {
        yxDeviceSDK = new YxDeviceSDK();
        if (yxDeviceSDK != null && (yxDevicePortCtrl == null || !yxDevicePortCtrl.isOpen())) {
            Constants.ReadDeviceType readDeviceType = READ_DEVICE3;
            String readCardSerialPath = "/dev/ttyS8";
            Log.d(TAG, "limecode readCardSerialPath: " + readCardSerialPath);
            if (TextUtils.isEmpty(readCardSerialPath)) {
                yxDevicePortCtrl = yxDeviceSDK.openReadCardDevice(readDeviceType, this, this);
            }else{
                yxDevicePortCtrl = yxDeviceSDK.openReadCardDevice(readDeviceType, readCardSerialPath, this, this);
            }
        }
    }

    @Override
    public int getContentPlaceHolderId() {
        return R.id.fl_main_content;
    }

    @Override
    protected void onPause() {
        super.onPause();
        //关闭屏幕常亮
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }

    @Override
    protected void onResume() {
        super.onResume();
        //打开屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (needRestartConsumer) {
            needRestartConsumer = false;
        }
//        EventBus.getDefault().post(new FindViewResumeEvent());
//        try {
//
//            if (cbgCameraHelper!= null){
//                cbgCameraHelper.getCameraHelper().onResume();
//            }
//
//        } catch (Throwable e) {
//            e.printStackTrace();
//        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    /**
     * 初始化app
     */
    private void initApp() {

        flScreenWelcom.setVisibility(View.VISIBLE);
        initData();


    }

    private void initYxSDK() {
        try {
            Log.e("settingTAG", "isInit SN: " + DeviceManager.INSTANCE.getDeviceInterface().getMachineNumber());
            YxDeviceSDK.InitSDK(this, DeviceManager.INSTANCE.getDeviceInterface().getMachineNumber(), new DeviceInitCallBlack() {
                @Override
                public void initStatus(boolean isInit, String message) {
                    Log.d("settingTAG", "isInit : " + isInit + "   mesg : " + message);
                    flScreenWelcom.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            openYxDeviceSDK();
                        }
                    },1 * 1000);
                }
            });
        }catch (Exception e){
            Log.e("settingTAG", "isInit : " + e.getMessage());
        }
    }


    /**
     * 清理焦点
     */
    public void clearMainFocus() {
        //清理焦点信息
        View currentFocus = getCurrentFocus();
        if (currentFocus != null) {
            currentFocus.clearFocus();
        }
        if (DeviceManager.INSTANCE.getDeviceInterface().isCanDispatchKeyEvent()) {
            scanHolderView.requestFocus();
        }
    }

    private void findViews() {
        scanHolderView = findViewById(R.id.scan_holder_view);
        htlConsumer = (BindingHomeTitleLayout) findViewById(R.id.htl_consumer);
        flScreenWelcom = findViewById(R.id.fl_screen_welcom);
        tv_food_name = findViewById(R.id.tv_food_name);
        tv_price_flag = findViewById(R.id.tv_price_flag);
        warning_tips_view = findViewById(R.id.warning_tips_view);
        tv_price = findViewById(R.id.tv_price);
        tv_price_unit = findViewById(R.id.tv_price_unit);
        fl_screen_success = findViewById(R.id.fl_screen_success);
        tv_account_info =  findViewById(R.id.tv_account_info);
        setTextViewStyles(tv_account_info);
        vp2Content = findViewById(R.id.vp2_content);
        initTvDefault();
        tv_food_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tv_food_name.getText().toString().equals("暂未选择菜品")){
                    initTvUnit();
                }else {
                    fl_screen_success.setVisibility(View.VISIBLE);
                    ledLightShow(LED_GREEN_TYPE);
                }
            }
        });
    }

    private void initTvDefault() {
        tv_food_name.setText("暂未选择菜品");
        tv_price.setText("--");
        AppToast.toastMsg("limeopenLed 341");
        ledLightShow(LED_RED_TYPE);
        tv_food_name.setTextColor(Color.parseColor("#FF2C2C"));
        warning_tips_view.setVisibility(View.VISIBLE);
        tv_price_flag.setVisibility(View.GONE);
        tv_price_unit.setVisibility(View.GONE);
        warning_tips_view.postDelayed(new Runnable() {
            @Override
            public void run() {
                warning_tips_view.delayHideTipsView();
                getWeakRefHolder(TTSVoiceHelper.class).speakByTTSVoice("请先选择菜品");
            }
        }, 6 * 1000);

    }

    private void initTvUnit() {
        tv_food_name.setText("小炒黄牛肉");
        tv_price.setText(PriceUtils.formatPrice(99));
        tv_food_name.setTextColor(Color.parseColor("#FFFFFF"));
        warning_tips_view.setVisibility(View.GONE);
        tv_price_flag.setVisibility(View.VISIBLE);
        tv_price_unit.setVisibility(View.VISIBLE);
        ledLightShow(LED_WHITE_TYPE);
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        LogHelper.print("---MainActivity--dispatchKeyEvent--activity event: " + event);
        if (isSoftKeyboardShow && DeviceManager.INSTANCE.getDeviceInterface().isFinishDispatchKeyEvent()) {
            return super.dispatchKeyEvent(event);
        }
        //判断扫码枪是否连接
        if (DeviceManager.INSTANCE.getDeviceInterface().isCanDispatchKeyEvent()) {
            if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                KeyBoardUtils.hideSoftKeyboard(this, scanHolderView);
            } else {
                if (!scanHolderView.hasFocus()) {
                    scanHolderView.requestFocus();
                }
            }
            DeviceManager.INSTANCE.getDeviceInterface().dispatchKeyEvent(event);
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN) {
            ScreenProtectHelper screenProtectHelper = getWeakRefHolder(ScreenProtectHelper.class);
            screenProtectHelper.stopScreenProtect();
        } else if (action == MotionEvent.ACTION_UP) {
            ScreenProtectHelper screenProtectHelper = getWeakRefHolder(ScreenProtectHelper.class);
            screenProtectHelper.startScreenProtect();
        }
        return super.dispatchTouchEvent(event);
    }

    private void initData() {

        flScreenWelcom.postDelayed(new Runnable() {
            @Override
            public void run() {
                initYxSDK();
            }
        },60 * 1000);
        initHomeContent();
        initMinuteAlarm();
    }

    @Override
    public void onNetInitSuccess() {
        hideLoadingDialog();
        Log.d(TAG, "limeonNetInitSuccess: " + 387);
    }

    @Override
    public void onNetInitError(String message) {
        hideLoadingDialog();
        showAppNetInitErrorDialog(message);
    }

    /**
     * 展示 app 初始化失败弹窗
     */
    private void showAppNetInitErrorDialog(String errorMsg) {
        CommonAlertDialogFragment commonAlertDialogFragment = CommonAlertDialogFragment.build()
                .setAlertTitleTxt("提示")
                .setAlertContentTxt("初始化失败,错误原因:\n" + errorMsg)
                .setLeftNavTxt("重试")
                .setLeftNavClickListener(new CommonAlertDialogFragment.OnSweetClickListener() {
                    @Override
                    public void onClick(CommonAlertDialogFragment alertDialogFragment) {
                        showLoadingDialog();
                        AppNetManager.INSTANCE.initAppNet();
                    }
                });
        if (BuildConfig.DEBUG) {
            commonAlertDialogFragment.setRightNavTxt("切换服务器")
                    .setRightNavClickListener(new CommonAlertDialogFragment.OnSweetClickListener() {
                        @Override
                        public void onClick(CommonAlertDialogFragment alertDialogFragment) {
                            showInputServerAddressDialog();
                        }
                    })
                    .show(MainActivity.this);
        } else {
            commonAlertDialogFragment.setRightNavTxt("关闭App")
                    .setRightNavClickListener(new CommonAlertDialogFragment.OnSweetClickListener() {
                        @Override
                        public void onClick(CommonAlertDialogFragment alertDialogFragment) {
                            DeviceManager.INSTANCE.getDeviceInterface().release();
                            AndroidUtils.killApp(MainActivity.this);
                        }
                    })
                    .show(MainActivity.this);
        }
    }

    /**
     * 显示修改服务器地址
     */
    private void showInputServerAddressDialog() {
        CommonInputDialogFragment.build()
                .setTitle("修改服务器地址")
                .setOnInputListener(new CommonInputDialogFragment.OnInputListener() {
                    @Override
                    public void onInputEnd(String input) {
                        ServerSettingMMKV.handleChangeServerAddress(MainActivity.this, input);
                    }
                }).show(this);
    }



    /**
     * 加载主页内容
     */
    private void initHomeContent() {
        //添加左侧tab列表
        List<HomeTabInfo<HomeMenuList.Menu>> homeTabInfoList = new ArrayList<>();
        for (int i = 0; i < 1; i++) {
            if (i == 0) {
                //设置
                HomeTabInfo<HomeMenuList.Menu> paymentTabInfo = new HomeTabInfo<>();
                paymentTabInfo.setExtraInfo(new HomeMenuList.Menu(HomeTabPageAdapter.TAB_SETTING_TAG, "设置"));
                homeTabInfoList.add(paymentTabInfo);

            } else {

            }
        }


        //添加右侧内容页面
        homeTabPageAdapter = new HomeTabPageAdapter(this, homeTabInfoList);
        //禁止viewPager左右滑动切换tab页
        vp2Content.setUserInputEnabled(false);
        vp2Content.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                Log.d(TAG, "limeonPageSelected position: " + position);
//                ConsumerModeHelper consumerModeHelper = new ConsumerModeHelper(MainActivity.this);
//                int currentConsumerMode = consumerModeHelper.getCurrentConsumerMode();
//                if(currentConsumerMode == PayConstants.CONSUMER_GOODS_MODE){
                    homeTabPageAdapter.createFragment(position);
//                }
            }
        });
        vp2Content.setAdapter(homeTabPageAdapter);
        vp2Content.setOffscreenPageLimit(2);
        vp2Content.setCurrentItem(0, false);
        //每秒回调helper
        CountDownHelper countDownHelper = getWeakRefHolder(CountDownHelper.class);
        countDownHelper.startCountDown();
        //开始心跳设置
        HeartBeatHelper heartBeatHelper = getWeakRefHolder(HeartBeatHelper.class);
        heartBeatHelper.requestHeartBeat();
        countDownHelper.addCountDownListener(heartBeatHelper);
        //请求设备信息
        StoreInfoHelper storeInfoHelper = getWeakRefHolder(StoreInfoHelper.class);
        storeInfoHelper.requestStoreInfo();
        storeInfoHelper.addGetStoreInfoListener(new OnGetStoreInfoListener() {
            @Override
            public void onGetStoreInfo(StoreInfo storeInfo) {
                htlConsumer.getTv_canteen_name().setText(storeInfo.getDeviceName());
            }
        });
        //获取餐厅时段信息
        ConsumerModeHelper consumerModeHelper = getWeakRefHolder(ConsumerModeHelper.class);
        consumerModeHelper.requestCanteenCurrentTimeInfo();
        countDownHelper.addCountDownListener(consumerModeHelper);
        //初始化语音
        TTSVoiceHelper ttsVoiceHelper = getWeakRefHolder(TTSVoiceHelper.class);
        ttsVoiceHelper.initTTSVoice(null);

        //网络状态回调
        SystemEventWatcherHelper systemEventWatcherHelper = getWeakRefHolder(SystemEventWatcherHelper.class);
        countDownHelper.addCountDownListener(systemEventWatcherHelper);

        //启动检查升级
        AppUpgradeHelper appUpgradeHelper = getWeakRefHolder(AppUpgradeHelper.class);
        appUpgradeHelper.checkAppVersion();
        hasInitMenuData = true;
    }

    @Override
    protected void onSaveInstanceState(@androidx.annotation.NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(TAB_CURRENT_PAGE, vp2Content.getCurrentItem());
    }

    private void readSaveInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            saveStateCurrentTabPage = savedInstanceState.getInt(TAB_CURRENT_PAGE, 0);
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {

        if (vp2Content.getCurrentItem() == 0) {

            long currentTime = System.currentTimeMillis();
            if ((currentTime - lastBackClickTime) > 2000) {
                AppToast.toastMsg("再按一次退出程序");
                lastBackClickTime = currentTime;
            } else {
                //杀掉进程
                DeviceManager.INSTANCE.getDeviceInterface().release();
                AndroidUtils.killApp(this);
            }
        }else {
            EventBus.getDefault().post(new BindFragmentBackEvent(vp2Content.getCurrentItem()));
        }
    }

    @Override
    protected void onDestroy() {
        AppManager.INSTANCE.clearMainActivity();
        EventBus.getDefault().unregister(this);
        LedCtrlUtil.getInstance().closeLedThread();
        super.onDestroy();
    }

    @Override
    public void onCreateFacePreviewView(SurfaceView previewView, SurfaceView irPreview) {

    }

    @Override
    public void onConsumerDismiss() {
        needRestartConsumer = true;
        ConsumerManager.INSTANCE.clearConsumerPresentation();
        //清理相机相关引用,释放相机
        CBGCameraHelper cbgCameraHelper = getWeakRefHolder(CBGCameraHelper.class);
        cbgCameraHelper.releaseCameraHelper();
        clearWeakRefHolder(CBGCameraHelper.class);
    }


    /**
     * 字体颜色渐变
     * @param textView
     */
    private void setTextViewStyles(TextView textView) {
        float x1=textView.getPaint().measureText(textView.getText().toString());
        float y1=textView.getPaint().getTextSize();
        int c1=Color.parseColor("#307EFE");
        int c2= Color.parseColor("#70DDFF");
        LinearGradient topToBottomLG = new LinearGradient(0, 0, 0, y1,c1, c2, Shader.TileMode.CLAMP);
        textView.getPaint().setShader(topToBottomLG);
        textView.invalidate();
    }

    @Override
    public void onCreateTitleLayout(HomeTitleLayout homeTitleLayout) {
        //系统事件监听
        SystemEventWatcherHelper systemEventWatcherHelper = getWeakRefHolder(SystemEventWatcherHelper.class);
        if (systemEventWatcherHelper != null) {
            systemEventWatcherHelper.addSystemEventListener(homeTitleLayout);
        }
        //添加设备信息更新回调
        StoreInfoHelper storeInfoHelper = getWeakRefHolder(StoreInfoHelper.class);
        if (storeInfoHelper != null) {
            storeInfoHelper.addGetStoreInfoListener(homeTitleLayout);
        }
    }

    @Override
    public void addContentPlaceHolderFragment(Fragment fragment) {
        super.addContentPlaceHolderFragment(fragment);
    }

    public Fragment getCurrentTabFragment() {
        if (vp2Content != null && homeTabPageAdapter != null) {
            int currentItem = vp2Content.getCurrentItem();
            return homeTabPageAdapter.findPageFragment(this, currentItem);
        }
        return null;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBindFragmentSwitchEvent(BindFragmentSwitchEvent eventBus) {
        Log.d(TAG, "limeonBindFragmentSwitchEvent 700 eventBus: " + eventBus.getPosition());
        vp2Content.setCurrentItem(eventBus.getPosition(), false);
        if (eventBus.getPosition() == 0){
            MainApplication.barcode = "";
            htlConsumer.setVisibility(View.VISIBLE);
            flScreenWelcom.setVisibility(View.VISIBLE);
            flScreenWelcom.postDelayed(new Runnable() {
                @Override
                public void run() {
                    openYxDeviceSDK();
                }
            },1 * 1000);
            vp2Content.setVisibility(View.GONE);
            fl_screen_success.setVisibility(View.GONE);
            ledLightShow(LED_WHITE_TYPE);
        }else {
            if (yxDevicePortCtrl != null && yxDevicePortCtrl.isOpen()){
                yxDevicePortCtrl.closeDevice();
            }
            htlConsumer.setVisibility(View.GONE);
            flScreenWelcom.setVisibility(View.GONE);
            fl_screen_success.setVisibility(View.GONE);
            vp2Content.setVisibility(View.VISIBLE);
            ledLightShow(LED_BLUE_TYPE);
        }
    }

    private AlarmManager mAlarmManager;
    private PendingIntent mPendingIntent;

    private void initMinuteAlarm() {
        mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, MinuteReceiver.class);
        mPendingIntent = PendingIntent.getBroadcast(
                this, 0, intent, PendingIntent.FLAG_IMMUTABLE
        );

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.MINUTE, 1);

        mAlarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                60 * 1000,
                mPendingIntent
        );
    }

    public static class MinuteReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            htlConsumer.onDateChange();
        }
    }





    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTTSSpeakEvent(TTSSpeakEvent eventBus) {
        Log.d(TAG, "limeonTTSSpeakEvent: " + 832);
        if (!TextUtils.isEmpty(eventBus.getContent())){
            Log.d(TAG, "limeonTTSSpeakEvent: " + 834);
            Activity mainActivity = AppManager.INSTANCE.getMainActivity();
            if (mainActivity instanceof BaseActivity) {
                Log.d(TAG, "limeonTTSSpeakEvent: " + 837);
                BaseActivity baseActivity = (BaseActivity) mainActivity;
                baseActivity.getWeakRefHolder(TTSVoiceHelper.class).speakByTTSVoice(eventBus.getContent());
            }
        }
    }

    @Override
    public void onDataResult(ReadCardResulBean resulBean) {
        boolean trakCardStatus = resulBean.isTrakCardStatus();
        if (trakCardStatus){

            final String message = resulBean.getTrakCardNo();

            if (!TextUtils.equals(currentTrayNo, message) && !TextUtils.isEmpty(message)){
                currentTrayNo = message;
                lossCount = 0;
                totalCount = 0;
            }

            if (!TextUtils.isEmpty(currentTrayNo)){
                if (!TextUtils.isEmpty(message)) {
                    totalCount++;
                }else{
                    lossCount++;
                }
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    String line = "limeCardResult : card : "+ message;
                    Log.d(TAG, "limeCardResult : card : "+ message);
                    if (yxDevicePortCtrl != null && yxDevicePortCtrl.isOpen()){
                        yxDevicePortCtrl.closeDevice();
                    }
                    if (vp2Content.getCurrentItem() == 0){
                        MainApplication.barcode = message;
                        flScreenWelcom.setVisibility(View.GONE);
                        flScreenWelcom.postDelayed(new Runnable() {
                            @Override
                            public void run() {
//                                ConsumerManager.INSTANCE.showConsumer(MainActivity.this,homeTabPageAdapter.getTabBindHomeFragment() , MainActivity.this);
//                                homeTabPageAdapter.getTabBindHomeFragment().findViews();
//                                homeTabPageAdapter.getTabBindHomeFragment().onRefreshBindModeEvent(new RefreshBindModeEvent(0));
                            }
                        },100);
                    }

                }
            });

        }else{

            byte icCardType = resulBean.getIcCardType();

            final String message = resulBean.getIcCardNo();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    String line = "CardResult :   IC card : "+ message;

//                    addLogLine(line);
                }
            });

        }
    }

    @Override
    public void onReceiveTimeOut() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String line = "CardResult :  Receive TimeOut ";
                //addLogLine(line);
            }
        });
    }

    @Override
    public void onConnectSuccess() {

    }

    @Override
    public void onConnectFail(String s) {

    }

    @Override
    public void onClose() {

    }


    private void ledLightShow(final int ledType){
        Log.d(TAG, "limeopenLed 817 ledType: " + ledType);
        if (DeviceSettingMMKV.isOpenWarning()) {
            LedCtrlUtil.getInstance().openLed(ledType);
        }
    }
}