package com.nst.jiazheng.worker;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.nst.jiazheng.R;
import com.nst.jiazheng.api.resp.Register;
import com.nst.jiazheng.base.BaseActivity;
import com.nst.jiazheng.base.BaseToolBarActivity;
import com.nst.jiazheng.base.Layout;
import com.nst.jiazheng.base.LogUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 创建者     ZhangAnran
 * 创建时间   2020/4/18 2:36 PM
 * 描述	      提现记录
 */

@Layout(layoutId = R.layout.activity_draw_record)
public class WithdrawalsRecordActivity extends BaseToolBarActivity {
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    private MyAdapter myAdapter;
    private List<Register> items;

    @Override
    protected void init() {
        initView();
        initData();
    }

    private void initData() {
        items = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            items.add(new Register());
        }
        myAdapter.setNewInstance(items);
    }

    private void initView() {
        setTitle("提现记录");
        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(manager);
        myAdapter = new MyAdapter(R.layout.item_with_record, items);
        mRecyclerView.setAdapter(myAdapter);
    }

    class MyAdapter extends BaseQuickAdapter<Register, BaseViewHolder> {

        public MyAdapter(int layoutResId, @Nullable List<Register> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, Register item) {
            int position = helper.getLayoutPosition();
            LogUtil.getInstance().d("position  :: " + position);

        }
    }
}
