package com.nst.jiazheng.worker;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import java.lang.reflect.Field;
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
    private int mSelectTabIndex;

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
                Bundle bundle = new Bundle();
                String status = OrderDetailsActivity.DJD;
                switch (mSelectTabIndex) {
                    case 0:
                        status = OrderDetailsActivity.DJD;
                        break;
                    case 1:
                        status = OrderDetailsActivity.DJX;
                        break;
                    case 2:
                        status = OrderDetailsActivity.JXZ;
                        break;
                    case 3:
                        status = OrderDetailsActivity.DQR;
                        break;
                    case 4:
                        status = OrderDetailsActivity.YWC;
                        break;
                    default:
                        break;
                }
                bundle.putString(OrderDetailsActivity.STATUS, status);
                overlay(OrderDetailsActivity.class, bundle);
            }
        });
        mTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                mSelectTabIndex = position;
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
