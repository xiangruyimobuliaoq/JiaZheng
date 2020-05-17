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
import com.nst.jiazheng.login.LoginActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;

/**
 * 创建者     ZhangAnran
 * 创建时间   2020/4/18 2:36 PM
 * 描述	      提现记录
 */

@Layout(layoutId = R.layout.activity_draw_record)
public class WithdrawalsRecordActivity extends BaseToolBarActivity {
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
        mUserInfo = (Register) SpUtil.readObj("userInfo");
        items = new ArrayList<>();
        OkGo.<String>post(Api.userApi)
                .params("api_name", "withdraw_log")
                .params("token", mUserInfo.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LogUtil.getInstance().d("withdraw_log : " + response.body());
                        Resp<List<Order>> resp = new Gson().fromJson(response.body(),
                                new TypeToken<Resp<List<Order>>>() {
                                }.getType());
                        if (resp.code == 1) {
                            List<Order> data = resp.data;
                            myAdapter.setNewInstance(data);
                        }else if (resp.code == 101) {
                            SpUtil.putBoolean("isLogin", false);
                            startAndClearAll(LoginActivity.class);
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                    }
                });

    }

    private void initView() {
        setTitle("提现记录");
        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(manager);
        myAdapter = new MyAdapter(R.layout.item_with_record, items);
        mRecyclerView.setAdapter(myAdapter);
    }

    class MyAdapter extends BaseQuickAdapter<Order, BaseViewHolder> {

        public MyAdapter(int layoutResId, @Nullable List<Order> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, Order item) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            helper.setText(R.id.tv_status, item.title)
                    .setText(R.id.tv_money, item.money)
                    .setText(R.id.tv_msg, item.msg)
                    .setText(R.id.tv_time, format.format(new Date(item.ctime*1000)))
            ;

        }
    }
}
