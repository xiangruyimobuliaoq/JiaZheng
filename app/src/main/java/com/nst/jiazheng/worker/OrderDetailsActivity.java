package com.nst.jiazheng.worker;

import android.view.View;
import android.widget.TextView;

import com.nst.jiazheng.R;
import com.nst.jiazheng.base.BaseToolBarActivity;
import com.nst.jiazheng.base.Layout;

import butterknife.BindView;

/**
 * 创建者     ZhangAnran
 * 创建时间   2020/4/19 10:57 AM
 * 描述	      订单详情
 */

@Layout(layoutId = R.layout.activity_order_details)
public class OrderDetailsActivity extends BaseToolBarActivity {

    @BindView(R.id.tv_confirm)
    TextView mTvConfirm;

    @Override
    protected void init() {

        initView();
        initEvent();
    }

    private void initEvent() {
        mTvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overlay(AddCommentActivity.class);
            }
        });

    }

    private void initView() {
        setTitle("订单详情");
    }
}
