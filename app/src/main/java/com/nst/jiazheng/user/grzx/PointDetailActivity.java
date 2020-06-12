package com.nst.jiazheng.user.grzx;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.nst.jiazheng.R;
import com.nst.jiazheng.api.Api;
import com.nst.jiazheng.api.resp.Question;
import com.nst.jiazheng.api.resp.Resp;
import com.nst.jiazheng.base.BaseToolBarActivity;
import com.nst.jiazheng.base.Layout;
import com.nst.jiazheng.base.SpUtil;
import com.nst.jiazheng.login.LoginActivity;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import androidx.annotation.RequiresApi;
import butterknife.BindView;

/**
 * 创建者     彭龙
 * 创建时间   2020/6/11 8:52 AM
 * 描述
 * <p>
 * 更新者     $
 * 更新时间   $
 * 更新描述
 */
@Layout(layoutId = R.layout.activity_pointdetail)
public class PointDetailActivity extends BaseToolBarActivity {
    @BindView(R.id.content)
    TextView content;

    @Override
    protected void init() {
        setTitle(getIntent().getStringExtra("title"));
        OkGo.<String>post(Api.publicApi).params("api_name", "qustion_info").params("id", getIntent().getIntExtra("id", 0)).execute(new StringCallback() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onSuccess(Response<String> response) {
                Resp<Question> resp = new Gson().fromJson(response.body(), new TypeToken<Resp<Question>>() {
                }.getType());
                if (resp.code == 1) {
                    new Thread(() -> {
                        Spanned text = Html.fromHtml(resp.data.content, s -> {
                            Log.e("123", s);
                            Drawable drawable;
                            drawable = getImageNetwork(s);
                            if (drawable != null) {
                                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                            } else if (drawable == null) {
                                return null;
                            }
                            return drawable;
                        }, null);
                        runOnUiThread(() -> content.setText(text));
                    }).start();
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

    public Drawable getImageNetwork(String imageUrl) {
        URL myFileUrl;
        Drawable drawable = null;
        try {
            myFileUrl = new URL(imageUrl);
            HttpURLConnection conn = (HttpURLConnection) myFileUrl
                    .openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            // 在这一步最好先将图片进行压缩，避免消耗内存过多
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            drawable = new BitmapDrawable(bitmap);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return drawable;
    }
}
