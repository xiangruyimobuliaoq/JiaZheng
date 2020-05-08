package com.nst.jiazheng.worker;

import com.nst.jiazheng.R;
import com.nst.jiazheng.base.BaseToolBarActivity;
import com.nst.jiazheng.base.Layout;

/**
 * 创建者     ZhangAnran
 * 创建时间   2020/4/19 6:05 PM
 * 描述	      评价
 */
@Layout(layoutId = R.layout.activity_add_comment)
public class AddCommentActivity extends BaseToolBarActivity {
    @Override
    protected void init() {
        initView();
        initData();
        initEvent();
    }

    private void initView() {
        setTitle("评价");
    }

    private void initData() {

    }

    private void initEvent() {

    }
}
