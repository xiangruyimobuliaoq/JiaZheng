package com.nst.jiazheng.user.grzx;

import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nst.jiazheng.R;
import com.nst.jiazheng.api.resp.Comment;
import com.nst.jiazheng.base.BaseToolBarActivity;
import com.nst.jiazheng.base.Layout;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;
import per.wsj.library.AndRatingBar;

/**
 * 创建者     彭龙
 * 创建时间   2020/4/25 2:57 PM
 * 描述
 * <p>
 * 更新者     $
 * 更新时间   $
 * 更新描述
 */
@Layout(layoutId = R.layout.activity_commentdetail)
public class CommentDetailActivity extends BaseToolBarActivity {
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.content)
    TextView content;
    @BindView(R.id.ctime)
    TextView ctime;
    @BindView(R.id.tx)
    CircleImageView tx;
    @BindView(R.id.score)
    AndRatingBar score;

    @Override
    protected void init() {
        setTitle("评价详情");
        Comment data = (Comment) getIntent().getSerializableExtra("data");
        name.setText(data.name);
        content.setText(data.content);
        ctime.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(data.ctime * 1000)));
        Glide.with(this).load(data.headimgurl).error(R.mipmap.ic_tx).into(tx);
        score.setRating(Float.parseFloat(data.score));
    }
}
