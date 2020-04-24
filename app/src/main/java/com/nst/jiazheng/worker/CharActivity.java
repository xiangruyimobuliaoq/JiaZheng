package com.nst.jiazheng.worker;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.nst.jiazheng.R;
import com.nst.jiazheng.api.Api;
import com.nst.jiazheng.api.resp.Order;
import com.nst.jiazheng.api.resp.Register;
import com.nst.jiazheng.api.resp.Resp;
import com.nst.jiazheng.base.BaseActivity;
import com.nst.jiazheng.base.BaseToolBarActivity;
import com.nst.jiazheng.base.Layout;
import com.nst.jiazheng.base.LogUtil;
import com.nst.jiazheng.base.SpUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 创建者     ZhangAnran
 * 创建时间   2020/4/18 2:38 PM
 * 描述	      对话
 */
@Layout(layoutId = R.layout.activity_chat)
public class CharActivity extends BaseToolBarActivity {
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    private MyAdapter myAdapter;
    private List<Order> items;
    private Register mUserInfo;
    @Override
    protected void init() {
        initView();
        initData();
    }

    private void initData() {
        items = new ArrayList<>();
        mUserInfo = (Register) SpUtil.readObj("userInfo");
        for (int i = 0; i < 5; i++) {
            items.add(new Order());
        }

    }

    private void initView() {
        setTitle("对话");
        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(manager);
        myAdapter = new MyAdapter(R.layout.item_chat, items);
        mRecyclerView.setAdapter(myAdapter);
    }


    class MyAdapter extends BaseQuickAdapter<Order, BaseViewHolder> {

        public MyAdapter(int layoutResId, @Nullable List<Order> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, Order item) {
            helper
                    .setText(R.id.tv_name,item.name)
                    .setText(R.id.tv_content,item.content)
                    ;

        }
    }
}
