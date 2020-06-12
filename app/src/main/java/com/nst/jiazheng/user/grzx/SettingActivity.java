package com.nst.jiazheng.user.grzx;

import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.nst.jiazheng.R;
import com.nst.jiazheng.base.BaseToolBarActivity;
import com.nst.jiazheng.base.Layout;
import com.nst.jiazheng.base.SpUtil;
import com.nst.jiazheng.login.LoginActivity;
import com.nst.jiazheng.user.qb.CouponListActivity;
import com.nst.jiazheng.worker.widget.ConfirmWindow;

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
    @BindView(R.id.yhzn)
    TextView yhzn;
    @BindView(R.id.logout)
    TextView logout;

    @Override
    protected void init() {
        setTitle("设置");
        gywm.setOnClickListener(v -> overlay(AboutUsActivity.class));
        yhzn.setOnClickListener(v -> overlay(UserPointActivity.class));
        SpannableString content = new SpannableString("退出登录");
        content.setSpan(new UnderlineSpan(), 0, "退出登录".length(), 0);
        logout.setText(content);
        logout.setOnClickListener(view -> {
            new ConfirmWindow(this)
                    .setContent("确认退出登录？")
                    .setListener((ConfirmWindow window) -> {
                        SpUtil.putBoolean("isLogin", false);
                        startAndClearAll(LoginActivity.class);
                        window.dismiss();
                    })
                    .setPopupGravity(Gravity.CENTER)
                    .setBackPressEnable(true)
                    .setOutSideDismiss(true)
                    .showPopupWindow();

        });
    }
}
