package com.stkj.cashier.base.ui.dialog;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.stkj.cashier.BuildConfig;
import com.stkj.cashier.R;
import com.stkj.cashier.base.device.DeviceManager;
import com.stkj.cashier.base.ui.widget.CommonSeekProgressBar;
import com.stkj.cashier.base.utils.CommonDialogUtils;
import com.stkj.cashier.setting.callback.FacePassSettingCallback;
import com.stkj.cashier.setting.model.ResumeFacePassDetect;
import com.stkj.common.log.LogHelper;
import com.stkj.common.ui.fragment.BaseDialogFragment;
import com.stkj.common.ui.fragment.BaseRecyclerFragment;
import com.stkj.common.ui.toast.AppToast;
import com.stkj.common.ui.widget.shapeselectlayout.ShapeSelectTextView;
import com.stkj.common.utils.AndroidUtils;
import com.stkj.common.utils.FragmentUtils;

import org.greenrobot.eventbus.EventBus;

/**
 * 人脸识别设置弹窗
 */
public class FacePassSettingBindAlertFragment extends BaseDialogFragment {
    private LinearLayout llAddFaceSetting;
    private TextView tvAddFaceSetting;
    private ImageView ivClose;
    private TextView tvLiveness;
    private TextView tvDualCamera;
    private ImageView ivSwitchDualCamera;
    private ImageView ivSwitchLiveness;
    private ImageView ivSwitchOcclusionMode;
    private ImageView ivSwitchGaThreshold;
    private CommonSeekProgressBar seekbarSearchThreshold;
    private CommonSeekProgressBar seekbarGaThreshold;
    private CommonSeekProgressBar seekbarLivenessThreshold;
    private CommonSeekProgressBar seekbarDetectFaceMinThreshold;
    private TextView tvDetectFaceMinThresholdTips;
    private CommonSeekProgressBar seekbarAddFaceMinThreshold;
    private CommonSeekProgressBar seekbarResultSearchScoreThreshold;
    private CommonSeekProgressBar seekbarPoseThreshold;
    private FacePassSettingCallback facePassSettingCallback;
    private ShapeSelectTextView stvDetectMinThreshold50;
    private ShapeSelectTextView stvDetectMinThreshold80;
    private ShapeSelectTextView stvDetectMinThreshold100;


    public void setFacePassSettingCallback(FacePassSettingCallback facePassSettingCallback) {
        this.facePassSettingCallback = facePassSettingCallback;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_facepass_setting_alert_dialog_bind;
    }

