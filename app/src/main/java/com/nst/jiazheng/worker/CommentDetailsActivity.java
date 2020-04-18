package com.nst.jiazheng.worker;

import com.nst.jiazheng.R;
import com.nst.jiazheng.base.BaseToolBarActivity;
import com.nst.jiazheng.base.Layout;

/**
 * 创建者     ZhangAnran
 * 创建时间   2020/4/18 5:45 PM
 * 描述	      评价详情
 */
@Layout(layoutId = R.layout.activity_comment_details)
public class CommentDetailsActivity extends BaseToolBarActivity {
    @Override
    protected void init() {
        iniView();
    }

    private void iniView() {
        setTitle("评价详情");
    }
}
