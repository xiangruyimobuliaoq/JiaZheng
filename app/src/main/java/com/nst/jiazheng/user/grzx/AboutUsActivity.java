package com.nst.jiazheng.user.grzx;

import android.text.Html;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.nst.jiazheng.R;
import com.nst.jiazheng.api.Api;
import com.nst.jiazheng.api.resp.AboutUs;
import com.nst.jiazheng.api.resp.Register;
import com.nst.jiazheng.api.resp.Resp;
import com.nst.jiazheng.base.BaseToolBarActivity;
import com.nst.jiazheng.base.Layout;

import butterknife.BindView;

/**
 * 创建者     彭龙
 * 创建时间   2020/4/11 12:54 PM
 * 描述
 * <p>
 * 更新者     $
 * 更新时间   $
 * 更新描述
 */
@Layout(layoutId = R.layout.activity_aboutus)
public class AboutUsActivity extends BaseToolBarActivity {
    @BindView(R.id.content)
    TextView content;

    @Override
    protected void init() {
        setTitle("关于我们");

        OkGo.<String>post(Api.publicApi).params("api_name", "about_us").execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                Resp<AboutUs> resp = new Gson().fromJson(response.body(), new TypeToken<Resp<AboutUs>>() {
                }.getType());
                if (resp.code == 1) {
                    content.setText(Html.fromHtml(resp.data.copyright) + "\r\n" + "        联系电话:" + resp.data.mobile);
                }
            }
        });
    }
}
