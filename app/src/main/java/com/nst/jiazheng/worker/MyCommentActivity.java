package com.nst.jiazheng.worker;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.nst.jiazheng.R;
import com.nst.jiazheng.api.resp.Register;
import com.nst.jiazheng.base.BaseToolBarActivity;
import com.nst.jiazheng.base.Layout;
import com.nst.jiazheng.base.LogUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 创建者     ZhangAnran
 * 创建时间   2020/4/18 4:48 PM
 * 描述	      服务类型
 */
@Layout(layoutId = R.layout.activity_my_comment)
public class MyCommentActivity extends BaseToolBarActivity {
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    private MyAdapter myAdapter;
    private List<Register> items;

    @Override
    protected void init() {
        initView();
        initData();
        initEvent();
    }

    private void initEvent() {
        myAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                overlay(CommentDetailsActivity.class);
            }
        });
    }

    private void initView() {

        setTitle("我的评价");
        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(manager);
        myAdapter = new MyAdapter(R.layout.item_my_comment, items);
        mRecyclerView.setAdapter(myAdapter);
    }

    private void initData() {
        items = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            items.add(new Register());
        }
        myAdapter.setNewInstance(items);

    }

    class MyAdapter extends BaseQuickAdapter<Register, BaseViewHolder> {

        public MyAdapter(int layoutResId, @Nullable List<Register> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, Register item) {
            int position = helper.getLayoutPosition();
            LogUtil.getInstance().d("position  :: " + position);

        }
    }

}
