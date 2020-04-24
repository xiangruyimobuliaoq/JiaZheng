package com.nst.jiazheng.user.grzx;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.google.android.material.tabs.TabLayout;
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
import com.nst.jiazheng.base.BaseToolBarActivity;
import com.nst.jiazheng.base.Layout;
import com.nst.jiazheng.base.SpUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;

/**
 * 创建者     彭龙
 * 创建时间   2020/4/24 10:45 AM
 * 描述
 * <p>
 * 更新者     $
 * 更新时间   $
 * 更新描述
 */
@Layout(layoutId = R.layout.activity_msgcenter)
public class MsgCenterActivity extends BaseToolBarActivity {
    @BindView(R.id.tab)
    TabLayout mTab;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    private Register mUserInfo;
    private MsgCenterAdapter mAdapter;

    @Override
    protected void init() {
        setTitle("消息中心");
        mUserInfo = (Register) SpUtil.readObj("userInfo");
        mTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if (position == 0) {
                    getMessage();
                } else {

                }
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
        mAdapter = new MsgCenterAdapter(null);
        recyclerView.setAdapter(mAdapter);
        getMessage();
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
                        }
                    }
                });
    }

    class MsgCenterAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {

        public MsgCenterAdapter(List<MultiItemEntity> data) {
            super(data);
            addItemType(0, R.layout.item_message);
        }

        @Override
        protected void convert(BaseViewHolder baseViewHolder, MultiItemEntity multiItemEntity) {
            switch (baseViewHolder.getItemViewType()) {
                case 0:
                    Message message = (Message) multiItemEntity;
                    baseViewHolder.setText(R.id.title, message.title)
                            .setText(R.id.content, message.content)
                            .setText(R.id.ctime, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(message.ctime * 1000)));
                    break;
            }
        }
    }
}
