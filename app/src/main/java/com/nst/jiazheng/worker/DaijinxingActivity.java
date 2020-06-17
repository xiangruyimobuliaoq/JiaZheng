package com.nst.jiazheng.worker;

import android.content.Intent;
import android.net.Uri;
import android.view.Gravity;
import android.widget.ImageView;
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
import com.nst.jiazheng.api.resp.Order;
import com.nst.jiazheng.api.resp.Register;
import com.nst.jiazheng.api.resp.Resp;
import com.nst.jiazheng.api.resp.UpFile;
import com.nst.jiazheng.base.BaseToolBarActivity;
import com.nst.jiazheng.base.Layout;
import com.nst.jiazheng.base.SpUtil;
import com.nst.jiazheng.login.LoginActivity;
import com.nst.jiazheng.map.HisLocationBean;
import com.nst.jiazheng.map.MapWindow;
import com.nst.jiazheng.user.SendServeActivity;
import com.nst.jiazheng.worker.widget.ConfirmWindow;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;
import io.rong.imkit.RongIM;
import io.rong.imkit.userInfoCache.RongUserInfoManager;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.UserInfo;

/**
 * 创建者     彭龙
 * 创建时间   2020/5/11 3:33 PM
 * 描述
 * <p>
 * 更新者     $
 * 更新时间   $
 * 更新描述
 */
@Layout(layoutId = R.layout.activity_daijinxing)
public class DaijinxingActivity extends BaseToolBarActivity {
    @BindView(R.id.tx)
    CircleImageView tx;
    @BindView(R.id.nickname)
    TextView nickname;
    @BindView(R.id.StatusText)
    TextView StatusText;
    @BindView(R.id.score)
    TextView score;
    @BindView(R.id.ddh)
    ImageView ddh;
    @BindView(R.id.tv_confirm)
    TextView mTvConfirm;
    @BindView(R.id.cancel)
    TextView cancel;
    @BindView(R.id.xx)
    ImageView xx;
    @BindView(R.id.dh)
    ImageView dh;
    @BindView(R.id.serve_type_name)
    TextView serve_type_name;
    @BindView(R.id.content)
    TextView content;
    @BindView(R.id.time)
    TextView time;
    @BindView(R.id.address)
    TextView address;
    @BindView(R.id.serve_type_units)
    TextView serve_type_units;
    @BindView(R.id.num)
    TextView num;
    @BindView(R.id.pay_price)
    TextView pay_price;
    @BindView(R.id.jie_time)
    TextView jie_time;
    @BindView(R.id.imglist)
    RecyclerView imglist;
    private Register mUserInfo;
    private ArrayList<UpFile> mUpFiles;
    private PicAdapter mAdapter;

    @Override
    protected void init() {
        setTitle("订单详情");
        String id = getIntent().getStringExtra("id");
        mUserInfo = (Register) SpUtil.readObj("userInfo");
        OkGo.<String>post(Api.orderApi).params("api_name", "app_order_info")
                .params("token", mUserInfo.token)
                .params("order_id", id).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                Resp<Order> resp = new Gson().fromJson(response.body(), new TypeToken<Resp<Order>>() {
                }.getType());
                if (resp.code == 1) {
                    setData(resp.data);
                } else if (resp.code == 101) {
                    SpUtil.putBoolean("isLogin", false);
                    startAndClearAll(LoginActivity.class);
                }
            }
        });
    }

    private void setData(Order data) {
        nickname.setText(data.staff_name);
        address.setText(data.address);
        StatusText.setText(data.StatusText);
        score.setText(data.staff_score);
        ddh.setOnClickListener(view -> {
            new ConfirmWindow(mContext)
                    .setContent(data.staff_mobile, "拨打")
                    .setListener((ConfirmWindow window) -> {
                        callServer(data.staff_mobile);
                        window.dismiss();
                    })
                    .setPopupGravity(Gravity.CENTER)
                    .setBackPressEnable(true)
                    .setOutSideDismiss(true)
                    .showPopupWindow();
        });
        mTvConfirm.setOnClickListener(view -> {
            new ConfirmWindow(this)
                    .setContent("是否确认开始订单？")
                    .setListener((ConfirmWindow window) -> {
                        startOrder(data.id);
                        window.dismiss();
                    })
                    .setPopupGravity(Gravity.CENTER)
                    .setBackPressEnable(true)
                    .setOutSideDismiss(true)
                    .showPopupWindow();
        });
        cancel.setOnClickListener(view -> {
            new ConfirmWindow(this)
                    .setContent("是否确认取消该订单？")
                    .setListener((ConfirmWindow window) -> {
                        cancelOrder(data.id);
                        window.dismiss();
                    })
                    .setPopupGravity(Gravity.CENTER)
                    .setBackPressEnable(true)
                    .setOutSideDismiss(true)
                    .showPopupWindow();
        });
        xx.setOnClickListener(view -> {
            Conversation.ConversationType conversationType = Conversation.ConversationType.PRIVATE;
            String targetId = data.staff_id;
            String title = "聊天";
            UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(targetId);
            RongIM.getInstance().refreshUserInfoCache(userInfo);
            RongIM.getInstance().startConversation(this, conversationType, targetId, title, null);
        });
        dh.setOnClickListener(view -> {
            new MapWindow(this, new HisLocationBean(data.lat, data.lng)).setPopupGravity(Gravity.BOTTOM).showPopupWindow();
        });
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        serve_type_name.setText(data.serve_type_name);
        content.setText(data.content);
        jie_time.setText(data.jie_time == 0 ? "" : format.format(new Date(data.jie_time * 1000)));
        time.setText(data.time);
        serve_type_units.setText(data.serve_type_units);
        num.setText(data.num);
        pay_price.setText("¥ " + data.pay_price);
        GridLayoutManager manager = new GridLayoutManager(this, 4);
        imglist.setLayoutManager(manager);
        String[] split = data.serve_img.split(",");
        mUpFiles = new ArrayList<>();
        for (String s : split
        ) {
            mUpFiles.add(new UpFile("", s));
        }
        mAdapter = new PicAdapter(R.layout.item_pic, mUpFiles);
        imglist.setAdapter(mAdapter);
        try {
            Glide.with(this).load(data.staff_logo).error(R.mipmap.ic_tx).into(tx);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startOrder(String id) {
        showDialog("正在提交", true);
        OkGo.<String>post(Api.orderApi).params("api_name", "order_start")
                .params("token", mUserInfo.token)
                .params("order_id", id).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                dismissDialog();
                Resp resp = new Gson().fromJson(response.body(), new TypeToken<Resp>() {
                }.getType());
                toast(resp.msg);
                if (resp.code == 1) {
                    finish();
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

    private void callServer(String staff_mobile) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        Uri data = Uri.parse("tel:" + staff_mobile);
        intent.setData(data);
        startActivity(intent);
    }

    private void cancelOrder(String id) {
        showDialog("正在取消", true);
        OkGo.<String>post(Api.orderApi).params("api_name", "app_order_cancel")
                .params("token", mUserInfo.token)
                .params("order_id", id).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                dismissDialog();
                Resp resp = new Gson().fromJson(response.body(), new TypeToken<Resp>() {
                }.getType());
                toast(resp.msg);
                if (resp.code == 1) {
                    finish();
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

    class PicAdapter extends BaseQuickAdapter<UpFile, BaseViewHolder> {

        public PicAdapter(int layoutResId, List<UpFile> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder baseViewHolder, UpFile upfile) {
            ImageView pic = baseViewHolder.getView(R.id.pic);
            try {
                Glide.with(DaijinxingActivity.this).load(upfile.path).centerCrop().into(pic);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
