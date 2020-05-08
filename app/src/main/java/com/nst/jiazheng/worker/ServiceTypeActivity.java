package com.nst.jiazheng.worker;


import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.nst.jiazheng.R;
import com.nst.jiazheng.api.Api;
import com.nst.jiazheng.api.resp.Register;
import com.nst.jiazheng.api.resp.Resp;
import com.nst.jiazheng.api.resp.ServeType;
import com.nst.jiazheng.base.BaseToolBarActivity;
import com.nst.jiazheng.base.Layout;
import com.nst.jiazheng.base.SpUtil;

import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;

/**
 * 创建者     ZhangAnran
 * 创建时间   2020/4/18 4:48 PM
 * 描述	      服务类型
 */
@Layout(layoutId = R.layout.activity_service_type)
public class ServiceTypeActivity extends BaseToolBarActivity {
    @BindView(R.id.typelist)
    RecyclerView typelist;
    @BindView(R.id.submit)
    Button submit;

    private TypeAdapter mAdapter;
    private Register mUserInfo;

    @Override
    protected void init() {
        mUserInfo = (Register) SpUtil.readObj("userInfo");
        initView();
        initData();
    }

    private void initView() {
        setTitle("选择服务");
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(RecyclerView.VERTICAL);
        typelist.setLayoutManager(manager);
        mAdapter = new TypeAdapter(R.layout.item_servetype_big, null);
        typelist.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
        });
        submit.setOnClickListener(view -> {
            submit();
        });
    }

    private void submit() {
        String params = "";
        for (ServeType serveType : mAdapter.getData()
        ) {
            if (serveType.checked == 1) {
                params += serveType.id + ",";
            }
        }
        OkGo.<String>post(Api.serverApi).params("api_name", "server_edit")
                .params("token", mUserInfo.token)
                .params("serve_type_id", params)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Resp resp = new Gson().fromJson(response.body(), new TypeToken<Resp>() {
                        }.getType());
                        toast(resp.msg);
                        if (resp.code == 1) {
                            finish();
                        }
                    }
                });
    }

    private void initData() {
        getTypeList();
    }

    private void getTypeList() {
        OkGo.<String>post(Api.serverApi).params("api_name", "server_type_app").params("token", mUserInfo.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Resp<List<ServeType>> resp = new Gson().fromJson(response.body(), new TypeToken<Resp<List<ServeType>>>() {
                        }.getType());
                        if (resp.code == 1) {
                            mAdapter.setList(resp.data);
                        }
                    }
                });
    }

    class TypeAdapter extends BaseQuickAdapter<ServeType, BaseViewHolder> {

        public TypeAdapter(int layoutResId, List<ServeType> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder baseViewHolder, ServeType serveType) {
            baseViewHolder.setText(R.id.title, serveType.title)
                    .setText(R.id.unit, serveType.price + "元/" + serveType.serve_type_units);
            CheckBox cb = baseViewHolder.getView(R.id.cb);
            cb.setChecked(serveType.checked == 1);

            cb.setOnCheckedChangeListener((compoundButton, b) -> {
                if (b) {
                    serveType.checked = 1;
                } else {
                    serveType.checked = 0;
                }
            });
        }
    }
}
