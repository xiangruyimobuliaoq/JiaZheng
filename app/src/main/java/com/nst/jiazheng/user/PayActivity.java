package com.nst.jiazheng.user;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.nst.jiazheng.R;
import com.nst.jiazheng.aliPay.PayResult;
import com.nst.jiazheng.api.Api;
import com.nst.jiazheng.api.resp.Coupon;
import com.nst.jiazheng.api.resp.PayInfo;
import com.nst.jiazheng.api.resp.Register;
import com.nst.jiazheng.api.resp.Resp;
import com.nst.jiazheng.base.BaseToolBarActivity;
import com.nst.jiazheng.base.Layout;
import com.nst.jiazheng.base.SpUtil;
import com.nst.jiazheng.login.LoginActivity;
import com.nst.jiazheng.api.WXPayEvent;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Map;

import butterknife.BindView;

/**
 * 创建者     彭龙
 * 创建时间   2020/4/27 5:28 PM
 * 描述
 * <p>
 * 更新者     $
 * 更新时间   $
 * 更新描述
 */
@Layout(layoutId = R.layout.activity_pay)
public class PayActivity extends BaseToolBarActivity {
    @BindView(R.id.tv_yue)
    TextView tv_yue;
    @BindView(R.id.total_price)
    TextView total_price;
    @BindView(R.id.pay_price)
    TextView pay_price;
    @BindView(R.id.coupon)
    TextView coupon;
    @BindView(R.id.submit)
    Button submit;
    @BindView(R.id.weixin)
    CheckBox weixin;
    @BindView(R.id.weixinpart)
    LinearLayout weixinpart;
    @BindView(R.id.zhifubao)
    CheckBox zhifubao;
    @BindView(R.id.zhifubaopart)
    LinearLayout zhifubaopart;
    @BindView(R.id.yue)
    CheckBox yue;
    @BindView(R.id.yuepart)
    LinearLayout yuepart;
    private static final int SDK_PAY_FLAG = 1;
    private Register mUserInfo;
    private String mOrderNo;
    private int currentPayType = 1;
    private int currentCouponId = 0;

    @Override
    protected void init() {
        setTitle("支付费用");
        mUserInfo = (Register) SpUtil.readObj("userInfo");
        mOrderNo = getIntent().getStringExtra("orderNo");
        weixinpart.setOnClickListener(view -> {
            if (!weixin.isChecked()) {
                weixin.setChecked(true);
                zhifubao.setChecked(false);
                yue.setChecked(false);
                currentPayType = 1;
            }
        });
        zhifubaopart.setOnClickListener(view -> {
            if (!zhifubao.isChecked()) {
                zhifubao.setChecked(true);
                weixin.setChecked(false);
                yue.setChecked(false);
                currentPayType = 2;
            }
        });
        yuepart.setOnClickListener(view -> {
            if (!yue.isChecked()) {
                yue.setChecked(true);
                weixin.setChecked(false);
                zhifubao.setChecked(false);
                currentPayType = 3;
            }
        });
        getPayInfo();
    }

    private void getPayInfo() {
        showDialog("正在计算", true);
        OkGo.<String>post(Api.serverApi).params("api_name", "server_pay_info").params("token", mUserInfo.token)
                .params("order_no", mOrderNo)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        dismissDialog();
                        Resp<PayInfo> resp = new Gson().fromJson(response.body(), new TypeToken<Resp<PayInfo>>() {
                        }.getType());
                        toast(resp.msg);
                        if (resp.code == 1) {
                            setData(resp.data, false);
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

    private void getPayInfoWithCoupon(int id) {
        showDialog("正在计算", true);
        OkGo.<String>post(Api.serverApi).params("api_name", "server_pay_info").params("token", mUserInfo.token)
                .params("order_no", mOrderNo)
                .params("coupon_id", id)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        dismissDialog();
                        Resp<PayInfo> resp = new Gson().fromJson(response.body(), new TypeToken<Resp<PayInfo>>() {
                        }.getType());
                        toast(resp.msg);
                        if (resp.code == 1) {
                            setData(resp.data, true);
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

    private void setData(PayInfo data, boolean hasCoupon) {
        tv_yue.setText("(剩余" + data.money + "元)");
        total_price.setText("¥ " + data.total_price);
        pay_price.setText("¥ " + data.pay_price);
        if (!hasCoupon)
            coupon.setText("有" + data.coupon_count + "张可用");
        coupon.setOnClickListener(view -> new CouponWindow(this, data.total_price).setOnCouponSelectListener(new CouponWindow.CouponSelectListener() {
            @Override
            public void selected(Coupon d) {
                currentCouponId = d.id;
                getPayInfoWithCoupon(d.id);
                coupon.setText("- ¥ " + d.money);
            }

            @Override
            public void unSelected() {
                currentCouponId = 0;
                getPayInfo();
            }
        }).setPopupGravity(Gravity.BOTTOM).showPopupWindow());
        submit.setOnClickListener(view -> pay());
    }

    private void pay() {
        showDialog("正在请求数据", true);
        OkGo.<String>post(Api.serverApi).params("api_name", "server_pay_sublimit").params("token", mUserInfo.token)
                .params("order_no", mOrderNo)
                .params("pay_type", currentPayType)
                .params("coupon_id", currentCouponId)
                .params("flag", "app")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        dismissDialog();
                        Resp<PayInfo> resp = new Gson().fromJson(response.body(), new TypeToken<Resp<PayInfo>>() {
                        }.getType());
                        toast(resp.msg);
                        if (resp.code == 1) {
                            if (currentPayType == 1) {
                                wxPay(resp.data);
                            } else if (currentPayType == 2) {
                                aliPay(resp.data.result);
                            } else if (currentPayType == 3) {
                                setResult(RESULT_OK);
                                finish();
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

    private void wxPay(PayInfo data) {
        IWXAPI api = WXAPIFactory.createWXAPI(this, Api.APP_ID);
        api.registerApp(Api.APP_ID);
        PayReq payReq = new PayReq();
        payReq.appId = Api.APP_ID;
        payReq.partnerId = data.partnerId;
        payReq.prepayId = data.prepayId;
        payReq.packageValue = data.packageX;
        payReq.nonceStr = data.nonceStr;
        payReq.timeStamp = data.timeStamp;
        payReq.sign = data.sign;
        api.sendReq(payReq);
    }

    public void aliPay(String orderInfo) {
        showDialog("正在启动支付宝", true);
        final Runnable payRunnable = () -> {
            PayTask alipay = new PayTask(PayActivity.this);
            Map<String, String> result = alipay.payV2(orderInfo, true);
            Log.i("msp", result.toString());
            Message msg = new Message();
            msg.what = SDK_PAY_FLAG;
            msg.obj = result;
            mHandler.sendMessage(msg);
        };
        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    dismissDialog();
                    String resultInfo = payResult.getResult();
                    String resultStatus = payResult.getResultStatus();
                    if (TextUtils.equals(resultStatus, "9000")) {
                        toast("充值成功");
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        toast(resultInfo);
                    }
                    break;
                }
            }
        }
    };

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
    public void onWXPayEvent(WXPayEvent ev) {
        setResult(RESULT_OK);
        finish();

    }
}
