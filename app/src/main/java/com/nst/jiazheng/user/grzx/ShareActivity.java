package com.nst.jiazheng.user.grzx;

import android.text.Html;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.nst.jiazheng.R;
import com.nst.jiazheng.api.Api;
import com.nst.jiazheng.api.resp.Question;
import com.nst.jiazheng.api.resp.Register;
import com.nst.jiazheng.api.resp.Resp;
import com.nst.jiazheng.base.BaseToolBarActivity;
import com.nst.jiazheng.base.Layout;
import com.nst.jiazheng.base.SpUtil;
import com.nst.jiazheng.login.LoginActivity;

import butterknife.BindView;

/**
 * 创建者     彭龙
 * 创建时间   2020/5/4 10:54 AM
 * 描述
 * <p>
 * 更新者     $
 * 更新时间   $
 * 更新描述
 */
@Layout(layoutId = R.layout.activity_share)
public class ShareActivity extends BaseToolBarActivity {
    @BindView(R.id.submit)
    Button submit;
    private Register mUserInfo;

    @Override
    protected void init() {
        setTitle("推荐分享");
        mUserInfo = (Register) SpUtil.readObj("userInfo");
        submit.setOnClickListener(view -> wxShare());
        getShareData();
    }

    private void getShareData() {
        OkGo.<String>post(Api.serverApi).params("api_name", "referrer").params("token", mUserInfo.token).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                Resp resp = new Gson().fromJson(response.body(), new TypeToken<Resp>() {
                }.getType());
                if (resp.code == 1) {
                } else if (resp.code == 101) {
                    SpUtil.putBoolean("isLogin", false);
                    startAndClearAll(LoginActivity.class);
                }
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                toast(response.body());
            }
        });
    }

    private void wxShare() {


    }
}
