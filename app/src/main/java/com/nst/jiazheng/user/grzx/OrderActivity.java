package com.nst.jiazheng.user.grzx;

import com.google.android.material.tabs.TabLayout;
import com.nst.jiazheng.R;
import com.nst.jiazheng.base.BaseToolBarActivity;
import com.nst.jiazheng.base.Layout;
import com.nst.jiazheng.base.LockableViewPager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import butterknife.BindView;

/**
 * 创建者     彭龙
 * 创建时间   2020/4/25 3:15 PM
 * 描述
 * <p>
 * 更新者     $
 * 更新时间   $
 * 更新描述
 */
@Layout(layoutId = R.layout.activity_order)
public class OrderActivity extends BaseToolBarActivity {
    @BindView(R.id.tab)
    TabLayout mTab;
    @BindView(R.id.vp)
    LockableViewPager vp;

    @Override
    protected void init() {
        setTitle("我的订单");
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

        vp.setSwipeLocked(true);
        vp.setAdapter(new OrderPageAdapter(getSupportFragmentManager(), 1));
        vp.setOffscreenPageLimit(5);
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
            return 6;
        }
    }

}
