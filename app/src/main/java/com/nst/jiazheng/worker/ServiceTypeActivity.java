package com.nst.jiazheng.worker;

import com.nst.jiazheng.R;
import com.nst.jiazheng.base.BaseActivity;
import com.nst.jiazheng.base.BaseToolBarActivity;
import com.nst.jiazheng.base.Layout;

/**
 * 创建者     ZhangAnran
 * 创建时间   2020/4/18 4:48 PM
 * 描述	      服务类型
 */
@Layout(layoutId = R.layout.activity_service_type)
public class ServiceTypeActivity extends BaseToolBarActivity {
    @Override
    protected void init() {
        initView();
        initData();
    }

    private void initView() {
        setTitle("选择服务");
    }

    private void initData() {


    }


}
