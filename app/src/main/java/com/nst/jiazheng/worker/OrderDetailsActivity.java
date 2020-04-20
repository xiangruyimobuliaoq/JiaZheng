package com.nst.jiazheng.worker;

import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.nst.jiazheng.R;
import com.nst.jiazheng.base.BaseToolBarActivity;
import com.nst.jiazheng.base.Layout;
import com.nst.jiazheng.worker.widget.ConfirmWindow;

import butterknife.BindView;

/**
 * 创建者     ZhangAnran
 * 创建时间   2020/4/19 10:57 AM
 * 描述	      订单详情
 */

@Layout(layoutId = R.layout.activity_order_details)
public class OrderDetailsActivity extends BaseToolBarActivity {

    public static final String STATUS = "订单状态";
    public static final String DJD = "待接单";
    public static final String DJX = "待进行";
    public static final String JXZ = "进行中";
    public static final String DQR = "待确认";
    public static final String YWC = "已完成";
    @BindView(R.id.tv_confirm)
    TextView mTvConfirm;
    @BindView(R.id.tv_cancel)
    TextView mTvCancel;
    private String mStatus;

    @Override
    protected void init() {
        mStatus = getIntent().getStringExtra(STATUS);
        initView();
        initData();
        initEvent();
    }

    private void initView() {
        setTitle("订单详情");
        mTvCancel.setVisibility(View.GONE);
        switch (mStatus) {
            case DJD:
                mTvConfirm.setText("确认接单");
                break;
            case DJX:
                mTvCancel.setVisibility(View.VISIBLE);
                mTvConfirm.setText("开始服务");
                break;
            case JXZ:
                mTvConfirm.setText("服务结束");
                break;
            case DQR:
                mTvConfirm.setVisibility(View.INVISIBLE);
                break;
            case YWC:
                mTvConfirm.setText("评价用户");
                break;
        }

    }

    private void initData() {

    }

    private void initEvent() {
        mTvConfirm.setOnClickListener(view -> {
            switch (mStatus) {
                case DJD:
                    new ConfirmWindow(this)
                            .setContent("是否确认接单？")
                            .setListener((ConfirmWindow window) -> {
                                window.dismiss();
                                overlay(AddCommentActivity.class);
                            })
                            .setPopupGravity(Gravity.CENTER)
                            .setBackPressEnable(true)
                            .setOutSideDismiss(true)
                            .showPopupWindow();
                    break;
                case DJX:
                    break;
                case JXZ:
                    break;
                case DQR:
                    break;
                case YWC:
                    overlay(AddCommentActivity.class);
                    break;
            }


        });

        mTvCancel.setOnClickListener(view -> {
            if (DJX.equals(mStatus)) {
                new ConfirmWindow(this)
                        .setContent("是否确认取消该订单？")
                        .setListener((ConfirmWindow window) -> {
                            window.dismiss();
                        })
                        .setPopupGravity(Gravity.CENTER)
                        .setBackPressEnable(true)
                        .setOutSideDismiss(true)
                        .showPopupWindow();
            }
        });

    }


}
