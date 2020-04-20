package com.nst.jiazheng.user.qb;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.nst.jiazheng.R;
import com.nst.jiazheng.api.Api;
import com.nst.jiazheng.api.resp.Recharge;
import com.nst.jiazheng.api.resp.Register;
import com.nst.jiazheng.api.resp.Resp;
import com.nst.jiazheng.base.BaseToolBarActivity;
import com.nst.jiazheng.base.Layout;
import com.nst.jiazheng.base.SpUtil;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;

/**
 * 创建者     彭龙
 * 创建时间   2020/4/19 9:48 AM
 * 描述
 * <p>
 * 更新者     $
 * 更新时间   $
 * 更新描述
 */
@Layout(layoutId = R.layout.activity_recharge)
public class RechargeActivity extends BaseToolBarActivity {
    @BindView(R.id.rechargelist)
    RecyclerView rechargelist;
    @BindView(R.id.num)
    EditText num;
    @BindView(R.id.weixin)
    CheckBox weixin;
    @BindView(R.id.zhifubao)
    CheckBox zhifubao;
    @BindView(R.id.submit)
    Button submit;
    private Register mUserInfo;
    private RechargeAdapter mAdapter;
    private int chargePosition = -1;

    @Override
    protected void init() {
        mUserInfo = (Register) SpUtil.readObj("userInfo");
        setTitle("充值");
        GridLayoutManager manager = new GridLayoutManager(this, 2);
        rechargelist.setLayoutManager(manager);
        mAdapter = new RechargeAdapter(R.layout.item_recharge, null);
        rechargelist.setAdapter(mAdapter);
        getRechargeList();
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                chargePosition = position;
                mAdapter.notifyDataSetChanged();
                num.setText(mAdapter.getData().get(position).money);
            }
        });

        weixin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    zhifubao.setChecked(false);
                } else {
                    zhifubao.setChecked(true);
                }
            }
        });
        zhifubao.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    weixin.setChecked(false);
                } else {
                    weixin.setChecked(true);
                }
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeBill();
            }
        });
    }

    private void makeBill() {
        if (chargePosition == -1) {
            toast("请选择充值套餐");
            return;
        }
        String money = num.getText().toString().trim();
        if (TextUtils.isEmpty(money)) {
            toast("请输入充值金额");
            return;
        }
        submit.setEnabled(false);
        OkGo.<String>post(Api.userApi).params("api_name", "recharge_sublimit").params("token", mUserInfo.token)
                .params("recharge_id", mAdapter.getData().get(chargePosition).id)
                .params("money", money)
                .params("pay_type", weixin.isChecked() ? 1 : 2)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        submit.setEnabled(true);
                        Resp resp = new Gson().fromJson(response.body(), new TypeToken<Resp>() {
                        }.getType());
                        toast(resp.msg);
                        if (resp.code == 1) {
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        submit.setEnabled(true);
                        super.onError(response);
                    }
                });
    }

    private void getRechargeList() {
        OkGo.<String>post(Api.userApi).params("api_name", "recharge_list").params("token", mUserInfo.token).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                Resp<List<Recharge>> resp = new Gson().fromJson(response.body(), new TypeToken<Resp<List<Recharge>>>() {
                }.getType());
                if (resp.code == 1) {
                    mAdapter.setList(resp.data);
                }
            }
        });
    }

    class RechargeAdapter extends BaseQuickAdapter<Recharge, BaseViewHolder> {

        public RechargeAdapter(int layoutResId, List<Recharge> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder baseViewHolder, Recharge recharge) {
            TextView content = baseViewHolder.getView(R.id.content);
            content.setText(recharge.name);
            content.setBackground(baseViewHolder.getLayoutPosition() == chargePosition ? getDrawable(R.drawable.shape_border_blue_corner_xuxian) : getDrawable(R.drawable.shape_border_gray_corner_xuxian));
            content.setTextColor(baseViewHolder.getLayoutPosition() == chargePosition ? getColor(R.color.statusbar_blue) : getColor(R.color.line_3));
        }
    }
}
