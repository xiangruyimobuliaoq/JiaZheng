package com.nst.jiazheng.worker;

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
    @BindView(R.id.tv_yue)
    TextView tv_yue;
    @BindView(R.id.today_order)
    TextView today_order;
    @BindView(R.id.today_income)
    TextView today_income;
    private Register mUserInfo;

    @Override
    protected void init() {
        initView();
        initData();
        initEvent();
    }

    private void initEvent() {
        mIvChat.setOnClickListener(view -> overlay(ChatActivity.class));
        mTvWithDraw.setOnClickListener(view -> overlay(WithdrawActivity.class));
        mTvWithDrawRecord.setOnClickListener(view -> overlay(WithdrawalsRecordActivity.class));
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

    private void initView() {
        setTitle("管家首页");
        mIvChat.setImageResource(R.mipmap.ic_xiaox);
        vp.setSwipeLocked(true);
        vp.setAdapter(new OrderPageAdapter(getSupportFragmentManager(), 1));
        vp.setOffscreenPageLimit(5);
    }

    private void initData() {
        mUserInfo = (Register) SpUtil.readObj("userInfo");
    }

    @Override
    protected void onResume() {
        super.onResume();
        getCenterInfo();
    }

    private void getCenterInfo() {
        OkGo.<String>post(Api.userApi).params("api_name", "private_center").params("token", mUserInfo.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Resp<UserCenter> resp = new Gson().fromJson(response.body(), new TypeToken<Resp<UserCenter>>() {
                        }.getType());
                        if (resp.code == 1) {
                            setData(resp.data);
                        }
                    }
                });
    }

    private void setData(UserCenter data) {
        tv_yue.setText(data.withdraw_money);
        tv_shouru.setText(data.total_money);
        today_order.setText(String.valueOf(data.today_order));
        today_income.setText(String.valueOf(data.today_income));
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
            return 5;
        }
    }
}
