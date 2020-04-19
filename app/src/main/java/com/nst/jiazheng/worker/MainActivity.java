package com.nst.jiazheng.worker;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.google.android.material.tabs.TabLayout;
import com.nst.jiazheng.R;
import com.nst.jiazheng.api.resp.Register;
import com.nst.jiazheng.base.BaseToolBarActivity;
import com.nst.jiazheng.base.Layout;
import com.nst.jiazheng.base.LogUtil;
import com.nst.jiazheng.base.SpUtil;
import com.nst.jiazheng.base.ToastHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 创建者     彭龙
 * 创建时间   2020/4/8 2:15 PM
 * 描述
 * <p>
 * 更新者     $
 * 更新时间   $
 * 更新描述
 */
@Layout(layoutId = R.layout.activity_main)
public class MainActivity extends BaseToolBarActivity {
    @BindView(R.id.item)
    ImageView mIvChat;
    @BindView(R.id.tab)
    TabLayout mTab;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.tv_withDraw)
    TextView mTvWithDraw;
    @BindView(R.id.tv_withDrawRecord)
    TextView mTvWithDrawRecord;
    @BindView(R.id.tv_service)
    TextView mTvServiceType;
    @BindView(R.id.tv_comment)
    TextView mTvComment;
    @BindView(R.id.tv_my)
    TextView mTvMy;

    private List<Register> items;
    private MyAdapter myAdapter;
    private Register mUserInfo;

    @Override
    protected void init() {
        initView();
        initData();
        initEvent();
    }

    private void initEvent() {
        mIvChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overlay(CharActivity.class);
            }
        });

        mTvWithDraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overlay(WithdrawActivity.class);
            }
        });

        mTvWithDrawRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overlay(WithdrawalsRecordActivity.class);
            }
        });

        mTvMy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overlay(MyProfileActivity.class);
            }
        });

        mTvComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overlay(MyCommentActivity.class);
            }
        });

        mTvServiceType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overlay(ServiceTypeActivity.class);
            }
        });

        myAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                overlay(OrderDetailsActivity.class);
            }
        });
        mTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                ToastHelper.showToast("addOnTabSelectedListener : " + position, mContext);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void initView() {
        setTitle("管家首页");
        mIvChat.setImageResource(R.mipmap.ic_xiaox);
        items = new ArrayList<>();
        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(manager);
        myAdapter = new MyAdapter(R.layout.item_worker, items);
        mRecyclerView.setAdapter(myAdapter);
    }


    private void initData() {
        mUserInfo = (Register) SpUtil.readObj("userInfo");
//        OkGo.<String>post(Api.serverApi)
//                .params("api_name", "server_type_app")
//                .params("token", mUserInfo.token)
////                .params("type", worker.type)
////                .params("id", worker.id)
//                .execute(new StringCallback() {
//            @Override
//            public void onSuccess(Response<String> response) {
//                Resp<List<ServeType>> resp = new Gson().fromJson(response.body(), new
//                TypeToken<Resp<List<ServeType>>>() {
//                }.getType());
//                if (resp.code == 1) {
//                    myAdapter.setNewInstance(resp.data);
//                }
//            }
//        });
        for (int i = 0; i < 10; i++) {
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
            helper.setText(R.id.tv_title, "MyAdapter");

        }
    }


}
