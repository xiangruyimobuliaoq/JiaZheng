package com.nst.jiazheng.worker;

import com.nst.jiazheng.R;
import com.nst.jiazheng.base.BaseActivity;
import com.nst.jiazheng.base.BaseToolBarActivity;
import com.nst.jiazheng.base.Layout;

/**
 * 创建者     ZhangAnran
 * 创建时间   2020/4/18 2:33 PM
 * 描述	      提现
 */
@Layout(layoutId = R.layout.activity_draw)
public class WithdrawActivity extends BaseToolBarActivity {
    @Override
    protected void init() {

        initView();
    }

    private void initView() {
        setTitle("提现");
    }
}
