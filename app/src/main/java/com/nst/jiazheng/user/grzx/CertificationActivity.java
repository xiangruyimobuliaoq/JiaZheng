package com.nst.jiazheng.user.grzx;

import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.nst.jiazheng.R;
import com.nst.jiazheng.api.Api;
import com.nst.jiazheng.api.resp.Register;
import com.nst.jiazheng.api.resp.Resp;
import com.nst.jiazheng.base.BaseToolBarActivity;
import com.nst.jiazheng.base.Layout;
import com.nst.jiazheng.base.SpUtil;
import com.nst.jiazheng.login.LoginActivity;

import butterknife.BindView;

/**
 * 创建者     彭龙
 * 创建时间   2020/5/6 3:51 PM
 * 描述
 * <p>
 * 更新者     $
 * 更新时间   $
 * 更新描述
 */
@Layout(layoutId = R.layout.activity_certification)
public class CertificationActivity extends BaseToolBarActivity {
    @BindView(R.id.name)
    EditText name;
    @BindView(R.id.cerNum)
    EditText cerNum;
    @BindView(R.id.submit)
    Button submit;
    private Register mUserInfo;

    @Override
    protected void init() {
        mUserInfo = (Register) SpUtil.readObj("userInfo");
        setTitle("实名认证");
        submit.setOnClickListener(view -> {
            submitCertificate();
        });
    }

    private void submitCertificate() {
        String name = this.name.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            toast("请输入姓名");
            return;
        }
        String cerNum = this.cerNum.getText().toString().trim();
        if (TextUtils.isEmpty(cerNum)) {
            toast("请输入身份证号码");
            return;
        }
        showDialog("认证中", true);
        OkGo.<String>post(Api.certificationApi).params("api_name", "idmatch")
                .params("token", mUserInfo.token)
                .params("id_card", cerNum)
                .params("name", name)
                .params("flag", "app").execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                dismissDialog();
                Resp resp = new Gson().fromJson(response.body(), Resp.class);
                toast(resp.msg);
                if (resp.code == 1) {
                    finish();
                }else if (resp.code == 101) {
                    SpUtil.putBoolean("isLogin", false);
                    startAndClearAll(LoginActivity.class);
                }
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                dismissDialog();
            }
        });
    }
}
