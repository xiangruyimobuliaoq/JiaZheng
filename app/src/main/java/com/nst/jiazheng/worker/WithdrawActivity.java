package com.nst.jiazheng.worker;

import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.nst.jiazheng.R;
import com.nst.jiazheng.api.Api;
import com.nst.jiazheng.api.resp.Order;
import com.nst.jiazheng.api.resp.Register;
import com.nst.jiazheng.api.resp.Resp;
import com.nst.jiazheng.base.BaseToolBarActivity;
import com.nst.jiazheng.base.Layout;
import com.nst.jiazheng.base.LogUtil;
import com.nst.jiazheng.base.SpUtil;
import com.nst.jiazheng.base.ToastHelper;
import com.nst.jiazheng.login.LoginActivity;

import butterknife.BindView;
/**
 * 创建者     ZhangAnran
 * 创建时间   2020/4/18 2:33 PM
 * 描述	      提现
 */
@Layout(layoutId = R.layout.activity_draw)
public class WithdrawActivity extends BaseToolBarActivity {
    @BindView(R.id.tv_request)
    TextView mTvRequest;
    @BindView(R.id.et_money)
    EditText mEtMoney;
    private Register mUserInfo;

    @Override
    protected void init() {
        initView();
        initData();
        initEvent();
    }

    private void initData() {
        mUserInfo = (Register) SpUtil.readObj("userInfo");
    }

    private void initEvent() {
        mTvRequest.setOnClickListener(view -> doWithDraw());
    }

    private void doWithDraw() {
        String money = mEtMoney.getText().toString();
        OkGo.<String>post(Api.userApi)
                .params("api_name", "withdraw_add")
                .params("token", mUserInfo.token)
                .params("money", money)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LogUtil.getInstance().d("withdraw_add : " + response.body());
                        Resp<Order> resp = new Gson().fromJson(response.body(),
                                new TypeToken< Resp<Order>>() {
                                }.getType());
                        if (resp.code == 1) {

                        }else if (resp.code == 101) {
                            SpUtil.putBoolean("isLogin", false);
                            startAndClearAll(LoginActivity.class);
                        }
                        ToastHelper.showToast(resp.msg, mContext);
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                    }
                });
    }

    private void initView() {
        setTitle("提现");
    }
}
