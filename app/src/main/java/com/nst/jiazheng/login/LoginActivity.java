package com.nst.jiazheng.login;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.nst.jiazheng.R;
import com.nst.jiazheng.api.Api;
import com.nst.jiazheng.api.resp.Register;
import com.nst.jiazheng.api.resp.Resp;
import com.nst.jiazheng.base.BaseActivity;
import com.nst.jiazheng.base.Layout;
import com.nst.jiazheng.base.SpUtil;
import com.nst.jiazheng.user.HomeActivity;
import com.nst.jiazheng.api.WechatEvent;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;

import androidx.annotation.Nullable;
import butterknife.BindView;

import static com.nst.jiazheng.api.Api.APP_ID;

/**
 * 创建者     彭龙
 * 创建时间   2020/4/1 2:29 PM
 * 描述
 * <p>
 * 更新者     $
 * 更新时间   $
 * 更新描述
 */
@Layout(layoutId = R.layout.activity_login)
public class LoginActivity extends BaseActivity {
    @BindView(R.id.regist)
    TextView regist;
    @BindView(R.id.forgotpwd)
    TextView forgotpwd;
    @BindView(R.id.login)
    TextView login;
    @BindView(R.id.username)
    EditText username;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.logo)
    ImageView logo;
    @BindView(R.id.wxLogin)
    ImageView wxLogin;
    private IWXAPI api;

    @Override
    protected void init() {
        Api.goingToLogin = false;
        if (SpUtil.getBoolean("isLogin", false)) {
            overlay(HomeActivity.class);
            finish();
        }
        regist.setOnClickListener(v -> overlay(RegistActivity.class));
        forgotpwd.setOnClickListener(v -> overlay(ForgotPwdActivity.class));
        login.setOnClickListener(v -> login());
        wxLogin.setOnClickListener(view -> wxLogin());
        setLogo();
    }

    private void wxLogin() {
        api = WXAPIFactory.createWXAPI(this, APP_ID, true);
        api.registerApp(APP_ID);
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "wechat_login";
        api.sendReq(req);
    }

    private void setLogo() {
        OkGo.<String>post(Api.publicApi).params("api_name", "logo").execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                Resp<Register> resp = new Gson().fromJson(response.body(), new TypeToken<Resp<Register>>() {
                }.getType());
                if (resp.code == 1) {
                    try {
                        Glide.with(LoginActivity.this).load(resp.data.logo).centerCrop().error(R.mipmap.ic_logo).into(logo);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void login() {
        String userName = username.getText().toString().trim();
        if (TextUtils.isEmpty(userName) || userName.length() != 11) {
            toast("请输入正确的手机号码");
            return;
        }
        String passWord = password.getText().toString().trim();
        if (TextUtils.isEmpty(passWord)) {
            toast("请输入密码");
            return;
        }
        showDialog("正在登陆", true);
        OkGo.<String>post(Api.registerApi).params("api_name", "login")
                .params("mobile", userName)
                .params("password", passWord).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                dismissDialog();
                Resp<Register> resp = new Gson().fromJson(response.body(), new TypeToken<Resp<Register>>() {
                }.getType());
                toast(resp.msg);
                if (resp.code == 1) {
                    SpUtil.putBoolean("isLogin", true);
                    SpUtil.saveObj("userInfo", resp.data);
                    overlay(HomeActivity.class);
                    finish();
                }
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                dismissDialog();
                toast(response.body());
            }
        });
    }

    @Override
    protected void onStart() {
        EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onWechatEvent(WechatEvent ev) {
        switch (ev.state) {
            case "wechat_login":
                sendCodeToServer(ev.code);
                break;
        }
    }

    private void sendCodeToServer(String code) {
        showDialog("授权验证中", true);
        OkGo.<String>post(Api.registerApi).params("api_name", "wxLogin")
                .params("wx_code", code).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                dismissDialog();
                Log.e("123", response.body());
                Resp<Register> resp = new Gson().fromJson(response.body(), new TypeToken<Resp<Register>>() {
                }.getType());
                toast(resp.msg);
                if (resp.code == 1) {
                    SpUtil.putBoolean("isLogin", true);
                    SpUtil.saveObj("userInfo", resp.data);
                    overlay(HomeActivity.class);
                    finish();
                } else if (resp.code == 2) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("code", resp.data);
                    overlayForResult(WxLoginActivity.class, 1, bundle);
                }
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                dismissDialog();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Register data1 = (Register) data.getSerializableExtra("data");
            SpUtil.putBoolean("isLogin", true);
            SpUtil.saveObj("userInfo", data1);
            overlay(HomeActivity.class);
            finish();
        }
    }
}
