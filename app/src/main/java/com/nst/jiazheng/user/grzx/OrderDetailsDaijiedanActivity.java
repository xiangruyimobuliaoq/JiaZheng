package com.nst.jiazheng.user.grzx;

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

import butterknife.BindView;

/**
 * 创建者     彭龙
 * 创建时间   2020/5/11 3:33 PM
 * 描述
 * <p>
 * 更新者     $
 * 更新时间   $
 * 更新描述
 */
@Layout(layoutId = R.layout.user_orderdetails_daijiedan)
public class OrderDetailsDaijiedanActivity extends BaseToolBarActivity {
    @BindView(R.id.order_no)
    TextView order_no;
    @BindView(R.id.status)
    TextView status;
    @BindView(R.id.content)
    TextView content;
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
    @BindView(R.id.total_price)
    TextView total_price;
    @BindView(R.id.coupon_money)
    TextView coupon_money;
    @BindView(R.id.serve_type_price)
    TextView serve_type_price;
    @BindView(R.id.submit)
    Button submit;
    private Register mUserInfo;

    @Override
    protected void init() {
        setTitle("订单详情");
        String id = getIntent().getStringExtra("id");
        mUserInfo = (Register) SpUtil.readObj("userInfo");
        OkGo.<String>post(Api.orderApi).params("api_name", "order_info")
                .params("token", mUserInfo.token)
                .params("order_id", id).execute(new StringCallback() {
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
        serve_type_name.setText("服务类型: " + order.serve_type_name);
        num.setText("数量: " + order.num);
        content.setText(order.content);
        address.setText("服务地址: " + order.address);
        time.setText("预约时间: " + order.time);
        pay_price.setText("¥ " + order.pay_price);
        total_price.setText("应付金额: ¥ " + order.total_price);
        coupon_money.setText("优惠券抵扣: ¥ " + order.coupon_money);
        serve_type_price.setText("服务单价: ¥ " + order.serve_type_price + " /" + order.serve_type_units);
        submit.setOnClickListener(view -> {
            cancelOrder(order.id);
        });
    }

    private void cancelOrder(String id) {
        showDialog("正在取消", true);
        OkGo.<String>post(Api.orderApi).params("api_name", "order_cancel")
                .params("token", mUserInfo.token)
                .params("order_id", id).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                dismissDialog();
                Resp resp = new Gson().fromJson(response.body(), new TypeToken<Resp>() {
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
                dismissDialog();
            }
        });
    }
}
