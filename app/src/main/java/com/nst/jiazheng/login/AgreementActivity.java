package com.nst.jiazheng.login;

import android.text.Html;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.nst.jiazheng.R;
import com.nst.jiazheng.api.Api;
import com.nst.jiazheng.api.resp.PrivateAgreement;
import com.nst.jiazheng.api.resp.Resp;
import com.nst.jiazheng.base.BaseToolBarActivity;
import com.nst.jiazheng.base.Layout;

import butterknife.BindView;

/**
 * 创建者     彭龙
 * 创建时间   2020/4/10 9:37 AM
 * 描述
 * <p>
 * 更新者     $
 * 更新时间   $
 * 更新描述
 */

@Layout(layoutId = R.layout.activity_agreement)
public class AgreementActivity extends BaseToolBarActivity {
    @BindView(R.id.content)
    TextView content;

    @Override
    protected void init() {
        setTitle("用户协议");
        OkGo.<String>post(Api.publicApi).params("api_name", "private_agreement").execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                Resp<PrivateAgreement> resp = new Gson().fromJson(response.body(), new TypeToken<Resp<PrivateAgreement>>() {
                }.getType());
                if (resp.code == 1) {
                    content.setText(Html.fromHtml(resp.data.private_agreement));
                }
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                toast(response.body());
            }
        });

    }
}
