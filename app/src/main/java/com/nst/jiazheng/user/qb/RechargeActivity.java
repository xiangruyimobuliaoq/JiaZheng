package com.nst.jiazheng.user.qb;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.nst.jiazheng.R;
import com.nst.jiazheng.aliPay.PayResult;
import com.nst.jiazheng.api.Api;
import com.nst.jiazheng.api.resp.PayInfo;
import com.nst.jiazheng.api.resp.Recharge;
import com.nst.jiazheng.api.resp.Register;
import com.nst.jiazheng.api.resp.Resp;
import com.nst.jiazheng.base.BaseToolBarActivity;
import com.nst.jiazheng.base.Layout;
import com.nst.jiazheng.base.SpUtil;
import com.nst.jiazheng.login.LoginActivity;
import com.nst.jiazheng.worker.utils.MoneyValueFilter;
import com.nst.jiazheng.api.WXPayEvent;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;

/**
 * 创建者     彭龙
 * 创建时间   2020/4/19 9:48 AM
 * 描述
 * <p>
 * 更新者     $
 * 更新时间   $
 * 更新描述
 */
@Layout(layoutId = R.layout.activity_recharge)
public class RechargeActivity extends BaseToolBarActivity {
    @BindView(R.id.rechargelist)
    RecyclerView rechargelist;
    @BindView(R.id.num)
    EditText num;
    @BindView(R.id.weixin)
    CheckBox weixin;
    @BindView(R.id.zhifubao)
    CheckBox zhifubao;
    @BindView(R.id.submit)
    Button submit;
    private Register mUserInfo;
    private RechargeAdapter mAdapter;
    private int chargePosition = -1;
    private static final int SDK_PAY_FLAG = 1;

    @Override
    protected void init() {
        mUserInfo = (Register) SpUtil.readObj("userInfo");
        setTitle("充值");
        GridLayoutManager manager = new GridLayoutManager(this, 2);
        rechargelist.setLayoutManager(manager);
        mAdapter = new RechargeAdapter(R.layout.item_recharge, null);
        num.setFilters(new InputFilter[]{new MoneyValueFilter()});
        rechargelist.setAdapter(mAdapter);
        getRechargeList();
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                chargePosition = position;
                mAdapter.notifyDataSetChanged();
                num.setText(mAdapter.getData().get(position).money);
            }
        });

        weixin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    zhifubao.setChecked(false);
                } else {
                    zhifubao.setChecked(true);
                }
            }
        });
        zhifubao.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    weixin.setChecked(false);
                } else {
                    weixin.setChecked(true);
                }
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeBill();
            }
        });
    }

    private void makeBill() {
        String money = num.getText().toString().trim();
        if (chargePosition == -1 && TextUtils.isEmpty(money)) {
            toast("请选择充值套餐或输入充值金额");
            return;
        }
        showDialog("正在请求数据", true);
        OkGo.<String>post(Api.userApi).params("api_name", "recharge_sublimit").params("token", mUserInfo.token)
                .params("recharge_id", chargePosition == -1 ? 0 : mAdapter.getData().get(chargePosition).id)
                .params("money", money)
                .params("flag", "app")
                .params("pay_type", weixin.isChecked() ? 1 : 2)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        dismissDialog();
                        try {
                            Resp<PayInfo> resp = new Gson().fromJson(response.body(), new TypeToken<Resp<PayInfo>>() {
                            }.getType());
                            toast(resp.msg);
                            if (resp.code == 1) {
                                if (weixin.isChecked()) {
                                    wxPay(resp.data);
                                } else {
                                    aliPay(resp.data.result);
                                }
                            } else if (resp.code == 101) {
                                SpUtil.putBoolean("isLogin", false);
                                startAndClearAll(LoginActivity.class);
                            }
                        } catch (JsonSyntaxException e) {
                            e.printStackTrace();
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
            PayTask alipay = new PayTask(this);
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
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    dismissDialog();
                    /**
                     * 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
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

    private void getRechargeList() {
        OkGo.<String>post(Api.userApi).params("api_name", "recharge_list").params("token", mUserInfo.token).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                Resp<List<Recharge>> resp = new Gson().fromJson(response.body(), new TypeToken<Resp<List<Recharge>>>() {
                }.getType());
                if (resp.code == 1) {
                    mAdapter.setList(resp.data);
                } else if (resp.code == 101) {
                    SpUtil.putBoolean("isLogin", false);
                    startAndClearAll(LoginActivity.class);
                }
            }
        });
    }

    class RechargeAdapter extends BaseQuickAdapter<Recharge, BaseViewHolder> {

        public RechargeAdapter(int layoutResId, List<Recharge> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder baseViewHolder, Recharge recharge) {
            TextView content = baseViewHolder.getView(R.id.content);
            content.setText(recharge.name);
            content.setBackground(baseViewHolder.getLayoutPosition() == chargePosition ? getDrawable(R.drawable.shape_border_blue_corner_xuxian) : getDrawable(R.drawable.shape_border_gray_corner_xuxian));
            content.setTextColor(baseViewHolder.getLayoutPosition() == chargePosition ? getColor(R.color.statusbar_blue) : getColor(R.color.line_3));
        }
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
    public void onWXPayEvent(WXPayEvent ev) {
        setResult(RESULT_OK);
        finish();
    }
}
