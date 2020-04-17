package com.nst.jiazheng.user;

import android.util.Log;
import android.widget.ImageView;
import android.widget.RadioGroup;

import com.nst.jiazheng.R;
import com.nst.jiazheng.base.BaseActivity;
import com.nst.jiazheng.base.BaseFragment;
import com.nst.jiazheng.base.Layout;
import com.nst.jiazheng.base.LockableViewPager;
import com.nst.jiazheng.user.grzx.GrzxFragment;
import com.nst.jiazheng.user.jzfw.JzfwFragment;
import com.nst.jiazheng.user.qb.WdqbFragment;
import com.nst.jiazheng.user.wdgj.WdgjFragment;

import java.util.HashMap;
import java.util.Map;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import butterknife.BindView;

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

    @Override
    protected void init() {
        mList = new HashMap();
        vp.setSwipeLocked(true);
        vp.setAdapter(new HomePageAdapter(getSupportFragmentManager()));
        vp.setOffscreenPageLimit(3);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.jzfw:
                        Log.e("123", "0");
                        vp.setCurrentItem(0);
                        break;
                    case R.id.wdgj:
                        Log.e("123", "1");
                        vp.setCurrentItem(1);
                        break;
                    case R.id.wdqb:
                        Log.e("123", "2");
                        vp.setCurrentItem(2);
                        break;
                    case R.id.grzx:
                        Log.e("123", "3");
                        vp.setCurrentItem(3);
                        break;
                }
            }
        });
        fbwf.setOnClickListener(v -> {
            overlay(SendServeActivity.class);
        });
    }

    private class HomePageAdapter extends FragmentPagerAdapter {

        public HomePageAdapter(FragmentManager fm) {
            super(fm);
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

    private Fragment getFragment(int position) {
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
