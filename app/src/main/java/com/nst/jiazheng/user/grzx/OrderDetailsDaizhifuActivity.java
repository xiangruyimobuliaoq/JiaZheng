package com.nst.jiazheng.user.grzx;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
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
import com.nst.jiazheng.base.SpUtil;
import com.nst.jiazheng.login.LoginActivity;
import com.nst.jiazheng.user.PayActivity;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.BindViews;

/**
 * 创建者     彭龙
 * 创建时间   2020/5/11 3:33 PM
 * 描述
 * <p>
 * 更新者     $
 * 更新时间   $
 * 更新描述
 */
@Layout(layoutId = R.layout.user_orderdetails_daizhifu)
public class OrderDetailsDaizhifuActivity extends BaseToolBarActivity {
    @BindView(R.id.order_no)
    TextView order_no;
    @BindView(R.id.status)
    TextView status;
    @BindView(R.id.serve_type_name)
    TextView serve_type_name;
    @BindView(R.id.num)
    TextView num;
    @BindView(R.id.address)
    TextView address;
    @BindView(R.id.time)
    TextView time;
    @BindView(R.id.pay_price)
    TextView pay_price;
    @BindView(R.id.serve_type_price)
    TextView serve_type_price;
    @BindView(R.id.content)
    TextView content;
    @BindView(R.id.submit)
    Button submit;
    private Register mUserInfo;
    private String mId;


    @Override
    protected void init() {
        setTitle("订单详情");
        mId = getIntent().getStringExtra("id");
        mUserInfo = (Register) SpUtil.readObj("userInfo");
        OkGo.<String>post(Api.orderApi).params("api_name", "order_info")
                .params("token", mUserInfo.token)
                .params("order_id", mId).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                Resp<Order> resp = new Gson().fromJson(response.body(), new TypeToken<Resp<Order>>() {
                }.getType());
                if (resp.code == 1) {
                    setData(resp.data);
                } else if (resp.code == 101) {
                    SpUtil.putBoolean("isLogin", false);
                    startAndClearAll(LoginActivity.class);
                }
            }
        });
    }

    private void setData(Order order) {
        order_no.setText("订单编号: " + order.order_no);
        status.setText(order.StatusText);
        content.setText(order.content);
        serve_type_name.setText("服务类型: " + order.serve_type_name);
        num.setText("数量: " + order.num);
        address.setText("服务地址: " + order.address);
        time.setText("预约时间: " + order.time);
        pay_price.setText("¥ " + order.pay_price);
        serve_type_price.setText("服务单价: ¥ " + order.serve_type_price + " /" + order.serve_type_units);
        submit.setOnClickListener(view -> {
            Bundle params = new Bundle();
            params.putString("orderNo", order.order_no);
            overlayForResult(PayActivity.class, 1, params);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bundle bundle = new Bundle();
            bundle.putString("id", mId);
            overlay(OrderDetailsDaijiedanActivity.class, bundle);
            finish();
        }
    }
}
