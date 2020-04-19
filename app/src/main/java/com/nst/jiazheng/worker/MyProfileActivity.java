package com.nst.jiazheng.worker;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nst.jiazheng.R;
import com.nst.jiazheng.base.BaseToolBarActivity;
import com.nst.jiazheng.base.Layout;

import butterknife.BindView;

/**
 * 创建者     ZhangAnran
 * 创建时间   2020/4/18 4:52 PM
 * 描述	      个人资料
 */

@Layout(layoutId = R.layout.activity_my_profile)
public class MyProfileActivity extends BaseToolBarActivity {
    @BindView(R.id.ll_age)
    LinearLayout mSelectAge;
    @BindView(R.id.ll_sex)
    LinearLayout mSelectSex;
    @BindView(R.id.ll_city)
    LinearLayout mSelectCity;
    @BindView(R.id.ll_long)
    LinearLayout mSelectLong;

    @BindView(R.id.tv_age)
    TextView mTvAge;
    @BindView(R.id.tv_sex)
    TextView mTvSex;
    @BindView(R.id.tv_city)
    TextView mTvCity;
    @BindView(R.id.tv_long)
    TextView mTvLong;

    @Override
    protected void init() {
        initView();
        initData();
        initEvent();
    }

    private void initView() {
        setTitle("个人资料");
    }

    private void initData() {
        mSelectAge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overlay(InputAgeActivity.class);
            }
        });

        mSelectSex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        mSelectCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        mSelectLong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


    }

    private void initEvent() {


    }
}
