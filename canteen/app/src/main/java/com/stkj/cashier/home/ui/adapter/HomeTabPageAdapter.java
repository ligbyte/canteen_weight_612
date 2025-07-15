package com.stkj.cashier.home.ui.adapter;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.stkj.cashier.home.model.HomeMenuList;
import com.stkj.cashier.home.model.HomeTabInfo;
import com.stkj.cashier.machine.bind.TabWeightHomeFragment;
import com.stkj.cashier.machine.bind.TabWeightSettingFragment;
import com.stkj.common.ui.adapter.CommonFragmentPageAdapter;

import java.util.List;

/**
 * 首页全部界面
 */
public class HomeTabPageAdapter extends CommonFragmentPageAdapter {

    public final static String TAG = "HomeTabPageAdapter";
    public static final String TAB_BINDING_TAG = "tab_binding_tag";
    public static final String TAB_PAYMENT_TAG = "cashier";
    public static final String TAB_STAT_TAG = "stat";
    public static final String TAB_SETTING_TAG = "set";
    private List<HomeTabInfo<HomeMenuList.Menu>> homeTabInfoList;
    private FragmentActivity fragmentActivity = null;
    private TabWeightHomeFragment tabBindHomeFragment = new TabWeightHomeFragment();
    public HomeTabPageAdapter(@NonNull FragmentActivity fragmentActivity, List<HomeTabInfo<HomeMenuList.Menu>> tabInfoList) {
        super(fragmentActivity);
        homeTabInfoList = tabInfoList;
        this.fragmentActivity = fragmentActivity;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Log.d(TAG, "limeonPageSelected 50: ");
        HomeTabInfo<HomeMenuList.Menu> homeTabInfo = homeTabInfoList.get(position);
        Log.d(TAG, "limeonPageSelected 52: ");
        HomeMenuList.Menu extraInfo = homeTabInfo.getExtraInfo();
        Log.d(TAG, "limeonPageSelected 54: ");
        String path = extraInfo.getPath();
        Log.d(TAG, "limeonPageSelected 56: ");
        if (TextUtils.equals(path, TAB_BINDING_TAG)) {
            Log.d(TAG, "limeonPageSelected 58: ");
            return tabBindHomeFragment;
        } else {
            Log.d(TAG, "limeonPageSelected 60: ");
            return new TabWeightSettingFragment();
        }
    }

    public TabWeightHomeFragment getTabBindHomeFragment() {
        return tabBindHomeFragment;
    }

    @Override
    public int getItemCount() {
        return homeTabInfoList == null ? 0 : homeTabInfoList.size();
    }

    public int findTabPageIndex(String path) {
        if (homeTabInfoList != null) {
            int size = homeTabInfoList.size();
            for (int i = 0; i < size; i++) {
                HomeMenuList.Menu extraInfo = homeTabInfoList.get(i).getExtraInfo();
                if (TextUtils.equals(path, extraInfo.getPath())) {
                    return i;
                }
            }
        }
        return -1;
    }
}
