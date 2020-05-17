package com.nst.jiazheng.user.jzfw;

import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.nst.jiazheng.R;
import com.nst.jiazheng.api.resp.ServeType;
import com.nst.jiazheng.api.resp.Worker;

import java.util.List;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import razerdp.basepopup.BasePopupWindow;

/**
 * 创建者     彭龙
 * 创建时间   2020/4/11 3:22 PM
 * 描述
 * <p>
 * 更新者     $
 * 更新时间   $
 * 更新描述
 */
public class MarkerInfoWindow extends BasePopupWindow implements View.OnClickListener {

    private CircleImageView mTx;
    private TextView mCounts;
    private TextView mPoint;
    private TextView mBtn;
    private RecyclerView mTypelist;
    private MarkInfoCallback callback;
    private TextView nickname;

    public MarkerInfoWindow(JzfwFragment jzfwFragment) {
        super(jzfwFragment);
    }

    @Override
    public View onCreateContentView() {
        View view = createPopupById(R.layout.window_markerinfo);
        return view;
    }

    public MarkerInfoWindow initView(Worker worker) {
        mTx = findViewById(R.id.tx);
        mCounts = findViewById(R.id.counts);
        mPoint = findViewById(R.id.point);
        mBtn = findViewById(R.id.btn);
        mTypelist = findViewById(R.id.typelist);
        nickname = findViewById(R.id.nickname);
        mCounts.setText(worker.OrderCount + "单");
        mPoint.setText(worker.score + "分");
        nickname.setText(worker.name);
        GridLayoutManager manager = new GridLayoutManager(getContext(), 4);
        mTypelist.setLayoutManager(manager);
        TypeAdapter adapter = new TypeAdapter(R.layout.item_servetype, worker.serve_type);
        mTypelist.setAdapter(adapter);
        mBtn.setOnClickListener(this);
        try {
            Glide.with(getContext()).load(worker.logo).error(R.mipmap.ic_tx).into(mTx);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    @Override
    public void onClick(View view) {
        if (null != callback) {
            callback.callBack();
        }
    }

    public MarkerInfoWindow setCallback(MarkInfoCallback callback) {
        this.callback = callback;
        return this;
    }

    class TypeAdapter extends BaseQuickAdapter<ServeType, BaseViewHolder> {

        public TypeAdapter(int layoutResId, List<ServeType> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder baseViewHolder, ServeType serveType) {
            baseViewHolder.setText(R.id.text, serveType.title);
        }
    }

    interface MarkInfoCallback {
        void callBack();
    }
}
