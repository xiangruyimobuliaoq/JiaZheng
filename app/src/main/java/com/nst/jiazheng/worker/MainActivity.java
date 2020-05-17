package com.nst.jiazheng.worker;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.nst.jiazheng.R;
import com.nst.jiazheng.api.Api;
import com.nst.jiazheng.api.resp.Register;
import com.nst.jiazheng.api.resp.Resp;
import com.nst.jiazheng.api.resp.UserCenter;
import com.nst.jiazheng.base.BaseToolBarActivity;
import com.nst.jiazheng.base.Layout;
import com.nst.jiazheng.base.LockableViewPager;
import com.nst.jiazheng.base.SpUtil;
import com.nst.jiazheng.login.LoginActivity;

import butterknife.BindView;

/**
 * 创建者     彭龙
 * 创建时间   2020/4/8 2:15 PM
 * 描述
 * <p>
 * 更新者     $
 * 更新时间   $
 * 更新描述
 */
@Layout(layoutId = R.layout.activity_main)
public class MainActivity extends BaseToolBarActivity {
    @BindView(R.id.item)
    ImageView mIvChat;
    @BindView(R.id.tab)
    TabLayout mTab;
    @BindView(R.id.vp)
    LockableViewPager vp;
    @BindView(R.id.tv_withDraw)
    TextView mTvWithDraw;
    @BindView(R.id.tv_withDrawRecord)
    TextView mTvWithDrawRecord;
    @BindView(R.id.tv_service)
    TextView mTvServiceType;
    @BindView(R.id.tv_comment)
    TextView mTvComment;
    @BindView(R.id.tv_my)
    TextView mTvMy;
    @BindView(R.id.tv_shouru)
    TextView tv_shouru;
    @BindView(R.id.tv_shouru_title)
    TextView tv_shouru_title;
    @BindView(R.id.tv_yue_title)
    TextView tv_yue_title;
    @BindView(R.id.tv_yue)
    TextView tv_yue;
    @BindView(R.id.today_order_title)
    TextView today_order_title;
    @BindView(R.id.today_income_title)
    TextView today_income_title;
    @BindView(R.id.today_order)
    TextView today_order;
    @BindView(R.id.today_income)
    TextView today_income;
    private Register mUserInfo;

    @Override
    protected void init() {
        mUserInfo = (Register) SpUtil.readObj("userInfo");
        if (mUserInfo.type == 2) {
            setTitle("管家首页");
            tv_shouru_title.setText("总收入（元）");
            tv_yue_title.setText("余额（元）");
            today_order_title.setText("今日接单");
            today_income_title.setText("今日收入（元）");
            mTvWithDraw.setVisibility(View.VISIBLE);
            mTvWithDrawRecord.setVisibility(View.VISIBLE);
            mTvWithDraw.setOnClickListener(view -> overlay(WithdrawActivity.class));
            mTvWithDrawRecord.setOnClickListener(view -> overlay(WithdrawalsRecordActivity.class));
            mTab.addTab(mTab.newTab().setText("接单栏"));
            mTab.addTab(mTab.newTab().setText("待进行"));
            mTab.addTab(mTab.newTab().setText("进行中"));
            mTab.addTab(mTab.newTab().setText("待确认"));
            mTab.addTab(mTab.newTab().setText("已完成"));
        } else {
            setTitle("家政首页");
            tv_shouru_title.setText("今日已完成");
            tv_yue_title.setText("总完成任务");
            today_order_title.setText("本周已完成");
            today_income_title.setText("本月已完成");
            mTvWithDraw.setVisibility(View.GONE);
            mTvWithDrawRecord.setVisibility(View.GONE);
            mTab.addTab(mTab.newTab().setText("任务栏"));
            mTab.addTab(mTab.newTab().setText("进行中"));
            mTab.addTab(mTab.newTab().setText("待确认"));
            mTab.addTab(mTab.newTab().setText("已完成"));
        }
        mIvChat.setImageResource(R.mipmap.ic_xiaox);
        vp.setSwipeLocked(true);
        vp.setAdapter(new OrderPageAdapter(getSupportFragmentManager(), 1));
        vp.setOffscreenPageLimit(mTab.getTabCount() - 1);
        mIvChat.setOnClickListener(view -> overlay(ChatActivity.class));
        mTvMy.setOnClickListener(view -> overlay(MyProfileActivity.class));
        mTvComment.setOnClickListener(view -> overlay(MyCommentActivity.class));
        mTvServiceType.setOnClickListener(view -> overlay(ServiceTypeActivity.class));
        mTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                vp.setCurrentItem(position, false);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        getCenterInfo();
    }

    private void getCenterInfo() {
        OkGo.<String>post(Api.userApi).params("api_name", mUserInfo.type == 2 ? "private_center" : "staff_center").params("token", mUserInfo.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Resp<UserCenter> resp = new Gson().fromJson(response.body(), new TypeToken<Resp<UserCenter>>() {
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

    private void setData(UserCenter data) {
        if (mUserInfo.type == 2) {
            tv_yue.setText(data.withdraw_money);
            tv_shouru.setText(data.total_money);
            today_order.setText(String.valueOf(data.today_order));
            today_income.setText(String.valueOf(data.today_income));
        } else {
            tv_yue.setText(data.total_order);
            tv_shouru.setText(data.today_order);
            today_order.setText(String.valueOf(data.week_order));
            today_income.setText(String.valueOf(data.month_order));
        }
    }

    class OrderPageAdapter extends FragmentPagerAdapter {

        public OrderPageAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return new OrderFragment(position);
        }

        @Override
        public int getCount() {
            return mTab.getTabCount();
        }
    }
}
