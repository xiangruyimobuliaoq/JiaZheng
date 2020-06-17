package com.nst.jiazheng.worker;

import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.nst.jiazheng.R;
import com.nst.jiazheng.api.Api;
import com.nst.jiazheng.api.resp.Register;
import com.nst.jiazheng.api.resp.Resp;
import com.nst.jiazheng.base.BaseToolBarActivity;
import com.nst.jiazheng.base.Layout;
import com.nst.jiazheng.base.SpUtil;
import com.nst.jiazheng.login.LoginActivity;

import butterknife.BindView;
import per.wsj.library.AndRatingBar;

/**
 * 创建者     彭龙
 * 创建时间   2020/5/11 3:33 PM
 * 描述
 * <p>
 * 更新者     $
 * 更新时间   $
 * 更新描述
 */
@Layout(layoutId = R.layout.activity_add_comment)
public class AddCommentActivity extends BaseToolBarActivity {
    @BindView(R.id.score)
    AndRatingBar score;
    @BindView(R.id.comment)
    EditText comment;
    @BindView(R.id.submit)
    Button submit;
    private Register mUserInfo;
    private String mId;
    private boolean isWorker;

    @Override
    protected void init() {
        setTitle("评价");
        mId = getIntent().getStringExtra("id");
        isWorker = getIntent().getBooleanExtra("isWorker", false);
        mUserInfo = (Register) SpUtil.readObj("userInfo");
        score.setRating(1.0f);
        submit.setOnClickListener(view -> {
            commentSublimit();
        });
    }

    private void commentSublimit() {
        float rating = score.getRating();
        String content = comment.getText().toString().trim();
        showDialog("正在提交", true);
        OkGo.<String>post(Api.userApi).params("api_name", isWorker ? "app_comment_sublimit" : "comment_sublimit")
                .params("token", mUserInfo.token)
                .params("order_id", mId)
                .params("score", rating)
                .params("content", content).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                dismissDialog();
                Resp resp = new Gson().fromJson(response.body(), Resp.class);
                toast(resp.msg);
                if (resp.code == 1) {
                    setResult(RESULT_OK);
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
}
