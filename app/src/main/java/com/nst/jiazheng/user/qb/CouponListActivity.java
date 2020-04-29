package com.nst.jiazheng.user.qb;

import android.view.Gravity;
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
import com.nst.jiazheng.base.BaseToolBarActivity;
import com.nst.jiazheng.base.Layout;
import com.nst.jiazheng.base.SpUtil;
import com.nst.jiazheng.worker.widget.ConfirmWindow;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;

/**
 * 创建者     彭龙
 * 创建时间   2020/4/22 9:57 AM
 * 描述
 * <p>
 * 更新者     $
 * 更新时间   $
 * 更新描述
 */
@Layout(layoutId = R.layout.activity_couponlist)
public class CouponListActivity extends BaseToolBarActivity {
    @BindView(R.id.couponlist)
    RecyclerView couponlist;
    private Register mUserInfo;
    private CouponAdapter mAdapter;

    @Override
    protected void init() {
        setTitle("我的优惠券");
        mUserInfo = (Register) SpUtil.readObj("userInfo");
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(RecyclerView.VERTICAL);
        couponlist.setLayoutManager(manager);
        mAdapter = new CouponAdapter(R.layout.item_coupon, null);
        couponlist.setAdapter(mAdapter);
        getCouponList();
    }

    private void getCouponList() {
        OkGo.<String>post(Api.userApi).params("api_name", "coupon_list").params("token", mUserInfo.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Resp<List<Coupon>> resp = new Gson().fromJson(response.body(), new TypeToken<Resp<List<Coupon>>>() {
                        }.getType());
                        if (resp.code == 1) {
                            mAdapter.setList(resp.data);
                        }
                    }
                });
    }

    private void deleteCoupon(Coupon coupon) {
        OkGo.<String>post(Api.userApi).params("api_name", "coupon_del").params("token", mUserInfo.token)
                .params("coupon_id", coupon.id)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Resp resp = new Gson().fromJson(response.body(), new TypeToken<Resp>() {
                        }.getType());
                        toast(resp.msg);
                        if (resp.code == 1) {
                            mAdapter.remove(coupon);
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
            baseViewHolder.findView(R.id.del).setOnClickListener(view -> {

                new ConfirmWindow(CouponListActivity.this)
                        .setContent("是否确认删除该优惠券？")
                        .setListener((ConfirmWindow window) -> {
                            deleteCoupon(coupon);
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
