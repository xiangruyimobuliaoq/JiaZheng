package com.nst.jiazheng.user.wdgj;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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
import com.nst.jiazheng.api.resp.Worker;
import com.nst.jiazheng.base.BaseToolBarActivity;
import com.nst.jiazheng.base.Layout;
import com.nst.jiazheng.base.SpUtil;

import java.util.List;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;
import per.wsj.library.AndRatingBar;

/**
 * 创建者     彭龙
 * 创建时间   2020/4/16 10:08 AM
 * 描述
 * <p>
 * 更新者     $
 * 更新时间   $
 * 更新描述
 */
@Layout(layoutId = R.layout.activity_companyinfo)
public class CompanyInfoActivity extends BaseToolBarActivity {
    @BindView(R.id.tx)
    CircleImageView tx;
    @BindView(R.id.nickname)
    TextView nickname;
    @BindView(R.id.ratingbar)
    AndRatingBar ratingbar;
    @BindView(R.id.point)
    TextView point;
    @BindView(R.id.typelist)
    RecyclerView typelist;
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.staff_count)
    TextView staff_count;
    @BindView(R.id.address)
    TextView address;
    @BindView(R.id.submit)
    Button submit;

    private Worker mWorker;
    private Register mUserInfo;
    private Worker mData;

    @Override
    protected void init() {
        setTitle("管家详情");
        mUserInfo = (Register) SpUtil.readObj("userInfo");
        mWorker = (Worker) getIntent().getSerializableExtra("worker");
        submit.setEnabled(false);
        getInfo();
    }

    private void getInfo() {
        OkGo.<String>post(Api.serverApi).params("api_name", "company_info").params("token", mUserInfo.token)
                .params("id", mWorker.id).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                Resp<Worker> resp = new Gson().fromJson(response.body(), new TypeToken<Resp<Worker>>() {
                }.getType());
                if (resp.code == 1) {
                    mData = resp.data;
                    setData(mData);
                }
            }
        });
    }

    private void setData(Worker data) {
        submit.setEnabled(true);
        submit.setText(data.is_collect == 0 ? "收藏Ta" : "取消收藏");
        Glide.with(this).load(data.headimgurl).error(R.mipmap.ic_tx).into(tx);
        nickname.setText(mWorker.name);
        ratingbar.setRating(Float.parseFloat(data.score));
        point.setText(data.score + "分");
        name.setText(data.name);
        address.setText(data.address);
        staff_count.setText(data.staff_count + "人");
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                collect();
            }
        });
        GridLayoutManager manager = new GridLayoutManager(this, 4);
        typelist.setLayoutManager(manager);
        TypeAdapter adapter = new TypeAdapter(R.layout.item_servetype, data.servetype);
        typelist.setAdapter(adapter);
    }

    private void collect() {
        OkGo.<String>post(Api.serverApi).params("api_name", "collect").params("token", mUserInfo.token)
                .params("id", mData.id).params("type", 2).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                submit.setEnabled(true);
                Resp<Worker> resp = new Gson().fromJson(response.body(), new TypeToken<Resp<Worker>>() {
                }.getType());
                toast(resp.msg);
                if (resp.code == 1) {
                    if (mData.is_collect == 0) {
                        mData.is_collect = 1;
                        submit.setText("取消收藏");
                    } else {
                        mData.is_collect = 0;
                        submit.setText("收藏Ta");
                    }
                }
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                submit.setEnabled(true);
            }
        });
    }

    class TypeAdapter extends BaseQuickAdapter<ServeType, BaseViewHolder> {

        public TypeAdapter(int layoutResId, List<ServeType> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder baseViewHolder, ServeType serveType) {
            baseViewHolder.setText(R.id.text, serveType.title);
        }
    }
}
