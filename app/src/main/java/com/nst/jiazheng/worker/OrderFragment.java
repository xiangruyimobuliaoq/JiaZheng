package com.nst.jiazheng.worker;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.chad.library.adapter.base.listener.OnItemClickListener;
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
import com.nst.jiazheng.user.grzx.OrderDetailsDaizhifuActivity;
import com.nst.jiazheng.worker.widget.ConfirmWindow;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
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
    int type;
    private OrderAdapter mAdapter;
    private Register mUserInfo;
    private BaseLoadMoreModule mLoadMoreModule;
    private int status;

    public OrderFragment(int type) {
        super();
        this.type = type;
    }

    @Override
    protected void init() {
        mUserInfo = (Register) SpUtil.readObj("userInfo");
        if (mUserInfo.type == 2) {
            switch (type) {
                case 0:
                    status = 2;
                    break;
                case 1:
                    status = 3;
                    break;
                case 2:
                    status = 4;
                    break;
                case 3:
                    status = 7;
                    break;
                case 4:
                    status = 6;
                    break;
            }
        } else {
            switch (type) {
                case 0:
                    status = 3;
                    break;
                case 1:
                    status = 4;
                    break;
                case 2:
                    status = 7;
                    break;
                case 3:
                    status = 6;
                    break;
            }
        }
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
                case 2:
                case 5:
                    overlay(JiedanlanActivity.class, bundle);
                    break;
                case 3:
                    overlay(DaijinxingActivity.class, bundle);
                    break;
                case 4:
                    overlay(JinxingzhongActivity.class, bundle);
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
        if (loadMore) {
            OkGo.<String>post(Api.orderApi).params("api_name", "order_list_app")
                    .params("token", mUserInfo.token)
                    .params("status", status)
                    .params("page", mAdapter.getData().size() / 10 + 1)
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(Response<String> response) {
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
                        }
                    });
        } else {
            OkGo.<String>post(Api.orderApi).params("api_name", "order_list_app")
                    .params("token", mUserInfo.token)
                    .params("status", status)
                    .params("page", 1)
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(Response<String> response) {
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

    private void acceptOrder(Order order) {
        showDialog("正在提交", true);
        OkGo.<String>post(Api.orderApi).params("api_name", order.status == 2 ? "order_qiang" : "order_confirm")
                .params("token", mUserInfo.token)
                .params("order_id", order.id)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        dismissDialog();
                        Resp resp = new Gson().fromJson(response.body(), new TypeToken<Resp>() {
                        }.getType());
                        toast(resp.msg);
                        if (resp.code == 1) {
                            mAdapter.remove(order);
                        } else if (resp.code == 101) {
                            SpUtil.putBoolean("isLogin", false);
                            startAndClearAll(LoginActivity.class);
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        dismissDialog();
                    }
                });
    }

    class OrderAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> implements LoadMoreModule {

        public OrderAdapter(List<MultiItemEntity> data) {
            super(data);
            addItemType(2, R.layout.item_order_worker_jiedan);
            addItemType(3, R.layout.item_order_worker_daijinxing);
            addItemType(4, R.layout.item_order_worker_jinxingzhong);
            addItemType(5, R.layout.item_order_worker_jiedan);
            addItemType(6, R.layout.item_order_worker_yiwancheng);
            addItemType(7, R.layout.item_order_worker_daiqueren);
            addItemType(-1, R.layout.item_order_worker_yiwancheng);
            addItemType(-2, R.layout.item_order_worker_yiwancheng);
        }

        @Override
        protected void convert(BaseViewHolder baseViewHolder, MultiItemEntity multiItemEntity) {
            Order order = (Order) multiItemEntity;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            baseViewHolder.setText(R.id.serve_type_name, order.serve_type_name)
                    .setText(R.id.content, order.content)
                    .setText(R.id.num, order.num + "")
                    .setText(R.id.address, order.address)
                    .setText(R.id.time, order.time)
                    .setText(R.id.serve_type_units, order.serve_type_units)
                    .setText(R.id.pay_price, "¥ " + order.pay_price);
            switch (baseViewHolder.getItemViewType()) {
                case 2:
                case 5:
                    baseViewHolder.getView(R.id.jie).setOnClickListener(view -> {
                        new ConfirmWindow(mContext)
                                .setContent("是否确认接单?", "确认")
                                .setListener((ConfirmWindow window) -> {
                                    acceptOrder(order);
                                    window.dismiss();
                                })
                                .setPopupGravity(Gravity.CENTER)
                                .setBackPressEnable(true)
                                .setOutSideDismiss(true)
                                .showPopupWindow();
                    });
                    break;
                case 4:
                    baseViewHolder.setText(R.id.start_time, format.format(new Date(order.start_time * 1000)));
                    break;
                case 7:
                    baseViewHolder.setText(R.id.start_time, format.format(new Date(order.start_time * 1000)))
                            .setText(R.id.petime, format.format(new Date(order.petime * 1000)));
                    break;
                case 6:
                case -1:
                case -2:
                    baseViewHolder.setText(R.id.start_time, format.format(new Date(order.start_time * 1000)))
                            .setText(R.id.etime, format.format(new Date(order.etime * 1000)))
                            .setText(R.id.petime, format.format(new Date(order.petime * 1000)));
                    break;
            }
        }
    }
}
