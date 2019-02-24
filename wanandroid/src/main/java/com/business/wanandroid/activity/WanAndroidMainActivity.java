package com.business.wanandroid.activity;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.business.wanandroid.Constants;
import com.business.wanandroid.R;
import com.business.wanandroid.fragment.HomeFragment;
import com.business.wanandroid.fragment.MineFragment;
import com.business.wanandroid.fragment.ProjectTabFragment;
import com.business.wanandroid.fragment.SystemCategoryFragment;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;
import com.sgitg.common.base.AbstractDoubleClickOutActivity;

/**
 * 描述：
 *
 * @author 周麟
 * @date 2019/2/10/010 22:11
 */
@Route(path = "/WanAndroid/MainActivity")
public class WanAndroidMainActivity extends AbstractDoubleClickOutActivity {
    private HomeFragment mHomeFragment;
    private ProjectTabFragment mProjectFragment;
    private SystemCategoryFragment mSystemFragment;
    private MineFragment mMineFragment;

    @Override
    protected int setLayoutResourceID() {
        return R.layout.activity_main;
    }

    @Override
    protected void setUpView() {
        super.setUpView();
        BottomBar mBottomBar = findViewById(R.id.bottomBar);
        mBottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(int tabId) {
                switchFragment(tabId);
            }
        });
        findViewById(R.id.search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readyGo(SearchActivity.class);
            }
        });
    }

    private void switchFragment(int tabId) {
        FragmentManager mFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        hideFragment(fragmentTransaction);
        if (tabId == R.id.tab_home) {
            if (mHomeFragment == null) {
                mHomeFragment = new HomeFragment();
                fragmentTransaction.add(R.id.container, mHomeFragment, Constants.HOME_TAG);
            } else {
                fragmentTransaction.show(mHomeFragment);
            }

        } else if (tabId == R.id.tab_project) {
            if (mProjectFragment == null) {
                mProjectFragment = new ProjectTabFragment();
                fragmentTransaction.add(R.id.container, mProjectFragment, Constants.PROJECT_TAG);
            } else {
                fragmentTransaction.show(mProjectFragment);
            }

        } else if (tabId == R.id.tab_system) {
            if (mSystemFragment == null) {
                mSystemFragment = new SystemCategoryFragment();
                fragmentTransaction.add(R.id.container, mSystemFragment, Constants.SYSTEM_TAG);
            } else {
                fragmentTransaction.show(mSystemFragment);
            }

        } else if (tabId == R.id.tab_mine) {
            if (mMineFragment == null) {
                mMineFragment = new MineFragment();
                fragmentTransaction.add(R.id.container, mMineFragment, Constants.MINE_TAG);
            } else {
                fragmentTransaction.show(mMineFragment);
            }
        }
        fragmentTransaction.commit();
    }


    private void hideFragment(FragmentTransaction fragmentTransaction) {
        if (mHomeFragment != null) {
            fragmentTransaction.hide(mHomeFragment);
        }

        if (mProjectFragment != null) {
            fragmentTransaction.hide(mProjectFragment);
        }
        if (mSystemFragment != null) {
            fragmentTransaction.hide(mSystemFragment);
        }

        if (mMineFragment != null) {
            fragmentTransaction.hide(mMineFragment);
        }
    }
}
