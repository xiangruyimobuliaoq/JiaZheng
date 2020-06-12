package com.nst.jiazheng.user.grzx;

import android.content.Intent;
import android.view.Gravity;
import android.widget.Button;
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
import com.nst.jiazheng.api.resp.UpFile;
import com.nst.jiazheng.api.resp.UserCenter;
import com.nst.jiazheng.base.BaseToolBarActivity;
import com.nst.jiazheng.base.Layout;
import com.nst.jiazheng.base.SpUtil;
import com.nst.jiazheng.login.LoginActivity;
import com.nst.jiazheng.worker.widget.ConfirmWindow;

import androidx.annotation.Nullable;
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
    @BindView(R.id.bg)
    ImageView bg;
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
        getCenterData();
        getBg();
    }

    private void getBg() {
        OkGo.<String>post(Api.serverApi).params("api_name", "private_img").params("token", mUserInfo.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Resp<UpFile> resp = new Gson().fromJson(response.body(), new TypeToken<Resp<UpFile>>() {
                        }.getType());
                        if (resp.code == 1) {
                            try {
                                Glide.with(WorkerApplyActivity.this).load(resp.data.img).centerCrop().error(R.mipmap.ic_bg3).into(bg);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else if (resp.code == 101) {
                            SpUtil.putBoolean("isLogin", false);
                            startAndClearAll(LoginActivity.class);
                        }
                    }
                });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1 && resultCode == RESULT_OK) {
            getCenterData();
        }
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
                    overlayForResult(CertificationActivity.class, 1);
                    window.dismiss();
                })
                .setPopupGravity(Gravity.CENTER)
                .setBackPressEnable(true)
                .setOutSideDismiss(true)
                .showPopupWindow();
    }
}
