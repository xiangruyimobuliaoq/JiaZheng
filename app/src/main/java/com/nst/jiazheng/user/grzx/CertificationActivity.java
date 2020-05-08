package com.nst.jiazheng.user.grzx;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.nst.jiazheng.R;
import com.nst.jiazheng.base.BaseToolBarActivity;
import com.nst.jiazheng.base.Layout;

import butterknife.BindView;

/**
 * 创建者     彭龙
 * 创建时间   2020/5/6 3:51 PM
 * 描述
 * <p>
 * 更新者     $
 * 更新时间   $
 * 更新描述
 */
@Layout(layoutId = R.layout.activity_certification)
public class CertificationActivity extends BaseToolBarActivity {
    @BindView(R.id.name)
    EditText name;
    @BindView(R.id.cerNum)
    EditText cerNum;
    @BindView(R.id.submit)
    Button submit;

    @Override
    protected void init() {
        setTitle("实名认证");
        submit.setOnClickListener(view -> {
            submitCertificate();
        });
    }

    private void submitCertificate() {
        String name = this.name.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            toast("请输入姓名");
            return;
        }
        String cerNum = this.cerNum.getText().toString().trim();
        if (TextUtils.isEmpty(cerNum)) {
            toast("请输入身份证号码");
            return;
        }
    }
}
