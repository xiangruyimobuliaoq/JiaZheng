package com.nst.jiazheng.user.grzx;

import android.widget.Button;
import android.widget.TextView;

import com.nst.jiazheng.R;
import com.nst.jiazheng.base.BaseToolBarActivity;
import com.nst.jiazheng.base.Layout;

import butterknife.BindView;

/**
 * 创建者     彭龙
 * 创建时间   2020/4/28 3:11 PM
 * 描述
 * <p>
 * 更新者     $
 * 更新时间   $
 * 更新描述
 */
@Layout(layoutId = R.layout.activity_workerapply)
public class WorkerApplyActivity extends BaseToolBarActivity {
    @BindView(R.id.xieyi)
    TextView xieyi;
    @BindView(R.id.submit)
    Button submit;

    @Override
    protected void init() {
        setTitle("私人管家认证");
        xieyi.setOnClickListener(view -> {
            overlay(WorkerAgreementActivity.class);
        });
        submit.setOnClickListener(view -> {

        });
    }
}
