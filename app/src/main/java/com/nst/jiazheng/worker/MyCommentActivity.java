package com.nst.jiazheng.worker;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
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
import com.nst.jiazheng.base.BaseToolBarActivity;
import com.nst.jiazheng.base.Layout;
import com.nst.jiazheng.base.LogUtil;
import com.nst.jiazheng.base.SpUtil;
import com.nst.jiazheng.base.ToastHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    private List<Order> items;
    private Register mUserInfo;

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
        mUserInfo = (Register) SpUtil.readObj("userInfo");
        items = new ArrayList<>();
//        for (int i = 0; i < 5; i++) {
//            items.add(new Register());
//        }
        OkGo.<String>post(Api.userApi)
                .params("api_name", "comment_list")
                .params("token", mUserInfo.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LogUtil.getInstance().d("comment_list : " + response.body());
                        Resp<List<Order>> resp = new Gson().fromJson(response.body(),
                                new TypeToken<Resp<List<Order>>>() {
                                }.getType());
                        if (resp.code == 1) {
                            List<Order> data = resp.data;
                            myAdapter.setNewInstance(data);
                        }
                        ToastHelper.showToast(resp.msg, mContext);
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                    }
                });

    }

    class MyAdapter extends BaseQuickAdapter<Order, BaseViewHolder> {

        public MyAdapter(int layoutResId, @Nullable List<Order> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, Order item) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            helper
                    .setText(R.id.tv_name, item.name)
                    .setText(R.id.tv_content, item.content)
                    .setText(R.id.tv_time, format.format(new Date(item.ctime)))
            ;
        }
    }

}
