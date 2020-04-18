package com.nst.jiazheng.worker;

import com.nst.jiazheng.R;
import com.nst.jiazheng.base.BaseToolBarActivity;
import com.nst.jiazheng.base.Layout;

/**
 * 创建者     ZhangAnran
 * 创建时间   2020/4/18 4:52 PM
 * 描述	      个人资料
 */

@Layout(layoutId = R.layout.activity_my_profile)
public class MyProfileActivity extends BaseToolBarActivity {
    @Override
    protected void init() {
        initView();

        initData();
    }

    private void initView() {
        setTitle("个人资料");
    }

    private void initData() {

    }
}
