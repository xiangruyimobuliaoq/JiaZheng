package com.nst.jiazheng.user.grzx;

import android.view.View;
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
import com.nst.jiazheng.api.resp.Order;
import com.nst.jiazheng.api.resp.Register;
import com.nst.jiazheng.api.resp.Resp;
import com.nst.jiazheng.api.resp.UserCenter;
import com.nst.jiazheng.base.BaseToolBarActivity;
import com.nst.jiazheng.base.Layout;
import com.nst.jiazheng.base.LogUtil;
import com.nst.jiazheng.base.SpUtil;
import com.nst.jiazheng.base.ToastHelper;

import java.util.HashMap;

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
@Layout(layoutId = R.layout.activity_userinfo)
public class UserInfoActivity extends BaseToolBarActivity {
    @BindView(R.id.ll_sex)
    LinearLayout mSelectSex;
    @BindView(R.id.tv_sex)
    TextView mTvSex;
    @BindView(R.id.tv_mobile)
    TextView mTvMobile;
    @BindView(R.id.tv_name)
    TextView mTvName;
    @BindView(R.id.tv_is_certification)
    TextView mTvIsCertification;
    @BindView(R.id.iv_avatar)
    CircleImageView mIvAvatar;

    private Register mUserInfo;

    @Override
    protected void init() {
        initView();
        initEvent();
    }

    private void initView() {
        setTitle("个人资料");
    }

    private void initData() {
        getUserInfo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mUserInfo = (Register) SpUtil.readObj("userInfo");
        initData();
    }

    private void getUserInfo() {
        OkGo.<String>post(Api.userApi)
                .params("api_name", "center")
                .params("token", mUserInfo.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Resp<UserCenter> resp = new Gson().fromJson(response.body(),
                                new TypeToken<Resp<UserCenter>>() {
                                }.getType());
                        if (resp.code == 1) {
                            setData(resp.data);
                        }
                        toast(resp.msg);
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                    }
                });
    }

    private void setData(UserCenter data) {
        mTvMobile.setText(data.mobile);
        mTvName.setText(data.nickname);
        mTvIsCertification.setText(data.is_certification == 1 ? "已认证" : "未认证");
        mTvSex.setText(data.sex == 0 ? "未知" : data.sex == 1 ? "男" : "女");
        Glide.with(this)
                .load(data.headimgurl)
                .error(R.mipmap.ic_tx)
                .into(mIvAvatar);
    }

    private void updateUserInfo(HashMap<String, String> info) {
        HashMap<String, String> map = new HashMap<>();
        map.put("api_name", "edit_user");
        map.put("token", mUserInfo.token);
        map.putAll(info);
        OkGo.<String>post(Api.userApi)
                .params(map)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LogUtil.getInstance().d("edit_user : " + response.body());
                        Resp<Order> resp = new Gson().fromJson(response.body(),
                                new TypeToken<Resp<Order>>() {
                                }.getType());
                        if (resp.code == 1) {

                        }
                        getUserInfo();
                        ToastHelper.showToast(resp.msg, mContext);
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                    }
                });
    }


    private void initEvent() {
        mSelectSex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        mTvIsCertification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}
