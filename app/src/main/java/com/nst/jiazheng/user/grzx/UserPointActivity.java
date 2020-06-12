package com.nst.jiazheng.user.grzx;

import android.os.Bundle;
import android.view.View;

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
import com.nst.jiazheng.api.resp.Question;
import com.nst.jiazheng.api.resp.Resp;
import com.nst.jiazheng.api.resp.UserCenter;
import com.nst.jiazheng.base.BaseToolBarActivity;
import com.nst.jiazheng.base.Layout;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;

/**
 * 创建者     彭龙
 * 创建时间   2020/6/10 5:39 PM
 * 描述
 * <p>
 * 更新者     $
 * 更新时间   $
 * 更新描述
 */
@Layout(layoutId = R.layout.activity_userpoint)
public class UserPointActivity extends BaseToolBarActivity {
    @BindView(R.id.list)
    RecyclerView list;
    private PointAdapter mAdapter;

    @Override
    protected void init() {
        setTitle("用户指南");
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(RecyclerView.VERTICAL);
        list.setLayoutManager(manager);
        mAdapter = new PointAdapter(R.layout.item_question, null);
        list.setAdapter(mAdapter);
        getPointList();
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                Question question = mAdapter.getData().get(position);
                Bundle bundle = new Bundle();
                bundle.putInt("id", question.id);
                bundle.putString("title", question.title);
                overlay(PointDetailActivity.class,bundle);
            }
        });
    }

    private void getPointList() {
        OkGo.<String>post(Api.publicApi).params("api_name", "qustion_list").execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                Resp<List<Question>> resp = new Gson().fromJson(response.body(), new TypeToken<Resp<List<Question>>>() {
                }.getType());
                if (resp.code == 1) {
                    mAdapter.setList(resp.data);
                }
            }
        });
    }

    class PointAdapter extends BaseQuickAdapter<Question, BaseViewHolder> {

        public PointAdapter(int layoutResId, @Nullable List<Question> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(@NotNull BaseViewHolder baseViewHolder, Question question) {
            baseViewHolder.setText(R.id.title, question.title);
        }
    }
}
