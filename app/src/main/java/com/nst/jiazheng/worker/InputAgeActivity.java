package com.nst.jiazheng.worker;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.nst.jiazheng.R;
import com.nst.jiazheng.base.BaseToolBarActivity;
import com.nst.jiazheng.base.Layout;
import com.nst.jiazheng.worker.utils.KeyboardUtils;

import butterknife.BindView;

/**
 * 创建者     ZhangAnran
 * 创建时间   2020/4/19 10:42 AM
 * 描述	      年龄
 */
@Layout(layoutId = R.layout.activity_input_age)
public class InputAgeActivity extends BaseToolBarActivity {
    @BindView(R.id.tv_confirm)
    TextView mTvConfirm;
    @BindView(R.id.et_age)
    EditText mEtAge;
    public static String INPUT_AGE = "年龄";
    public static int RESULT_CODE = 666;

    @Override
    protected void init() {
        initView();
        initData();
        initEvent();
    }


    private void initView() {
        setTitle("年龄");
    }

    private void initData() {
        String age = getIntent().getStringExtra(INPUT_AGE);
        mEtAge.setText(age);
    }

    private void initEvent() {
        mTvConfirm.setOnClickListener(view -> {
            KeyboardUtils.hideKeyboard(this);
            Intent intent = new Intent();
            int age = Integer.parseInt(mEtAge.getText().toString().trim());
            intent.putExtra(INPUT_AGE, age); //将计算的值回传回去
            setResult(RESULT_CODE, intent);
            finish(); //结束当前的activity的生命周期
        });

    }

}