    @Override
    protected void initViews(View rootView) {
        llAddFaceSetting = (LinearLayout) findViewById(R.id.ll_add_face_setting);
        tvAddFaceSetting = (TextView) findViewById(R.id.tv_add_face_setting);
        if (BuildConfig.DEBUG) {
            llAddFaceSetting.setVisibility(View.VISIBLE);
            tvAddFaceSetting.setVisibility(View.VISIBLE);
        } else {
            llAddFaceSetting.setVisibility(View.GONE);
            tvAddFaceSetting.setVisibility(View.GONE);
        }
        ivClose = (ImageView) findViewById(R.id.iv_close);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //恢复人脸识别功能
                EventBus.getDefault().post(new ResumeFacePassDetect());
                FragmentUtils.safeRemoveFragment(getParentFragmentManager(), FacePassSettingBindAlertFragment.this);
            }
        });
        ivSwitchLiveness = (ImageView) findViewById(R.id.iv_switch_liveness);
        ivSwitchLiveness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean selected = !ivSwitchLiveness.isSelected();
                ivSwitchLiveness.setSelected(selected);
                updateFacePassConfig();
            }
        });
        tvLiveness = (TextView) findViewById(R.id.tv_liveness);
        tvDualCamera = (TextView) findViewById(R.id.tv_dual_camera);
        ivSwitchDualCamera = (ImageView) findViewById(R.id.iv_switch_dual_camera);
        boolean supportDualCamera = DeviceManager.INSTANCE.getDeviceInterface().isSupportDualCamera();
        if (supportDualCamera) {
            tvDualCamera.setVisibility(View.VISIBLE);
            ivSwitchDualCamera.setVisibility(View.VISIBLE);
            ivSwitchDualCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommonAlertDialogFragment.build()
                            .setAlertTitleTxt("提示")
                            .setAlertContentTxt("打开/关闭双目检测需要重启App")
                            .setLeftNavTxt("确认重启")
                            .setLeftNavClickListener(new CommonAlertDialogFragment.OnSweetClickListener() {
                                @Override
                                public void onClick(CommonAlertDialogFragment alertDialogFragment) {
                                    boolean selected = !ivSwitchDualCamera.isSelected();
                                    ivSwitchLiveness.setSelected(selected);
                                    DeviceManager.INSTANCE.getDeviceInterface().release();
                                    AndroidUtils.restartApp();
                                }
                            }).setRightNavTxt("取消")
                            .show(mActivity);
                }
            });

        } else {
            tvDualCamera.setVisibility(View.GONE);
            ivSwitchDualCamera.setVisibility(View.GONE);
        }
        ivSwitchOcclusionMode = (ImageView) findViewById(R.id.iv_switch_occlusionMode);
        ivSwitchOcclusionMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean selected = !ivSwitchOcclusionMode.isSelected();
                ivSwitchOcclusionMode.setSelected(selected);
                updateFacePassConfig();
            }
        });
        ivSwitchGaThreshold = (ImageView) findViewById(R.id.iv_switch_GaThreshold);
        ivSwitchGaThreshold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean selected = !ivSwitchGaThreshold.isSelected();
                ivSwitchGaThreshold.setSelected(selected);
                updateFacePassConfig();
            }
        });
        seekbarSearchThreshold = (CommonSeekProgressBar) findViewById(R.id.seekbar_searchThreshold);
        seekbarSearchThreshold.setSeekProgressListener(new CommonSeekProgressBar.OnCommonSeekProgressListener() {
            @Override
            public void onProgressFinished(int progress) {
                updateFacePassConfig();
            }
        });
        seekbarGaThreshold = (CommonSeekProgressBar) findViewById(R.id.seekbar_GaThreshold);
        seekbarGaThreshold.setSeekProgressListener(new CommonSeekProgressBar.OnCommonSeekProgressListener() {
            @Override
            public void onProgressFinished(int progress) {
                updateFacePassConfig();
            }
        });
        seekbarLivenessThreshold = (CommonSeekProgressBar) findViewById(R.id.seekbar_livenessThreshold);
        seekbarLivenessThreshold.setSeekProgressListener(new CommonSeekProgressBar.OnCommonSeekProgressListener() {
            @Override
            public void onProgressFinished(int progress) {
                updateFacePassConfig();
            }
        });
        //角度识别阈值
        seekbarPoseThreshold = (CommonSeekProgressBar) findViewById(R.id.seekbar_poseThreshold);
        seekbarPoseThreshold.setSeekProgressListener(new CommonSeekProgressBar.OnCommonSeekProgressListener() {
            @Override
            public void onProgressFinished(int progress) {
                updateFacePassConfig();
            }
        });
        tvDetectFaceMinThresholdTips = (TextView) findViewById(R.id.tv_detect_faceMinThreshold_tips);
        stvDetectMinThreshold50 = (ShapeSelectTextView) findViewById(R.id.stv_detect_MinThreshold50);
        stvDetectMinThreshold80 = (ShapeSelectTextView) findViewById(R.id.stv_detect_MinThreshold80);
        stvDetectMinThreshold100 = (ShapeSelectTextView) findViewById(R.id.stv_detect_MinThreshold100);
        stvDetectMinThreshold50.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unSelectAllFaceMinThreshold();
                stvDetectMinThreshold50.setShapeSelect(true);
                int cm50FaceMinThreshold = DeviceManager.INSTANCE.getDeviceInterface().get50cmDetectFaceMinThreshold();
                seekbarDetectFaceMinThreshold.setSeekProgress(cm50FaceMinThreshold);
                //转换成旷视sdk的识别距离设置
                int maxProgress = seekbarDetectFaceMinThreshold.getMaxProgress();
                int cbgFaceMinThreshold = maxProgress - cm50FaceMinThreshold;
                updateFacePassConfig();
            }
        });
        stvDetectMinThreshold80.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unSelectAllFaceMinThreshold();
                stvDetectMinThreshold80.setShapeSelect(true);
                int cm80FaceMinThreshold = DeviceManager.INSTANCE.getDeviceInterface().get80cmDetectFaceMinThreshold();
                seekbarDetectFaceMinThreshold.setSeekProgress(cm80FaceMinThreshold);
                //转换成旷视sdk的识别距离设置
                int maxProgress = seekbarDetectFaceMinThreshold.getMaxProgress();
                int cbgFaceMinThreshold = maxProgress - cm80FaceMinThreshold;
                updateFacePassConfig();
            }
        });
        stvDetectMinThreshold100 = (ShapeSelectTextView) findViewById(R.id.stv_detect_MinThreshold100);
        stvDetectMinThreshold100.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unSelectAllFaceMinThreshold();
                stvDetectMinThreshold100.setShapeSelect(true);
                int cm100FaceMinThreshold = DeviceManager.INSTANCE.getDeviceInterface().get100cmDetectFaceMinThreshold();
                seekbarDetectFaceMinThreshold.setSeekProgress(cm100FaceMinThreshold);
                //转换成旷视sdk的识别距离设置
                int maxProgress = seekbarDetectFaceMinThreshold.getMaxProgress();
                int cbgFaceMinThreshold = maxProgress - cm100FaceMinThreshold;
                updateFacePassConfig();
            }
        });
        //识别距离阈值
        seekbarDetectFaceMinThreshold = (CommonSeekProgressBar) findViewById(R.id.seekbar_detect_faceMinThreshold);
        seekbarDetectFaceMinThreshold.setSeekProgressListener(new CommonSeekProgressBar.OnCommonSeekProgressListener() {
            @Override
            public void onProgressFinished(int progress) {
                unSelectAllFaceMinThreshold();
                //转换成旷视sdk的识别距离设置
                int maxProgress = seekbarDetectFaceMinThreshold.getMaxProgress();
                int faceMinThreshold = maxProgress - progress;
                LogHelper.print("---seekbarFaceMinThreshold----new faceMinThreshold: " + faceMinThreshold);
                updateFacePassConfig();
            }
        });
        //人脸入库阈值
        seekbarAddFaceMinThreshold = (CommonSeekProgressBar) findViewById(R.id.seekbar_add_faceMinThreshold);
        seekbarAddFaceMinThreshold.setSeekProgressListener(new CommonSeekProgressBar.OnCommonSeekProgressListener() {
            @Override
            public void onProgressFinished(int progress) {
                LogHelper.print("---seekbarFaceMinThreshold----new add faceMinThreshold: " + progress);
                updateAddFacePassConfig();
                showNeedUpdateFacePass();
            }
        });
        //人脸识别结果分数阈值
        seekbarResultSearchScoreThreshold = (CommonSeekProgressBar) findViewById(R.id.seekbar_resultSearchScoreThreshold);
        seekbarResultSearchScoreThreshold.setSeekProgressListener(new CommonSeekProgressBar.OnCommonSeekProgressListener() {
            @Override
            public void onProgressFinished(int progress) {

            }
        });


    }

    /**
     * 显示需要更新人脸库弹窗
     */
    private void showNeedUpdateFacePass() {
        CommonAlertDialogFragment.build()
                .setAlertTitleTxt("提示")
                .setAlertContentTxt("修改此值需要更新人脸库,确认更新?")
                .setLeftNavTxt("现在更新")
                .setLeftNavClickListener(new CommonAlertDialogFragment.OnSweetClickListener() {
                    @Override
                    public void onClick(CommonAlertDialogFragment alertDialogFragment) {
                        //恢复人脸识别功能
                        EventBus.getDefault().post(new ResumeFacePassDetect());
                        FragmentUtils.safeRemoveFragment(getParentFragmentManager(), FacePassSettingBindAlertFragment.this);
                        if (facePassSettingCallback != null) {
                            facePassSettingCallback.needUpdateFacePass();
                        }
                    }
                })
                .setRightNavTxt("稍后更新")
                .show(mActivity);
    }

    private void unSelectAllFaceMinThreshold() {
        stvDetectMinThreshold50.setShapeSelect(false);
        stvDetectMinThreshold80.setShapeSelect(false);
        stvDetectMinThreshold100.setShapeSelect(false);
    }

    /**
     * 更新人脸识别设置
     */
    private void updateFacePassConfig() {

    }

    /**
     * 更新人脸入库设置
     */
    private void updateAddFacePassConfig() {

    }


    private static volatile FacePassSettingBindAlertFragment instance;

    public static FacePassSettingBindAlertFragment build() {
        if (instance == null) {
            synchronized (FacePassSettingBindAlertFragment.class) {
                if (instance == null) {
                    instance = new FacePassSettingBindAlertFragment();
                }
            }
        }
        return instance;
    }
}
