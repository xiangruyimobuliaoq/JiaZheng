package com.nst.jiazheng.worker;

import android.os.Bundle;
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
import com.nst.jiazheng.api.resp.Worker;
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

    private List<Order> items;
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
                getOrderList(position);
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
        myAdapter = new MyAdapter(R.layout.item_worker_home, items);
        mRecyclerView.setAdapter(myAdapter);
    }


    private void initData() {
        mUserInfo = (Register) SpUtil.readObj("userInfo");
        int status = 2;
        getOrderList(status);
    }

    private void getOrderList(int status) {
        OkGo.<String>post(Api.orderApi)
                .params("api_name", "order_list_app")
                .params("token", mUserInfo.token)
                .params("status", status)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LogUtil.getInstance().d("order_list_app : " + response.body());
                        Resp<List<Order>> resp = new Gson().fromJson(response.body(),
                                new TypeToken<Resp<List<Order>>>() {
                                }.getType());
                        if (resp.code == 1) {
                            List<Order> data = resp.data;
                            myAdapter.setNewInstance(data);
                        }
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
            int position = helper.getLayoutPosition();
            String typeName = item.serve_type_name;
            String address = item.address;
            int num = item.num;
            String time = item.time;
            String countPrice = item.pay_price;
            String units = item.serve_type_units;
            helper.setText(R.id.tv_title, typeName)
                    .setText(R.id.tv_ads, address)
                    .setText(R.id.tv_num, num + "")
                    .setText(R.id.tv_count_price, countPrice)
                    .setText(R.id.tv_time, time)
                    .setText(R.id.tv_units, units);

        }
    }


}
