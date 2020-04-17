package com.nst.jiazheng.user.grzx;

import android.widget.LinearLayout;
import android.widget.TextView;

import com.nst.jiazheng.R;
import com.nst.jiazheng.base.BaseToolBarActivity;
import com.nst.jiazheng.base.Layout;

import butterknife.BindView;

/**
 * 创建者     彭龙
 * 创建时间   2020/4/11 12:45 PM
 * 描述
 * <p>
 * 更新者     $
 * 更新时间   $
 * 更新描述
 */
@Layout(layoutId = R.layout.activity_setting)
public class SettingActivity extends BaseToolBarActivity {
    @BindView(R.id.gywm)
    TextView gywm;


    @Override
    protected void init() {
        setTitle("设置");
        gywm.setOnClickListener(v -> {
            overlay(AboutUsActivity.class);
        });

    }
}
