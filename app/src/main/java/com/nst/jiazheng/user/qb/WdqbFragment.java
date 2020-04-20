package com.nst.jiazheng.user.qb;

import android.view.View;
import android.widget.TextView;

import com.nst.jiazheng.R;
import com.nst.jiazheng.base.BaseFragment;
import com.nst.jiazheng.base.Layout;

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

    @Override
    protected void init() {
        wdjf.setOnClickListener(v -> {
            overlay(MyPointActivity.class);
        });
        recharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overlay(RechargeActivity.class);
            }
        });
    }
}
