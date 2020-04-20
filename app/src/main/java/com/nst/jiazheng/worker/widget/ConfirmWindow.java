package com.nst.jiazheng.worker.widget;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.nst.jiazheng.R;

import razerdp.basepopup.BasePopupWindow;

/**
 * 创建者     ZhangAnran
 * 创建时间   2020/4/20 10:23 AM
 * 描述	      确认弹窗
 */
public class ConfirmWindow extends BasePopupWindow {
    private View mView;
    private TextView mContentView;
    private TextView mConfirmView;
    private TextView mTitleView;

    public ConfirmWindow setListener(OnConfirmClickListener listener) {
        mConfirmView.setOnClickListener(view -> listener.onConfirmClick(this));
        return this;
    }

    public ConfirmWindow setTitle(String title) {
        mTitleView.setVisibility(View.VISIBLE);
        mTitleView.setText(title);
        return this;
    }

    public ConfirmWindow(Context context) {
        super(context);
    }


    @Override
    public void onViewCreated(View contentView) {
        super.onViewCreated(contentView);
        mConfirmView = contentView.findViewById(R.id.tv_confirm);
        TextView cancel = contentView.findViewById(R.id.tv_cancel);
        mContentView = contentView.findViewById(R.id.tv_content);
        mTitleView = contentView.findViewById(R.id.tv_title);
        cancel.setOnClickListener(view -> {
            this.dismiss();
        });
    }

    @Override
    public View onCreateContentView() {
        mView = createPopupById(R.layout.layout_dialog_confirm);
        return mView;
    }

    public ConfirmWindow setContent(String content) {
        mContentView.setText(content);
        return this;
    }

    public interface OnConfirmClickListener {
        void onConfirmClick(ConfirmWindow confirmWindow);
    }
}
