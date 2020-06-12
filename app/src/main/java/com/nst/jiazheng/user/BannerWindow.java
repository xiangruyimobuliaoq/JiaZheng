package com.nst.jiazheng.user;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseBinderAdapter;
import com.nst.jiazheng.R;
import com.nst.jiazheng.api.resp.UpFile;
import com.nst.jiazheng.worker.MainActivity;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import cn.bingoogolapple.bgabanner.BGABanner;
import razerdp.basepopup.BasePopupWindow;

/**
 * 创建者     彭龙
 * 创建时间   2020/6/12 9:49 AM
 * 描述
 * <p>
 * 更新者     $
 * 更新时间   $
 * 更新描述
 */
class BannerWindow extends BasePopupWindow {
    private List<UpFile> links;

    private BGABanner mBanner;

    public BannerWindow(Context context) {
        super(context);
    }

    @Override
    public View onCreateContentView() {
        return createPopupById(R.layout.window_banner);
    }

    public BannerWindow setData(List<UpFile> links) {
        this.links = links;
        mBanner.setAutoPlayAble(links.size() > 1);
        ArrayList<String> title = new ArrayList<>();
        for (UpFile upFile :
                links) {
            title.add(upFile.title);
        }
        mBanner.setData(links, title);
        return this;
    }

    @Override
    public void onViewCreated(View contentView) {
        super.onViewCreated(contentView);
        mBanner = contentView.findViewById(R.id.banner);
        mBanner.setAdapter(new BannerAdapter());
    }

    class BannerAdapter implements BGABanner.Adapter<ImageView, UpFile> {

        @Override
        public void fillBannerItem(BGABanner banner, ImageView itemView, @Nullable UpFile model, int position) {
            try {
                Glide.with(getContext())
                        .load(model.img)
                        .centerInside()
                        .dontAnimate()
                        .into(itemView);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
