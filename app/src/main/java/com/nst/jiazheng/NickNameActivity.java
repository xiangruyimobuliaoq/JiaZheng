package com.nst.jiazheng;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.nst.jiazheng.base.BaseToolBarActivity;
import com.nst.jiazheng.base.Layout;

import butterknife.BindView;

/**
 * 创建者     彭龙
 * 创建时间   2020/5/17 4:01 PM
 * 描述
 * <p>
 * 更新者     $
 * 更新时间   $
 * 更新描述
 */
@Layout(layoutId = R.layout.activity_nickname)
public class NickNameActivity extends BaseToolBarActivity {
    @BindView(R.id.content)
    EditText content;
    @BindView(R.id.submit)
    Button submit;

    @Override
    protected void init() {
        setTitle("昵称");
        String nickname = getIntent().getStringExtra("nickname");
        content.setText(nickname);
        submit.setOnClickListener(view -> {
            String trim = content.getText().toString().trim();
            if (TextUtils.isEmpty(trim)) {
                toast("昵称不能为空");
                return;
            }
            Intent intent = new Intent();
            intent.putExtra("nickname", trim);
            setResult(RESULT_OK, intent);
            finish();
        });
    }
}
