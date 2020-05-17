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
    private TextView gallery;
    private TextView cancel;
    private TextView mCam;

    public PhotoWindow(Context context) {
        super(context);
    }

    public PhotoWindow setListener(OnConfirmClickListener listener) {
        mCam.setOnClickListener(view -> listener.onCam(this));
        gallery.setOnClickListener(view -> listener.onGallery(this));
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
        mView = createPopupById(R.layout.window_photo);
        return mView;
    }

    public interface OnConfirmClickListener {
        void onCam(PhotoWindow confirmWindow);

        void onGallery(PhotoWindow confirmWindow);
    }
}
