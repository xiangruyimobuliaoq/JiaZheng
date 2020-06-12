package com.nst.jiazheng.user.grzx;

import android.content.Intent;
import android.net.Uri;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.nst.jiazheng.R;
import com.nst.jiazheng.api.Api;
import com.nst.jiazheng.api.resp.AboutUs;
import com.nst.jiazheng.api.resp.Message;
import com.nst.jiazheng.api.resp.PayInfo;
import com.nst.jiazheng.api.resp.Register;
import com.nst.jiazheng.api.resp.Resp;
import com.nst.jiazheng.api.resp.UserCenter;
import com.nst.jiazheng.base.BaseFragment;
import com.nst.jiazheng.base.Layout;
import com.nst.jiazheng.base.SpUtil;
import com.nst.jiazheng.login.LoginActivity;
import com.nst.jiazheng.user.qb.CouponListActivity;
import com.nst.jiazheng.worker.widget.ConfirmWindow;

import java.util.List;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 创建者     彭龙
 * 创建时间   2020/4/2 6:01 PM
 * 描述
 * <p>
 * 更新者     $
 * 更新时间   $
 * 更新描述
 */
@Layout(layoutId = R.layout.fragment_grzx)
public class GrzxFragment extends BaseFragment {
    @BindView(R.id.sz)
    ImageView sz;
    @BindView(R.id.user)
    ImageView user;
    @BindView(R.id.worker)
    ImageView worker;
    @BindView(R.id.grzl)
    LinearLayout grzl;
    @BindView(R.id.touxiang)
    CircleImageView touxiang;
    @BindView(R.id.nickname)
    TextView nickname;
    @BindView(R.id.msgnum)
    TextView msgnum;
    @BindView(R.id.descnum)
    TextView descnum;
    @BindView(R.id.wddd)
    TextView wddd;
    @BindView(R.id.sqcwgj)
    TextView sqcwgj;
    @BindView(R.id.sqcwgjline)
    View sqcwgjline;
    @BindView(R.id.yjfk)
    TextView yjfk;
    @BindView(R.id.tjfx)
    TextView tjfx;
    @BindView(R.id.lxkf)
    TextView lxkf;
    @BindView(R.id.msg)
    LinearLayout msg;
    @BindView(R.id.desc)
    LinearLayout desc;
    private Register mUserInfo;

    @Override
    protected void init() {
        mUserInfo = (Register) SpUtil.readObj("userInfo");
        sz.setOnClickListener(v -> {
            overlay(SettingActivity.class);
        });
        msg.setOnClickListener(v -> {
            overlay(MsgCenterActivity.class);
        });
        desc.setOnClickListener(v -> {
            overlay(CommentActivity.class);
        });
        wddd.setOnClickListener(v -> {
            overlay(OrderActivity.class);
        });
        sqcwgj.setOnClickListener(v -> {
            overlay(WorkerApplyActivity.class);
        });
        yjfk.setOnClickListener(v -> {
            overlay(FeedBackActivity.class);
        });
        grzl.setOnClickListener(v -> {
            overlay(UserInfoActivity.class);
        });
        tjfx.setOnClickListener(v -> {
            overlay(ShareActivity.class);
        });
        lxkf.setOnClickListener(v -> {
            callServer();
        });
    }

    private void callServer() {
        OkGo.<String>post(Api.publicApi).params("api_name", "mobile").execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                Resp<AboutUs> resp = new Gson().fromJson(response.body(), new TypeToken<Resp<AboutUs>>() {
                }.getType());
                if (resp.code == 1) {
                    new ConfirmWindow(mContext)
                            .setContent(resp.data.mobile, "拨打")
                            .setListener((ConfirmWindow window) -> {
                                Intent intent = new Intent(Intent.ACTION_DIAL);
                                Uri data = Uri.parse("tel:" + resp.data.mobile);
                                intent.setData(data);
                                startActivity(intent);
                                window.dismiss();
                            })
                            .setPopupGravity(Gravity.CENTER)
                            .setBackPressEnable(true)
                            .setOutSideDismiss(true)
                            .showPopupWindow();
                }
            }
        });
    }

    @Override
    public void setCenterData(UserCenter data) throws Exception{
        if (data.type != 1) {
            sqcwgj.setVisibility(View.GONE);
            sqcwgjline.setVisibility(View.GONE);
        }
        nickname.setText(data.nickname);
        msgnum.setText(String.valueOf(data.msg_count));
        descnum.setText(String.valueOf(data.comment_count));
        user.setImageResource(data.is_certification == 1 ? R.mipmap.ic_hy : R.mipmap.ic_hy2);
        worker.setImageResource(mUserInfo.type == 1 ? R.mipmap.ic_jz1 : R.mipmap.ic_jz);
        try {
            Glide.with(this).load(data.headimgurl).error(R.mipmap.ic_tx).into(touxiang);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
