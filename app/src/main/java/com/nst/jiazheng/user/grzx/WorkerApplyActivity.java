package com.nst.jiazheng.user.grzx;

import android.view.Gravity;
import android.widget.Button;
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
import com.nst.jiazheng.api.resp.UserCenter;
import com.nst.jiazheng.base.BaseToolBarActivity;
import com.nst.jiazheng.base.Layout;
import com.nst.jiazheng.base.SpUtil;
import com.nst.jiazheng.login.LoginActivity;
import com.nst.jiazheng.worker.widget.ConfirmWindow;

import butterknife.BindView;

/**
 * 创建者     彭龙
 * 创建时间   2020/4/28 3:11 PM
 * 描述
 * <p>
 * 更新者     $
 * 更新时间   $
 * 更新描述
 */
@Layout(layoutId = R.layout.activity_workerapply)
public class WorkerApplyActivity extends BaseToolBarActivity {
    @BindView(R.id.xieyi)
    TextView xieyi;
    @BindView(R.id.submit)
    Button submit;
    private Register mUserInfo;

    @Override
    protected void init() {
        mUserInfo = (Register) SpUtil.readObj("userInfo");
        setTitle("私人管家认证");
        xieyi.setOnClickListener(view -> {
            overlay(WorkerAgreementActivity.class);
        });
        submit.setOnClickListener(view -> {
            overlay(ApplySubmitActivity.class);
        });
        submit.setEnabled(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getCenterData();
    }

    private void getCenterData() {
        showDialog("获取实名认证信息", true);
        OkGo.<String>post(Api.userApi).params("api_name", "center").params("token", mUserInfo.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        dismissDialog();
                        Resp<UserCenter> resp = new Gson().fromJson(response.body(), new TypeToken<Resp<UserCenter>>() {
                        }.getType());
                        toast(resp.msg);
                        if (resp.code == 1) {
                            if (resp.data.is_certification == 1) {
                                submit.setEnabled(true);
                            } else {
                                showCertWindow();
                            }
                        } else if (resp.code == 101) {
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

    private void showCertWindow() {
        new ConfirmWindow(mContext)
                .setContent("请先进行实名认证操作，点击\n" +
                        "确定跳转到实名认证页面\n", "确定")
                .setListener((ConfirmWindow window) -> {
                    overlay(CertificationActivity.class);
                    window.dismiss();
                })
                .setPopupGravity(Gravity.CENTER)
                .setBackPressEnable(true)
                .setOutSideDismiss(true)
                .showPopupWindow();


    }
}
