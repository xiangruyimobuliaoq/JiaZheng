package com.nst.jiazheng.user.grzx;

import android.widget.ImageView;

import com.nst.jiazheng.R;
import com.nst.jiazheng.base.BaseToolBarActivity;
import com.nst.jiazheng.base.Layout;

import butterknife.BindView;

/**
 * 创建者     彭龙
 * 创建时间   2020/5/6 4:14 PM
 * 描述
 * <p>
 * 更新者     $
 * 更新时间   $
 * 更新描述
 */
@Layout(layoutId = R.layout.activity_applysubmit)
public class ApplySubmitActivity extends BaseToolBarActivity {
    @BindView(R.id.takepicture)
    ImageView takepicture;

    @Override
    protected void init() {
        setTitle("私人管家认证");
        takepicture.setOnClickListener(view -> {

        });
    }
}
