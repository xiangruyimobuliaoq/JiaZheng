package com.nst.jiazheng.user.qb;

import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.nst.jiazheng.R;
import com.nst.jiazheng.api.Api;
import com.nst.jiazheng.api.resp.Integral;
import com.nst.jiazheng.api.resp.IntegralResp;
import com.nst.jiazheng.api.resp.Register;
import com.nst.jiazheng.api.resp.Resp;
import com.nst.jiazheng.base.BaseToolBarActivity;
import com.nst.jiazheng.base.Layout;
import com.nst.jiazheng.base.SpUtil;
import com.nst.jiazheng.login.LoginActivity;
import com.nst.jiazheng.worker.widget.ConfirmWindow;

import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;

/**
 * 创建者     彭龙
 * 创建时间   2020/4/7 10:20 AM
 * 描述
 * <p>
 * 更新者     $
 * 更新时间   $
 * 更新描述
 */
@Layout(layoutId = R.layout.activity_myintegral)
public class MyIntegralActivity extends BaseToolBarActivity {
    @BindView(R.id.integral)
    TextView integral;
    @BindView(R.id.integrallog)
    TextView integrallog;
    @BindView(R.id.integrallist)
    RecyclerView integrallist;
    private Register mUserInfo;
    private IntegralAdapter mAdapter;

    @Override
    protected void init() {
        mUserInfo = (Register) SpUtil.readObj("userInfo");
        setTitle("我的积分");
        getIntegralList();
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(RecyclerView.VERTICAL);
        integrallist.setLayoutManager(manager);
        mAdapter = new IntegralAdapter(R.layout.item_integral, null);
        integrallist.setAdapter(mAdapter);
        integrallog.setOnClickListener(view -> {
            overlay(IntegralLogActivity.class);
        });
    }

    private void getIntegralList() {
        OkGo.<String>post(Api.userApi).params("api_name", "my_integral").params("token", mUserInfo.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Resp<IntegralResp> resp = new Gson().fromJson(response.body(), new TypeToken<Resp<IntegralResp>>() {
                        }.getType());
                        if (resp.code == 1) {
                            integral.setText(String.valueOf(resp.data.integral));
                            mAdapter.setList(resp.data.list);
                        } else if (resp.code == 101) {
                            SpUtil.putBoolean("isLogin", false);
                            startAndClearAll(LoginActivity.class);
                        }
                    }
                });
    }

    private void integralSublimit(Integral integral) {
        showDialog("正在兑换", true);
        OkGo.<String>post(Api.userApi).params("api_name", "integral_sublimit").params("token", mUserInfo.token)
                .params("coupon_id", integral.id)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        dismissDialog();
                        Resp<IntegralResp> resp = new Gson().fromJson(response.body(), new TypeToken<Resp<IntegralResp>>() {
                        }.getType());
                        toast(resp.msg);
                        if (resp.code == 1) {
                            getIntegralList();
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

    class IntegralAdapter extends BaseQuickAdapter<Integral, BaseViewHolder> {

        public IntegralAdapter(int layoutResId, List<Integral> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder baseViewHolder, Integral integral) {
            baseViewHolder.setText(R.id.name, integral.name)
                    .setText(R.id.money, "满 " + integral.money_limit + " 减 " + integral.money)
                    .setText(R.id.integral, integral.integral + "积分")
                    .setText(R.id.date, "有效期: " + integral.date + " 天");
            baseViewHolder.getView(R.id.click).setOnClickListener(view -> {
                new ConfirmWindow(MyIntegralActivity.this)
                        .setContent("是否确认兑换该优惠券？")
                        .setListener((ConfirmWindow window) -> {
                            integralSublimit(integral);
                            window.dismiss();
                        })
                        .setPopupGravity(Gravity.CENTER)
                        .setBackPressEnable(true)
                        .setOutSideDismiss(true)
                        .showPopupWindow();
            });
        }
    }
}
