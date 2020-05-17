package com.nst.jiazheng.login;

import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;

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

import butterknife.BindView;

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

    @Override
    protected void init() {
        if (SpUtil.getBoolean("isLogin", false)) {
            overlay(HomeActivity.class);
            finish();
        }
        regist.setOnClickListener(v -> {
            overlay(RegistActivity.class);
        });
        forgotpwd.setOnClickListener(v -> {
            overlay(ForgotPwdActivity.class);
        });
        login.setOnClickListener(v -> {
            login();
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
        regist.setEnabled(false);
        OkGo.<String>post(Api.registerApi).params("api_name", "login")
                .params("mobile", userName)
                .params("password", passWord).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                regist.setEnabled(true);
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
                regist.setEnabled(true);
                toast(response.body());
            }
        });
    }
}
