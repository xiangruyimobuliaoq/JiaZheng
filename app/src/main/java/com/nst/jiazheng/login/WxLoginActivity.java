package com.nst.jiazheng.login;

import android.content.Intent;
import android.os.CountDownTimer;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
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

import java.io.Serializable;

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
@Layout(layoutId = R.layout.activity_wxlogin)
public class WxLoginActivity extends BaseActivity {
    @BindView(R.id.sendcode)
    TextView sendcode;
    @BindView(R.id.username)
    EditText username;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.smscode)
    EditText smscode;
    @BindView(R.id.regist)
    Button regist;
    @BindView(R.id.logo)
    ImageView logo;

    @Override
    protected void init() {
        sendcode.setOnClickListener(v -> {
            sendCode();
        });
        regist.setOnClickListener(v -> {
            regist();
        });
        setLogo();
    }

    private void setLogo() {
        OkGo.<String>post(Api.publicApi).params("api_name", "logo").execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                Resp<Register> resp = new Gson().fromJson(response.body(), new TypeToken<Resp<Register>>() {
                }.getType());
                if (resp.code == 1) {
                    try {
                        Glide.with(WxLoginActivity.this).load(resp.data.logo).centerCrop().error(R.mipmap.ic_logo).into(logo);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
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
        showDialog("正在绑定", true);
        Register data = (Register) getIntent().getSerializableExtra("code");
        OkGo.<String>post(Api.registerApi).params("api_name", "appBindMobile")
                .params("mobile", userName)
                .params("password", passWord)
                .params("promoter_id", data.promoter_id)
                .params("nickname", data.nickname)
                .params("unionid", data.unionid)
                .params("headimgurl", data.headimgurl)
                .params("sex", data.sex)
                .params("app_openid", data.app_openid)
                .params("code", smsCode).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                dismissDialog();
                try {
                    Resp<Register> resp = new Gson().fromJson(response.body(), new TypeToken<Resp<Register>>() {
                    }.getType());
                    toast(resp.msg);
                    if (resp.code == 1) {
                        Intent intent = new Intent();
                        intent.putExtra("data",resp.data);
                        setResult(RESULT_OK,intent);
                        finish();
                    }
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
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

    private void sendCode() {
        String mobile = username.getText().toString().trim();
        if (TextUtils.isEmpty(mobile) || mobile.length() != 11) {
            toast("请输入正确的手机号码");
            return;
        }
        sendcode.setEnabled(false);
        new CountDownTimer(60000, 1000) {
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
