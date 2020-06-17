package com.nst.jiazheng.worker;

import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.nst.jiazheng.R;
import com.nst.jiazheng.api.Api;
import com.nst.jiazheng.api.resp.Comment;
import com.nst.jiazheng.api.resp.Message;
import com.nst.jiazheng.api.resp.Register;
import com.nst.jiazheng.api.resp.Resp;
import com.nst.jiazheng.base.BaseToolBarActivity;
import com.nst.jiazheng.base.Layout;
import com.nst.jiazheng.base.SpUtil;
import com.nst.jiazheng.login.LoginActivity;
import com.nst.jiazheng.user.grzx.CommentActivity;
import com.nst.jiazheng.user.grzx.CommentDetailActivity;
import com.nst.jiazheng.user.wdgj.WorkerInfoActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;
import per.wsj.library.AndRatingBar;

/**
 * 创建者     彭龙
 * 创建时间   2020/4/24 2:53 PM
 * 描述
 * <p>
 * 更新者     $
 * 更新时间   $
 * 更新描述
 */
@Layout(layoutId = R.layout.activity_comment)
public class MyCommentActivity extends BaseToolBarActivity {
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.tab)
    TabLayout mTab;
    private Register mUserInfo;
    private CommentAdapter mAdapter;
    int currentTpye = 2;

    @Override
    protected void init() {
        setTitle("我的评价");
        mUserInfo = (Register) SpUtil.readObj("userInfo");
        mTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if (position == 0) {
                    currentTpye = 2;
                } else {
                    currentTpye = 1;
                }
                getCommentList();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(manager);
        mAdapter = new CommentAdapter(R.layout.item_comment_padding, null);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
//            Bundle params = new Bundle();
//            params.putSerializable("data", mAdapter.getData().get(position));
//            overlay(CommentDetailActivity.class, params);
            Bundle bundle = new Bundle();
            bundle.putString("id", mAdapter.getData().get(position).order_id);
            overlay(YiwanchengActivity.class, bundle);
        });
        getCommentList();
    }

    private void getCommentList() {
        OkGo.<String>post(Api.userApi).params("api_name", "comment_list")
                .params("token", mUserInfo.token)
                .params("type", currentTpye)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Resp<List<Comment>> resp = new Gson().fromJson(response.body(), new TypeToken<Resp<List<Comment>>>() {
                        }.getType());
                        if (resp.code == 1) {
                            mAdapter.setList(resp.data);
                        } else if (resp.code == 101) {
                            SpUtil.putBoolean("isLogin", false);
                            startAndClearAll(LoginActivity.class);
                        }
                    }
                });
    }


    class CommentAdapter extends BaseQuickAdapter<Comment, BaseViewHolder> {

        public CommentAdapter(int layoutResId, List<Comment> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder baseViewHolder, Comment comment) {
            baseViewHolder
                    .setText(R.id.name, comment.name)
                    .setText(R.id.content, comment.content)
                    .setText(R.id.order_no, comment.order_no)
                    .setText(R.id.ctime, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(comment.ctime * 1000)));
            CircleImageView tx = baseViewHolder.getView(R.id.tx);
//            baseViewHolder.getView(R.id.orderpart).setOnClickListener(view -> {
//                Bundle bundle = new Bundle();
//                bundle.putString("id", comment.order_id);
//                overlay(YiwanchengActivity.class, bundle);
//            });
            AndRatingBar score = baseViewHolder.getView(R.id.score);
            score.setRating(Float.parseFloat(comment.score));
            try {
                Glide.with(MyCommentActivity.this).load(comment.headimgurl).error(R.mipmap.ic_tx).into(tx);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
