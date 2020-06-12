package com.nst.jiazheng.user;

import android.content.Context;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.nst.jiazheng.R;
import com.nst.jiazheng.api.Api;
import com.nst.jiazheng.api.resp.Coupon;
import com.nst.jiazheng.api.resp.Register;
import com.nst.jiazheng.api.resp.Resp;
import com.nst.jiazheng.base.BaseActivity;
import com.nst.jiazheng.base.SpUtil;
import com.nst.jiazheng.login.LoginActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import razerdp.basepopup.BasePopupWindow;

/**
 * 创建者     彭龙
 * 创建时间   2020/6/11 11:45 AM
 * 描述
 * <p>
 * 更新者     $
 * 更新时间   $
 * 更新描述
 */
public class CouponWindow extends BasePopupWindow {

    private Register mUserInfo;
    private CouponAdapter mAdapter;
    private CouponSelectListener listener;
    private String payPrice;
    private View mCancel;

    public CouponWindow(Context context, String payPrice) {
        super(context);
        this.payPrice = payPrice;
    }

    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.window_coupon);
    }

    @Override
    public void onViewCreated(View contentView) {
        super.onViewCreated(contentView);
        RecyclerView couponlist = contentView.findViewById(R.id.couponlist);
        mCancel = contentView.findViewById(R.id.submit);
        mUserInfo = (Register) SpUtil.readObj("userInfo");
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(RecyclerView.VERTICAL);
        couponlist.setLayoutManager(manager);
        mAdapter = new CouponAdapter(R.layout.item_coupon_window, null);
        couponlist.setAdapter(mAdapter);
        getCouponList();
    }

    public CouponWindow setOnCouponSelectListener(CouponSelectListener listener) {
        this.listener = listener;
        mCancel.setOnClickListener(view -> {
            listener.unSelected();
            dismiss();
        });
        return this;
    }


    private void getCouponList() {
        OkGo.<String>post(Api.userApi).params("api_name", "coupon_list")
                .params("token", mUserInfo.token)
                .params("pay_price", payPrice)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Resp<List<Coupon>> resp = new Gson().fromJson(response.body(), new TypeToken<Resp<List<Coupon>>>() {
                        }.getType());
                        if (resp.code == 1) {
                            mAdapter.setList(resp.data);
                        } else if (resp.code == 101) {
                            SpUtil.putBoolean("isLogin", false);
                            ((BaseActivity) getContext()).startAndClearAll(LoginActivity.class);
                        }
                    }
                });
    }

    class CouponAdapter extends BaseQuickAdapter<Coupon, BaseViewHolder> {

        public CouponAdapter(int layoutResId, List<Coupon> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder baseViewHolder, Coupon coupon) {
            baseViewHolder.setText(R.id.name, coupon.name)
                    .setText(R.id.money_limit, "满 " + coupon.money_limit + " 使用")
                    .setText(R.id.time, "有效日期: " + new SimpleDateFormat("yyyy/MM/dd").format(new Date(coupon.ctime * 1000)) + " - " + new SimpleDateFormat("yyyy/MM/dd").format(new Date(coupon.etime * 1000)))
                    .setText(R.id.money, String.valueOf(coupon.money));
            baseViewHolder.findView(R.id.use).setOnClickListener(view -> {
                if (null != listener) {
                    listener.selected(coupon);
                    dismiss();
                }
            });
        }
    }

    public interface CouponSelectListener {
        void selected(Coupon id);

        void unSelected();
    }
}
