package com.nst.jiazheng.user.grzx;

import android.os.Bundle;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.chad.library.adapter.base.listener.OnLoadMoreListener;
import com.chad.library.adapter.base.module.BaseLoadMoreModule;
import com.chad.library.adapter.base.module.LoadMoreModule;
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
import com.nst.jiazheng.base.BaseFragment;
import com.nst.jiazheng.base.Layout;
import com.nst.jiazheng.base.SpUtil;
import com.nst.jiazheng.login.LoginActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;

/**
 * 创建者     彭龙
 * 创建时间   2020/4/25 3:51 PM
 * 描述
 * <p>
 * 更新者     $
 * 更新时间   $
 * 更新描述
 */
@Layout(layoutId = R.layout.fragment_order)
public class OrderFragment extends BaseFragment implements OnLoadMoreListener {
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    String type;
    private OrderAdapter mAdapter;
    private Register mUserInfo;
    private BaseLoadMoreModule mLoadMoreModule;

    public OrderFragment(int type) {
        super();
        switch (type) {
            case 0:
                this.type = "";
                break;
            case 1:
                this.type = "2";
                break;
            case 2:
                this.type = "3";
                break;
            case 3:
                this.type = "4";
                break;
            case 4:
                this.type = "7";
                break;
            case 5:
                this.type = "6";
                break;
        }
    }

