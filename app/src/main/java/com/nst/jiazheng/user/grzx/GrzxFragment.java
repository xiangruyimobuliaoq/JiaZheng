package com.nst.jiazheng.user.grzx;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nst.jiazheng.R;
import com.nst.jiazheng.base.BaseFragment;
import com.nst.jiazheng.base.Layout;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 创建者     彭龙
 * 创建时间   2020/4/2 6:01 PM
 * 描述
 * <p>
 * 更新者     $
 * 更新时间   $
 * 更新描述
 */
@Layout(layoutId = R.layout.fragment_grzx)
public class GrzxFragment extends BaseFragment {
    @BindView(R.id.sz)
    ImageView sz;
    @BindView(R.id.grzl)
    LinearLayout grzl;
    @BindView(R.id.touxiang)
    CircleImageView touxiang;
    @BindView(R.id.nickname)
    TextView nickname;
    @BindView(R.id.msgnum)
    TextView msgnum;
    @BindView(R.id.descnum)
    TextView descnum;
    @BindView(R.id.wddd)
    TextView wddd;
    @BindView(R.id.sqcwgj)
    TextView sqcwgj;
    @BindView(R.id.yjfk)
    TextView yjfk;
    @BindView(R.id.tjfx)
    TextView tjfx;
    @BindView(R.id.lxkf)
    TextView lxkf;
    @BindView(R.id.msg)
    LinearLayout msg;
    @BindView(R.id.desc)
    LinearLayout desc;

    @Override
    protected void init() {
        sz.setOnClickListener(v -> {

        });
    }
}
