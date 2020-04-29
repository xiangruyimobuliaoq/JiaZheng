package com.nst.jiazheng.user;

import android.widget.CheckBox;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.nst.jiazheng.R;
import com.nst.jiazheng.api.Api;
import com.nst.jiazheng.api.resp.PayInfo;
import com.nst.jiazheng.api.resp.Register;
import com.nst.jiazheng.api.resp.Resp;
import com.nst.jiazheng.base.BaseToolBarActivity;
import com.nst.jiazheng.base.Layout;
import com.nst.jiazheng.base.SpUtil;

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
    @BindView(R.id.weixin)
    CheckBox weixin;
    @BindView(R.id.zhifubao)
    CheckBox zhifubao;
    @BindView(R.id.yue)
    CheckBox yue;

    private Register mUserInfo;
    private String mOrderNo;

    @Override
    protected void init() {
        setTitle("支付费用");
        mUserInfo = (Register) SpUtil.readObj("userInfo");
        mOrderNo = getIntent().getStringExtra("orderNo");
        weixin.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                zhifubao.setChecked(false);
                yue.setChecked(false);
            }
        });
        zhifubao.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                weixin.setChecked(false);
                yue.setChecked(false);
            }
        });
        yue.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                weixin.setChecked(false);
                zhifubao.setChecked(false);
            }
        });
        getPayInfo();
    }

    private void getPayInfo() {
        OkGo.<String>post(Api.serverApi).params("api_name", "server_pay_info").params("token", mUserInfo.token)
                .params("order_no", mOrderNo)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Resp<PayInfo> resp = new Gson().fromJson(response.body(), new TypeToken<Resp<PayInfo>>() {
                        }.getType());
                        if (resp.code == 1) {
                            setData(resp.data);
                        }
                    }
                });
    }

    private void setData(PayInfo data) {
        tv_yue.setText("(剩余" + data.money + "元)");
        total_price.setText("¥ " + data.total_price);
        pay_price.setText("¥ " + data.pay_price);
        coupon.setText("有" + data.coupon_count + "张可用");
    }
}
