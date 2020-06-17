package com.nst.jiazheng.user.grzx;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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
import com.nst.jiazheng.user.wdgj.WorkerInfoActivity;
import com.nst.jiazheng.worker.widget.ConfirmWindow;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;
import io.rong.imkit.RongIM;
import io.rong.imkit.userInfoCache.RongUserInfoManager;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.UserInfo;

/**
 * 创建者     彭龙
 * 创建时间   2020/5/11 3:33 PM
 * 描述
 * <p>
 * 更新者     $
 * 更新时间   $
 * 更新描述
 */
@Layout(layoutId = R.layout.user_orderdetails_jinxingzhong)
public class OrderDetailsJinxingzhongActivity extends BaseToolBarActivity {
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
    @BindView(R.id.total_price)
    TextView total_price;
    @BindView(R.id.coupon_money)
    TextView coupon_money;
    @BindView(R.id.time)
    TextView time;
    @BindView(R.id.pay_price)
    TextView pay_price;
    @BindView(R.id.serve_type_price)
    TextView serve_type_price;
    @BindView(R.id.submit)
    TextView submit;
    //    @BindView(R.id.cancel)
//    TextView cancel;
    @BindView(R.id.nickname)
    TextView nickname;
    @BindView(R.id.point)
    TextView point;
    @BindView(R.id.staff_name)
    TextView staff_name;
    @BindView(R.id.jie_time)
    TextView jie_time;
    @BindView(R.id.start_time)
    TextView start_time;
    @BindView(R.id.workerinfo)
    LinearLayout workerinfo;
    @BindView(R.id.tx)
    CircleImageView tx;
    @BindView(R.id.ddh)
    ImageView ddh;
    @BindView(R.id.xx)
    ImageView xx;
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
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        order_no.setText("订单编号: " + order.order_no);
        status.setText(order.StatusText);
        serve_type_name.setText("服务类型: " + order.serve_type_name);
        num.setText("数量: " + order.num);
        total_price.setText("应付金额: ¥ " + order.total_price);
        coupon_money.setText("优惠券抵扣: ¥ " + order.coupon_money);
        content.setText(order.content);
        address.setText("服务地址: " + order.address);
        time.setText("预约时间: " + order.time);
        pay_price.setText("¥ " + order.pay_price);
        serve_type_price.setText("服务单价: ¥ " + order.serve_type_price + " /" + order.serve_type_units);
        nickname.setText(order.staff_name);
        staff_name.setText("接单管家: " + order.staff_name);
        point.setText(order.staff_score + "分");
        jie_time.setText("接单时间: " + format.format(new Date(order.jie_time * 1000)));
        start_time.setText("开始服务时间: " + format.format(new Date(order.start_time * 1000)));
        ddh.setOnClickListener(view -> {
            new ConfirmWindow(mContext)
                    .setContent(order.staff_mobile, "拨打")
                    .setListener((ConfirmWindow window) -> {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        Uri data = Uri.parse("tel:" + order.staff_mobile);
                        intent.setData(data);
                        startActivity(intent);
                        window.dismiss();
                    })
                    .setPopupGravity(Gravity.CENTER)
                    .setBackPressEnable(true)
                    .setOutSideDismiss(true)
                    .showPopupWindow();
        });
        workerinfo.setOnClickListener(view -> {
            Bundle params = new Bundle();
            params.putString("worker", order.staff_id);
            overlay(WorkerInfoActivity.class, params);
        });
//        cancel.setOnClickListener(view -> {
//            cancelOrder(order.id);
//        });
        submit.setOnClickListener(view -> {
            Bundle params = new Bundle();
            params.putString("id", order.id);
            overlay(ComplainActivity.class, params);
        });
        xx.setOnClickListener(view -> {
            Conversation.ConversationType conversationType = Conversation.ConversationType.PRIVATE;
            String targetId = order.staff_id;
            String title = "聊天";
            UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(targetId);
            RongIM.getInstance().refreshUserInfoCache(userInfo);
            RongIM.getInstance().startConversation(this, conversationType, targetId, title, null);
        });
        try {
            Glide.with(this).load(order.staff_logo).error(R.mipmap.ic_tx).into(tx);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
