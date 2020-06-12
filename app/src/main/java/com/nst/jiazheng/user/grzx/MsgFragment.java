package com.nst.jiazheng.user.grzx;

import android.view.Gravity;
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
import com.nst.jiazheng.api.resp.Message;
import com.nst.jiazheng.api.resp.Register;
import com.nst.jiazheng.api.resp.Resp;
import com.nst.jiazheng.base.BaseFragment;
import com.nst.jiazheng.base.Layout;
import com.nst.jiazheng.base.SpUtil;
import com.nst.jiazheng.login.LoginActivity;
import com.nst.jiazheng.worker.widget.ConfirmWindow;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;

/**
 * 创建者     彭龙
 * 创建时间   2020/5/19 11:00 AM
 * 描述
 * <p>
 * 更新者     $
 * 更新时间   $
 * 更新描述
 */
@Layout(layoutId = R.layout.fragment_msg)
public class MsgFragment extends BaseFragment {
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    private Register mUserInfo;
    private MsgAdapter mAdapter;

    @Override
    protected void init() {
        mUserInfo = (Register) SpUtil.readObj("userInfo");
        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        manager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(manager);
        mAdapter = new MsgAdapter(R.layout.item_message, null);
        recyclerView.setAdapter(mAdapter);
        getMessage();
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            Message message = mAdapter.getData().get(position);
            switch (message.type) {
                case 1:
                    break;
                case 2:
                    break;
                case 3:
                    showInviteWindow(message);
                    break;
                case 4:
                    break;
            }
        });

    }

    private void showInviteWindow(Message message) {
        new ConfirmWindow(mContext)
                .setContent(message.content, "确认")
                .setListener((ConfirmWindow window) -> {
                    joinCompany(message);
                    window.dismiss();
                })
                .setPopupGravity(Gravity.CENTER)
                .setBackPressEnable(true)
                .setOutSideDismiss(true)
                .showPopupWindow();
    }

    private void joinCompany(Message message) {
        showDialog("正在提交", true);
        OkGo.<String>post(Api.userApi).params("api_name", "join_company")
                .params("token", mUserInfo.token)
                .params("company_id", message.company_id)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        dismissDialog();
                        Resp resp = new Gson().fromJson(response.body(), new TypeToken<Resp>() {
                        }.getType());
                        toast(resp.msg);
                        if (resp.code == 1) {
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

    private void getMessage() {
        OkGo.<String>post(Api.userApi).params("api_name", "msg_list").params("token", mUserInfo.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Resp<List<Message>> resp = new Gson().fromJson(response.body(), new TypeToken<Resp<List<Message>>>() {
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

    class MsgAdapter extends BaseQuickAdapter<Message, BaseViewHolder> {

        public MsgAdapter(int layoutResId, @Nullable List<Message> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(@NotNull BaseViewHolder baseViewHolder, Message message) {
            baseViewHolder.setText(R.id.title, message.title)
                    .setText(R.id.content, message.content)
                    .setText(R.id.ctime, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(message.ctime * 1000)));
        }
    }
}
