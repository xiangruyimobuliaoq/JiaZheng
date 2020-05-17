package com.nst.jiazheng.worker;

import android.content.Intent;
import android.net.Uri;
import android.view.Gravity;
import android.view.View;
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
import com.nst.jiazheng.api.resp.Order;
import com.nst.jiazheng.api.resp.Register;
import com.nst.jiazheng.api.resp.Resp;
import com.nst.jiazheng.base.BaseToolBarActivity;
import com.nst.jiazheng.base.Layout;
import com.nst.jiazheng.base.SpUtil;
import com.nst.jiazheng.login.LoginActivity;
import com.nst.jiazheng.worker.widget.ConfirmWindow;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 创建者     彭龙
 * 创建时间   2020/5/11 3:33 PM
 * 描述
 * <p>
 * 更新者     $
 * 更新时间   $
 * 更新描述
 */
@Layout(layoutId = R.layout.activity_jiedanlan)
public class JiedanlanActivity extends BaseToolBarActivity {
    @BindView(R.id.tx)
    CircleImageView tx;
    @BindView(R.id.nickname)
    TextView nickname;
    @BindView(R.id.score)
    TextView score;
    @BindView(R.id.ddh)
    ImageView ddh;
    @BindView(R.id.tv_confirm)
    TextView mTvConfirm;
    @BindView(R.id.xx)
    ImageView xx;
    @BindView(R.id.dh)
    ImageView dh;
    @BindView(R.id.serve_type_name)
    TextView serve_type_name;
    @BindView(R.id.content)
    TextView content;
    @BindView(R.id.time)
    TextView time;
    @BindView(R.id.serve_type_units)
    TextView serve_type_units;
    @BindView(R.id.num)
    TextView num;
    @BindView(R.id.pay_price)
    TextView pay_price;
    private Register mUserInfo;

    @Override
    protected void init() {
        setTitle("订单详情");
        String id = getIntent().getStringExtra("id");
        mUserInfo = (Register) SpUtil.readObj("userInfo");
        OkGo.<String>post(Api.orderApi).params("api_name", "app_order_info")
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

    private void setData(Order data) {
        nickname.setText(data.nickname);
        score.setText(data.staff_score);
        ddh.setOnClickListener(view -> {
            new ConfirmWindow(mContext)
                    .setContent(data.staff_mobile, "拨打")
                    .setListener((ConfirmWindow window) -> {
                        callServer(data.staff_mobile);
                        window.dismiss();
                    })
                    .setPopupGravity(Gravity.CENTER)
                    .setBackPressEnable(true)
                    .setOutSideDismiss(true)
                    .showPopupWindow();
        });
        mTvConfirm.setOnClickListener(view -> {
            new ConfirmWindow(this)
                    .setContent("是否确认接单？")
                    .setListener((ConfirmWindow window) -> {
                        acceptOrder(data);
                        window.dismiss();
                    })
                    .setPopupGravity(Gravity.CENTER)
                    .setBackPressEnable(true)
                    .setOutSideDismiss(true)
                    .showPopupWindow();
        });
        xx.setOnClickListener(view -> {

        });
        dh.setOnClickListener(view -> {

        });
        serve_type_name.setText(data.serve_type_name);
        content.setText(data.content);
        time.setText(data.time);
        serve_type_units.setText(data.serve_type_units);
        num.setText(data.num);
        pay_price.setText("¥ " + data.pay_price);

        try {
            Glide.with(this).load(data.staff_logo).error(R.mipmap.ic_tx).into(tx);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void callServer(String staff_mobile) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        Uri data = Uri.parse("tel:" + staff_mobile);
        intent.setData(data);
        startActivity(intent);
    }

    private void acceptOrder(Order order) {
        showDialog("正在提交", true);
        OkGo.<String>post(Api.orderApi).params("api_name", order.status == 2 ? "order_qiang" : "order_confirm")
                .params("token", mUserInfo.token)
                .params("order_id", order.id)
                .execute(new StringCallback() {
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
                });
    }
}