    @Override
    protected void init() {
        mUserInfo = (Register) SpUtil.readObj("userInfo");
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(RecyclerView.VERTICAL);
        mRecyclerView.setLayoutManager(manager);
        mAdapter = new OrderAdapter(null);
        mRecyclerView.setAdapter(mAdapter);
        mLoadMoreModule = mAdapter.getLoadMoreModule();
        mLoadMoreModule.setEnableLoadMoreIfNotFullPage(false);
        mLoadMoreModule.setOnLoadMoreListener(this);
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            Order order = (Order) adapter.getData().get(position);
            Bundle bundle = new Bundle();
            bundle.putString("id", order.id);
            switch (order.status) {
                case 1:
                    overlay(OrderDetailsDaizhifuActivity.class, bundle);
                    break;
                case 2:
                case 5:
                    overlay(OrderDetailsDaijiedanActivity.class, bundle);
                    break;
                case 3:
                    overlay(OrderDetailsYijiedanActivity.class, bundle);
                    break;
                case 4:
                    overlay(OrderDetailsJinxingzhongActivity.class, bundle);
                    break;
                case 6:
                    overlay(OrderDetailsYiwanchengActivity.class, bundle);
                    break;
                case 7:
                    overlay(OrderDetailsDaiquerenActivity.class, bundle);
                    break;
                case -1:
                case -2:
                    overlay(OrderDetailsYiquxiaoActivity.class, bundle);
                    break;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getOrderList(false);
    }

    private void getOrderList(boolean loadMore) {
        showDialog("加载中", true);
        if (loadMore) {
            OkGo.<String>post(Api.orderApi).params("api_name", "order_list")
                    .params("token", mUserInfo.token)
                    .params("status", type)
                    .params("page", mAdapter.getData().size() / 10 + 1)
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(Response<String> response) {
                            dismissDialog();
                            Resp<List<Order>> resp = new Gson().fromJson(response.body(), new TypeToken<Resp<List<Order>>>() {
                            }.getType());
                            if (resp.code == 1) {
                                if (resp.data.size() == 10) {
                                    mLoadMoreModule.loadMoreComplete();
                                } else {
                                    mLoadMoreModule.loadMoreEnd();
                                }
                                mAdapter.addData(resp.data);
                            } else if (resp.code == 101) {
                                SpUtil.putBoolean("isLogin", false);
                                startAndClearAll(LoginActivity.class);
                            }
                        }

                        @Override
                        public void onError(Response<String> response) {
                            super.onError(response);
                            mLoadMoreModule.loadMoreFail();
                            dismissDialog();
                        }
                    });
        } else {
            OkGo.<String>post(Api.orderApi).params("api_name", "order_list")
                    .params("token", mUserInfo.token)
                    .params("status", type)
                    .params("page", 1)
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(Response<String> response) {
                            dismissDialog();
                            Resp<List<Order>> resp = new Gson().fromJson(response.body(), new TypeToken<Resp<List<Order>>>() {
                            }.getType());
                            if (resp.code == 1) {
                                if (resp.data.size() < 10) {
                                    mLoadMoreModule.loadMoreEnd();
                                }
                                mAdapter.setList(resp.data);
                            } else if (resp.code == 101) {
                                SpUtil.putBoolean("isLogin", false);
                                startAndClearAll(LoginActivity.class);
                            }
                        }

                        @Override
                        public void onError(Response<String> response) {
                            super.onError(response);
                            showDialog("加载中", true);
                        }
                    });
        }
    }

    @Override
    public void onLoadMore() {
        if (mAdapter.getData().size() < 10) {
            mLoadMoreModule.loadMoreEnd();
            return;
        }
        getOrderList(true);
    }

    class OrderAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> implements LoadMoreModule {

        public OrderAdapter(List<MultiItemEntity> data) {
            super(data);
            addItemType(1, R.layout.item_order_daizhifu);
            addItemType(2, R.layout.item_order_daijiedan);
            addItemType(3, R.layout.item_order_yijiedan);
            addItemType(4, R.layout.item_order_jinxingzhong);
            addItemType(5, R.layout.item_order_daijiedan);
            addItemType(6, R.layout.item_order_wancheng);
            addItemType(7, R.layout.item_order_daiqueren);
            addItemType(-1, R.layout.item_order_tuikuan);
            addItemType(-2, R.layout.item_order_quxiao);
        }

        @Override
        protected void convert(BaseViewHolder baseViewHolder, MultiItemEntity multiItemEntity) {
            Order order = (Order) multiItemEntity;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            baseViewHolder.setText(R.id.order_no, "订单编号: " + order.order_no)
                    .setText(R.id.status, order.StatusText)
                    .setText(R.id.serve_type_name, "服务类型: " + order.serve_type_name)
                    .setText(R.id.num, "数量: " + order.num)
                    .setText(R.id.address, "服务地址: " + order.address)
                    .setText(R.id.time, "预约时间: " + order.time)
                    .setText(R.id.pay_price, "¥ " + order.pay_price)
                    .setText(R.id.serve_type_price, "服务单价: ¥ " + order.serve_type_price + " /" + order.serve_type_units);
            switch (baseViewHolder.getItemViewType()) {
                case 3:
                    baseViewHolder.setText(R.id.staff_name, "接单管家: " + order.staff_name)
                            .setText(R.id.jie_time, "接单时间: " + format.format(new Date(order.jie_time * 1000)));
                    break;
                case 4:
                    baseViewHolder.setText(R.id.staff_name, "接单管家: " + order.staff_name)
                            .setText(R.id.jie_time, "接单时间: " + format.format(new Date(order.jie_time * 1000)))
                            .setText(R.id.start_time, "开始服务时间: " + format.format(new Date(order.start_time * 1000)));
                    break;
                case 6:
                    baseViewHolder.setText(R.id.staff_name, "接单管家: " + order.staff_name)
                            .setText(R.id.jie_time, "接单时间: " + format.format(new Date(order.jie_time * 1000)))
                            .setText(R.id.etime, "结束服务时间: " + format.format(new Date(order.petime * 1000)))
                            .setText(R.id.petime, "订单完成时间: " + format.format(new Date(order.etime * 1000)))
                            .setText(R.id.start_time, "开始服务时间: " + format.format(new Date(order.start_time * 1000)));
                    break;
                case 7:
                    baseViewHolder.setText(R.id.staff_name, "接单管家: " + order.staff_name)
                            .setText(R.id.jie_time, "接单时间: " + format.format(new Date(order.jie_time * 1000)))
                            .setText(R.id.etime, "结束服务时间: " + format.format(new Date(order.petime * 1000)))
                            .setText(R.id.start_time, "开始服务时间: " + format.format(new Date(order.start_time * 1000)));
                    break;
                case -1:
                    baseViewHolder.setText(R.id.cancel_time, "退款时间: " + format.format(new Date(order.cancel_time * 1000)));
                    break;
                case -2:
                    baseViewHolder.setText(R.id.cancel_time, "取消时间: " + format.format(new Date(order.cancel_time * 1000)));
                    break;
            }
        }
    }

}
