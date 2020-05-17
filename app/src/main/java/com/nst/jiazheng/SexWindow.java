package com.nst.jiazheng;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

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
public class SexWindow extends BasePopupWindow {
    private View mView;
    private TextView gallery;
    private TextView cancel;
    private TextView mCam;

    public SexWindow(Context context) {
        super(context);
    }

    public SexWindow setListener(OnConfirmClickListener listener) {
        mCam.setOnClickListener(view -> listener.onSex(this, 1));
        gallery.setOnClickListener(view -> listener.onSex(this, 2));
        return this;
    }

    @Override
    public void onViewCreated(View contentView) {
        super.onViewCreated(contentView);
        cancel = contentView.findViewById(R.id.cancel);
        mCam = contentView.findViewById(R.id.cam);
        gallery = contentView.findViewById(R.id.gallery);
        cancel.setOnClickListener(view -> {
            this.dismiss();
        });
    }

    @Override
    public View onCreateContentView() {
        mView = createPopupById(R.layout.window_sex);
        return mView;
    }

    public interface OnConfirmClickListener {
        void onSex(SexWindow confirmWindow, int sex);
    }
}
