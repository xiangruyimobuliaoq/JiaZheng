package com.nst.jiazheng.user.qb;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.nst.jiazheng.R;
import com.nst.jiazheng.api.Api;
import com.nst.jiazheng.api.resp.IntegralLog;
import com.nst.jiazheng.api.resp.Register;
import com.nst.jiazheng.api.resp.Resp;
import com.nst.jiazheng.base.BaseToolBarActivity;
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
 * 创建时间   2020/4/22 9:23 AM
 * 描述
 * <p>
 * 更新者     $
 * 更新时间   $
 * 更新描述
 */
@Layout(layoutId = R.layout.activity_integrallog)
public class IntegralLogActivity extends BaseToolBarActivity {
    @BindView(R.id.integralloglist)
    RecyclerView integralloglist;
    private IntegralLogAdapter mAdapter;
    private Register mUserInfo;

    @Override
    protected void init() {
        setTitle("积分明细");
        mUserInfo = (Register) SpUtil.readObj("userInfo");
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(RecyclerView.VERTICAL);
        integralloglist.setLayoutManager(manager);
        mAdapter = new IntegralLogAdapter(R.layout.item_integrallog, null);
        integralloglist.setAdapter(mAdapter);
        getIntegralLog();
    }

    private void getIntegralLog() {
        showDialog("加载中", true);
        OkGo.<String>post(Api.userApi).params("api_name", "integral_log").params("token", mUserInfo.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        dismissDialog();
                        Resp<List<IntegralLog>> resp = new Gson().fromJson(response.body(), new TypeToken<Resp<List<IntegralLog>>>() {
                        }.getType());
                        if (resp.code == 1) {
                            mAdapter.setList(resp.data);
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

    class IntegralLogAdapter extends BaseQuickAdapter<IntegralLog, BaseViewHolder> {

        public IntegralLogAdapter(int layoutResId, List<IntegralLog> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder baseViewHolder, IntegralLog integralLog) {
            baseViewHolder.setText(R.id.title, integralLog.title)
                    .setText(R.id.ctime, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(integralLog.ctime * 1000)))
                    .setText(R.id.integral, integralLog.type == 1 ? "+" + integralLog.integral : "-" + integralLog.integral);
        }
    }
}
