package com.nst.jiazheng.user;

import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.nst.jiazheng.R;
import com.nst.jiazheng.base.BaseToolBarActivity;
import com.nst.jiazheng.base.Layout;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
/**
 * 创建者     彭龙
 * 创建时间   2020/4/11 1:14 PM
 * 描述
 * <p>
 * 更新者     $
 * 更新时间   $
 * 更新描述
 */
@Layout(layoutId = R.layout.activity_sendserve)
public class SendServeActivity extends BaseToolBarActivity {
    @BindView(R.id.addr)
    TextView addr;
    @BindView(R.id.time)
    TextView time;
    @BindView(R.id.servetype)
    TextView servetype;
    @BindView(R.id.piclist)
    RecyclerView piclist;
    @BindView(R.id.cb)
    CheckBox cb;
    @BindView(R.id.content)
    EditText content;
    @BindView(R.id.submit)
    Button submit;

    @Override
    protected void init() {
        setTitle("寻找管家");
    }
}
