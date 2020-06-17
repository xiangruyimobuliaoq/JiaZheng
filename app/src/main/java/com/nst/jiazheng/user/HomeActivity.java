package com.nst.jiazheng.user;

import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.RadioGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.nst.jiazheng.R;
import com.nst.jiazheng.api.Api;
import com.nst.jiazheng.api.resp.Register;
import com.nst.jiazheng.api.resp.Resp;
import com.nst.jiazheng.api.resp.UpFile;
import com.nst.jiazheng.api.resp.UserCenter;
import com.nst.jiazheng.base.BaseActivity;
import com.nst.jiazheng.base.BaseFragment;
import com.nst.jiazheng.base.Layout;
import com.nst.jiazheng.base.LockableViewPager;
import com.nst.jiazheng.base.SpUtil;
import com.nst.jiazheng.im.ImManager;
import com.nst.jiazheng.login.LoginActivity;
import com.nst.jiazheng.user.grzx.GrzxFragment;
import com.nst.jiazheng.user.jzfw.JzfwFragment;
import com.nst.jiazheng.user.qb.WdqbFragment;
import com.nst.jiazheng.user.wdgj.WdgjFragment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import butterknife.BindView;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.UserInfo;

/**
 * 创建者     彭龙
 * 创建时间   2020/4/2 11:48 AM
 * 描述
 * <p>
 * 更新者     $
 * 更新时间   $
 * 更新描述
 */
@Layout(layoutId = R.layout.activity_home_user)
public class HomeActivity extends BaseActivity {
    @BindView(R.id.vp)
    LockableViewPager vp;
    @BindView(R.id.rg)
    RadioGroup rg;
    @BindView(R.id.fbwf)
    ImageView fbwf;
    private Map<Integer, BaseFragment> mList;
    private Register mUserInfo;

    @Override
    protected void init() {
        mUserInfo = (Register) SpUtil.readObj("userInfo");
        mList = new HashMap();
        vp.setSwipeLocked(true);
        vp.setAdapter(new HomePageAdapter(getSupportFragmentManager(), 1));
        vp.setOffscreenPageLimit(3);
        rg.setOnCheckedChangeListener((radioGroup, i) -> {
            switch (i) {
                case R.id.jzfw:
                    vp.setCurrentItem(0, false);
                    break;
                case R.id.wdgj:
                    vp.setCurrentItem(1, false);
                    break;
                case R.id.wdqb:
                    vp.setCurrentItem(2, false);
                    break;
                case R.id.grzx:
                    vp.setCurrentItem(3, false);
                    break;
            }
        });
        fbwf.setOnClickListener(v -> {
            overlay(SendServeActivity.class);
        });
        getBannerData();
    }

    private void getBannerData() {
        OkGo.<String>post(Api.publicApi).params("api_name", "banner")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Resp<List<UpFile>> resp = new Gson().fromJson(response.body(), new TypeToken<Resp<List<UpFile>>>() {
                        }.getType());
                        if (resp.code == 1) {
                            new BannerWindow(HomeActivity.this).setData(resp.data).setPopupGravity(Gravity.CENTER).showPopupWindow();
                        }
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getCenterData();
    }

    private void getCenterData() {
        OkGo.<String>post(Api.userApi).params("api_name", "center").params("token", mUserInfo.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
//                        Log.e("213", response.body());
                        Resp<UserCenter> resp = new Gson().fromJson(response.body(), new TypeToken<Resp<UserCenter>>() {
                        }.getType());
                        if (resp.code == 1) {
                            if (RongIMClient.getInstance().getCurrentConnectionStatus() != RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTED) {
                                ImManager.connect(mUserInfo.ry_token, resp.data);
                            } else {
                                UserInfo userInfo = new UserInfo(resp.data.user_id, resp.data.nickname, Uri.parse(resp.data.headimgurl));
                                RongIM.getInstance().refreshUserInfoCache(userInfo);
                            }
                            try {
                                getFragment(0).setCenterData(resp.data);
                                getFragment(2).setCenterData(resp.data);
                                getFragment(3).setCenterData(resp.data);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else if (resp.code == 101) {
                            SpUtil.putBoolean("isLogin", false);
                            startAndClearAll(LoginActivity.class);
                        }
                    }
                });
    }

    private class HomePageAdapter extends FragmentPagerAdapter {


        public HomePageAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        @Override
        public Fragment getItem(int position) {
            return getFragment(position);
        }

        @Override
        public int getCount() {
            return 4;
        }
    }

    private BaseFragment getFragment(int position) {
        BaseFragment fragment = mList.get(position);
        if (null == fragment) {
            switch (position) {
                case 0:
                    mList.put(position, new JzfwFragment());
                    break;
                case 1:
                    mList.put(position, new WdgjFragment());
                    break;
                case 2:
                    mList.put(position, new WdqbFragment());
                    break;
                case 3:
                    mList.put(position, new GrzxFragment());
                    break;
            }
        }
        return mList.get(position);
    }
}
