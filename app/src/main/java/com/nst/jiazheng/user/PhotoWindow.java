package com.nst.jiazheng.user;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.nst.jiazheng.R;

import razerdp.basepopup.BasePopupWindow;

/**
 * 创建者     彭龙
 * 创建时间   2020/5/6 4:14 PM
 * 描述
 * <p>
 * 更新者     $
 * 更新时间   $
 * 更新描述
 */
public class PhotoWindow extends BasePopupWindow {
    private View mView;
    private TextView mContentView;
    private TextView mConfirmView;
    private TextView mTitleView;

    public PhotoWindow setListener(OnConfirmClickListener listener) {
        mConfirmView.setOnClickListener(view -> listener.onConfirmClick(this));
        return this;
    }

    public PhotoWindow setTitle(String title) {
        mTitleView.setVisibility(View.VISIBLE);
        mTitleView.setText(title);
        return this;
    }

    public PhotoWindow(Context context) {
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
        mView = createPopupById(R.layout.window_photo);
        return mView;
    }

    public PhotoWindow setContent(String content) {
        mContentView.setText(content);
        return this;
    }

    public PhotoWindow setContent(String content, String confirmText) {
        mContentView.setText(content);
        mConfirmView.setText(confirmText);
        return this;
    }

    public interface OnConfirmClickListener {
        void onConfirmClick(PhotoWindow confirmWindow);
    }
}
