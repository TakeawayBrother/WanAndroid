package com.business.user.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.business.user.R;
import com.business.user.bean.UserBean;
import com.business.user.utils.AntiHijackingUtils;
import com.business.user.viewmodel.LoginViewModel;
import com.gs.keyboard.KeyboardType;
import com.gs.keyboard.SecurityConfigure;
import com.gs.keyboard.SecurityEditText;
import com.gs.keyboard.SecurityKeyboard;
import com.sgitg.common.base.BaseActivity;
import com.sgitg.common.event.EventCenter;
import com.sgitg.common.event.EventCode;
import com.sgitg.common.http.RestResult;
import com.sgitg.common.thread.MainThreadExcute;
import com.sgitg.common.thread.ThreadManager;
import com.sgitg.common.utils.KeyboardUtils;
import com.sgitg.common.utils.StringUtils;
import com.sgitg.common.utils.ToastUtils;
import com.sgitg.common.viewmodel.LViewModelProviders;
import com.yanzhenjie.sofia.Sofia;

import org.greenrobot.eventbus.EventBus;

/**
 * 描述：
 *
 * @author 周麟
 * @date 2019/1/12/012 21:38
 */
@Route(path = "/User/AccountLoginActivity")
public class AccountLoginActivity extends BaseActivity {
    private LoginViewModel mLoginViewModel;
    private EditText mAccount;
    private SecurityEditText mM;

    @Override
    protected int setLayoutResourceID() {
        return R.layout.activity_account_login;
    }

    @Override
    protected ViewModel initViewModel() {
        mLoginViewModel = LViewModelProviders.of(this, LoginViewModel.class);
        mLoginViewModel.getLoginResult().observe(AccountLoginActivity.this, new Observer<RestResult<UserBean>>() {
            @Override
            public void onChanged(@Nullable RestResult<UserBean> restResult) {
                if (restResult != null) {
                    if (checkHttpResult(restResult)) {
                        successLogin(restResult.getData());
                    } else {
                        ToastUtils.getInstance().showErrorInfoToast(restResult.getErrorMsg());
                    }
                }

            }
        });
        return mLoginViewModel;
    }

    @Override
    protected void setUpView() {
        super.setUpView();
        Sofia.with(this).statusBarDarkFont().statusBarBackgroundAlpha(0).invasionStatusBar();
        mAccount = findViewById(R.id.account);
        mM = findViewById(R.id.mm);
        Button mBtLogin = findViewById(R.id.bt_login);
        RelativeLayout mRootView = findViewById(R.id.root);
        SecurityConfigure configure = new SecurityConfigure().setDefaultKeyboardType(KeyboardType.NUMBER);
        new SecurityKeyboard(mRootView, configure);
        mBtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                KeyboardUtils.hideKeyboard(AccountLoginActivity.this);
                if (StringUtils.isNullOrEmpty(mAccount.getText().toString())) {
                    ToastUtils.getInstance().showErrorInfoToast("账号不能为空！");
                    return;
                }
                if (StringUtils.isNullOrEmpty(mM.getText().toString())) {
                    ToastUtils.getInstance().showErrorInfoToast("密码不能为空！");
                    return;
                }
                if (mM.getText().toString().contains(" ")) {
                    ToastUtils.getInstance().showErrorInfoToast("密码格式非法！");
                    return;
                }
                mLoginViewModel.login(mAccount.getText().toString(), mM.getText().toString());
            }
        });

        findViewById(R.id.to_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readyGo(RegisterActivity.class);
            }
        });
    }

    private void successLogin(final UserBean user) {
        ThreadManager.getLongPool().execute(new Runnable() {
            @Override
            public void run() {
                user.save();
                MainThreadExcute.post(new Runnable() {
                    @Override
                    public void run() {
                        EventBus.getDefault().post(new EventCenter<>(EventCode.EVENT_LOGIN_SUCCESS, user.getUsername()));
                        finish();
                    }
                });
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        ThreadManager.getLongPool().execute(new Runnable() {
            @Override
            public void run() {
                // 白名单
                boolean safe = AntiHijackingUtils.checkActivity(getApplicationContext());
                // 系统桌面
                boolean isHome = AntiHijackingUtils.isHome(getApplicationContext());
                // 锁屏操作
                boolean isReflectScreen = AntiHijackingUtils.isReflectScreen(getApplicationContext());
                // 判断程序是否当前显示
                if (!safe && !isHome && !isReflectScreen) {
                    Looper.prepare();
                    MainThreadExcute.post(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtils.getInstance().showErrorInfoToast("WanAndroid被切换至后台，请您确认使用环境是否安全！");
                        }
                    });
                    Looper.loop();
                }
            }
        });
    }

    @Override
    protected boolean isBindEventBusHere() {
        return true;
    }

    @Override
    protected void onEventComming(EventCenter eventCenter) {
        if (eventCenter.getEventCode() == EventCode.EVENT_REGISTER_SUCCESS) {
            finish();
        }
    }
}
