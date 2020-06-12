package com.nst.jiazheng.worker;

import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
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
import com.nst.jiazheng.api.resp.UserCenter;
import com.nst.jiazheng.api.resp.Withdraw;
import com.nst.jiazheng.base.BaseToolBarActivity;
import com.nst.jiazheng.base.Layout;
import com.nst.jiazheng.base.SpUtil;
import com.nst.jiazheng.base.ToastHelper;
import com.nst.jiazheng.login.LoginActivity;
import com.nst.jiazheng.worker.utils.MoneyValueFilter;
import com.nst.jiazheng.api.WechatEvent;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;

import static com.nst.jiazheng.api.Api.APP_ID;

/**
 * 创建者     彭龙
 * 创建时间   2020/4/24 2:53 PM
 * 描述
 * <p>
 * 更新者     $
 * 更新时间   $
 * 更新描述
 */
@Layout(layoutId = R.layout.activity_draw)
public class WithdrawActivity extends BaseToolBarActivity {
    @BindView(R.id.tv_request)
    TextView mTvRequest;
    @BindView(R.id.money)
    TextView money;
    @BindView(R.id.shouxufei)
    TextView shouxufei;
    @BindView(R.id.bindwx)
    TextView bindwx;
    @BindView(R.id.et_money)
    EditText mEtMoney;
    private Register mUserInfo;
    private IWXAPI api;

    @Override
    protected void init() {
        setTitle("提现");
        mUserInfo = (Register) SpUtil.readObj("userInfo");
        withdrawInfo();
        getCenterInfo();
    }

    private void getCenterInfo() {
        OkGo.<String>post(Api.userApi).params("api_name", mUserInfo.type == 2 ? "private_center" : "staff_center").params("token", mUserInfo.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Resp<UserCenter> resp = new Gson().fromJson(response.body(), new TypeToken<Resp<UserCenter>>() {
                        }.getType());
                        if (resp.code == 1) {
                            if (resp.data.bind_weixin == 1) {
                                bindwx.setText("已绑定");
                                bindwx.setEnabled(false);
                            } else {
                                bindwx.setText("未绑定");
                                api = WXAPIFactory.createWXAPI(WithdrawActivity.this, APP_ID, true);
                                api.registerApp(APP_ID);
                                bindwx.setOnClickListener(view -> {
                                    SendAuth.Req req = new SendAuth.Req();
                                    req.scope = "snsapi_userinfo";
                                    req.state = "wechat_bind";
                                    api.sendReq(req);
                                });
                            }
                        } else if (resp.code == 101) {
                            SpUtil.putBoolean("isLogin", false);
                            startAndClearAll(LoginActivity.class);
                        }
                    }
                });
    }

    private void withdrawInfo() {
        OkGo.<String>post(Api.userApi)
                .params("api_name", "withdraw_sum")
                .params("token", mUserInfo.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Resp<Withdraw> resp = new Gson().fromJson(response.body(),
                                new TypeToken<Resp<Withdraw>>() {
                                }.getType());
                        toast(resp.msg);
                        if (resp.code == 1) {
                            setData(resp.data);
                        } else if (resp.code == 101) {
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

    private void bindWx(String code) {
        showDialog("正在验证授权", true);
        OkGo.<String>post(Api.registerApi)
                .params("api_name", "bindWx")
                .params("token", mUserInfo.token)
                .params("wx_code", code).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                dismissDialog();
                Resp resp = new Gson().fromJson(response.body(), Resp.class);
                toast(resp.msg);
                if (resp.code == 1) {
                    getCenterInfo();
                }
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                dismissDialog();
            }
        });
    }

    private void withdrawInfo(String money) {
        OkGo.<String>post(Api.userApi)
                .params("api_name", "withdraw_sum")
                .params("money", money)
                .params("token", mUserInfo.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Resp<Withdraw> resp = new Gson().fromJson(response.body(),
                                new TypeToken<Resp<Withdraw>>() {
                                }.getType());
                        if (resp.code == 1) {
                            shouxufei.setText("手续费：" + resp.data.charge_money + "元（" + resp.data.withdraw_price + "%）   实际到账：" + resp.data.real_money + "元");
                        } else if (resp.code == 101) {
                            SpUtil.putBoolean("isLogin", false);
                            startAndClearAll(LoginActivity.class);
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                    }
                });
    }

    private void setData(Withdraw data) {
        money.setText("可提现金额：￥" + data.withdraw_money);
        shouxufei.setText("手续费：" + data.charge_money + "元（" + data.withdraw_price + "%）   实际到账：" + data.real_money + "元");
        mTvRequest.setOnClickListener(view -> doWithDraw(data));
        mEtMoney.setFilters(new InputFilter[]{new MoneyValueFilter()});
        mEtMoney.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String money = charSequence.toString();
                if (TextUtils.isEmpty(money)) {
                    return;
                }
                try {
                    if (Double.parseDouble(money) > data.withdraw_money) {
                        toast("金额不能超过可提现总额");
                    } else {
                        withdrawInfo(money);

                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void doWithDraw(Withdraw data) {
        if (bindwx.getText().toString().trim().equals("未绑定")) {
            toast("绑定微信才能提现");
            return;
        }
        String money = mEtMoney.getText().toString();
        try {
            if (TextUtils.isEmpty(money)) {
                toast("请输入提现金额");
                return;
            }
            if (Double.parseDouble(money) > data.withdraw_money) {
                toast("金额不能超过可提现总额");
                return;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        OkGo.<String>post(Api.userApi)
                .params("api_name", "withdraw_add")
                .params("token", mUserInfo.token)
                .params("money", money)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Resp<Order> resp = new Gson().fromJson(response.body(),
                                new TypeToken<Resp<Order>>() {
                                }.getType());
                        toast(resp.msg);
                        if (resp.code == 1) {
                            finish();
                        } else if (resp.code == 101) {
                            SpUtil.putBoolean("isLogin", false);
                            startAndClearAll(LoginActivity.class);
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
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
            case "wechat_bind":
                bindWx(ev.code);
                break;
        }
    }

}
