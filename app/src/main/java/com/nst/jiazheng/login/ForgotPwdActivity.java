package com.nst.jiazheng.login;

import android.os.CountDownTimer;
import android.text.TextUtils;
import android.widget.Button;
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
@Layout(layoutId = R.layout.activity_forgotpwd)
public class ForgotPwdActivity extends BaseActivity {
    @BindView(R.id.sendcode)
    TextView sendcode;
    @BindView(R.id.yhxy)
    TextView agreement;
    @BindView(R.id.username)
    EditText username;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.smscode)
    EditText smscode;
    @BindView(R.id.submit)
    Button submit;
    @Override
    protected void init() {

        sendcode.setOnClickListener(v -> {
            sendCode();
        });
        agreement.setOnClickListener(v -> {
            overlay(AgreementActivity.class);
        });
        submit.setOnClickListener(v -> {
            regist();
        });
    }

    private void regist() {
        String userName = username.getText().toString().trim();
        if (TextUtils.isEmpty(userName) || userName.length() != 11) {
            toast("请输入正确的手机号码");
            return;
        }
        String smsCode = smscode.getText().toString().trim();
        if (TextUtils.isEmpty(smsCode)) {
            toast("请输入验证码");
            return;
        }
        String passWord = password.getText().toString().trim();
        if (TextUtils.isEmpty(passWord)) {
            toast("请输入密码");
            return;
        }
        submit.setEnabled(false);
        OkGo.<String>post(Api.registerApi).params("api_name", "forgetPassword")
                .params("mobile", userName)
                .params("password", passWord)
                .params("code", smsCode).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                submit.setEnabled(true);
                Resp<Register> resp = new Gson().fromJson(response.body(), new TypeToken<Resp<Register>>() {
                }.getType());
                toast(resp.msg);
                if (resp.code == 1) {
                    finish();
                }
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                submit.setEnabled(true);
                toast(response.body());
            }
        });
    }

    private void sendCode() {
        String mobile = username.getText().toString().trim();
        if (TextUtils.isEmpty(mobile) || mobile.length() != 11) {
            toast("请输入正确的手机号码");
            return;
        }
        sendcode.setEnabled(false);
        new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long l) {
                sendcode.setText(l / 1000 + "秒");
            }

            @Override
            public void onFinish() {
                sendcode.setText("获取验证码");
                sendcode.setEnabled(true);
            }
        }.start();
        OkGo.<String>post(Api.baseApi).params("api_name", "sendCode").params("mobile", mobile).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                Resp resp = new Gson().fromJson(response.body(), Resp.class);
                toast(resp.msg);
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                toast(response.body());
            }
        });
    }
}
