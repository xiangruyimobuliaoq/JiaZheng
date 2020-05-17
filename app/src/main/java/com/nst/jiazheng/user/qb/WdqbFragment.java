package com.nst.jiazheng.user.qb;

import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.nst.jiazheng.R;
import com.nst.jiazheng.api.Api;
import com.nst.jiazheng.api.resp.Money;
import com.nst.jiazheng.api.resp.Register;
import com.nst.jiazheng.api.resp.Resp;
import com.nst.jiazheng.api.resp.UserCenter;
import com.nst.jiazheng.base.BaseFragment;
import com.nst.jiazheng.base.Layout;
import com.nst.jiazheng.base.SpUtil;
import com.nst.jiazheng.login.LoginActivity;

import butterknife.BindView;

/**
 * 创建者     彭龙
 * 创建时间   2020/4/2 6:01 PM
 * 描述
 * <p>
 * 更新者     $
 * 更新时间   $
 * 更新描述
 */
@Layout(layoutId = R.layout.fragment_wdqb)
public class WdqbFragment extends BaseFragment {
    @BindView(R.id.wdjf)
    TextView wdjf;
    @BindView(R.id.recharge)
    TextView recharge;
    @BindView(R.id.money)
    TextView money;
    @BindView(R.id.couponlist)
    TextView couponlist;
    @BindView(R.id.cashlog)
    TextView cashlog;
    private Register mUserInfo;

    @Override
    protected void init() {
        mUserInfo = (Register) SpUtil.readObj("userInfo");
        wdjf.setOnClickListener(v -> {
            overlay(MyIntegralActivity.class);
        });
        recharge.setOnClickListener(view -> overlay(RechargeActivity.class));
        couponlist.setOnClickListener(view -> {
            overlay(CouponListActivity.class);
        });
        cashlog.setOnClickListener(view -> {
            overlay(CashLogActivity.class);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getUserInfo();
    }

    private void getUserInfo() {
        OkGo.<String>post(Api.userApi).params("api_name", "center").params("token", mUserInfo.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Resp<UserCenter> resp = new Gson().fromJson(response.body(), new TypeToken<Resp<UserCenter>>() {
                        }.getType());
                        if (resp.code == 1) {
                            money.setText(String.valueOf(resp.data.money));
                        }else if (resp.code == 101) {
                            SpUtil.putBoolean("isLogin", false);
                            startAndClearAll(LoginActivity.class);
                        }
                    }
                });
    }
}
