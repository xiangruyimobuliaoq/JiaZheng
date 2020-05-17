package com.nst.jiazheng.user.grzx;

import android.widget.Button;

import com.nst.jiazheng.R;
import com.nst.jiazheng.base.BaseToolBarActivity;
import com.nst.jiazheng.base.Layout;

import butterknife.BindView;

/**
 * 创建者     彭龙
 * 创建时间   2020/5/4 10:54 AM
 * 描述
 * <p>
 * 更新者     $
 * 更新时间   $
 * 更新描述
 */
@Layout(layoutId = R.layout.activity_share)
public class ShareActivity extends BaseToolBarActivity {
    @BindView(R.id.submit)
    Button submit;

    @Override
    protected void init() {
        setTitle("推荐分享");
    }
}