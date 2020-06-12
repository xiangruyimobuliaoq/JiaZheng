package com.nst.jiazheng.user.grzx;

import android.Manifest;
import android.os.Build;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.listener.OnResultCallbackListener;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.nst.jiazheng.R;
import com.nst.jiazheng.api.Api;
import com.nst.jiazheng.api.resp.Register;
import com.nst.jiazheng.api.resp.Resp;
import com.nst.jiazheng.api.resp.ServeType;
import com.nst.jiazheng.api.resp.UpFile;
import com.nst.jiazheng.base.BaseToolBarActivity;
import com.nst.jiazheng.base.GlideEngine;
import com.nst.jiazheng.base.Layout;
import com.nst.jiazheng.base.SpUtil;
import com.nst.jiazheng.login.LoginActivity;
import com.nst.jiazheng.user.PhotoWindow;
import com.nst.jiazheng.worker.widget.ConfirmWindow;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;

/**
 * 创建者     彭龙
 * 创建时间   2020/4/29 10:20 AM
 * 描述
 * <p>
 * 更新者     $
 * 更新时间   $
 * 更新描述
 */
@Layout(layoutId = R.layout.activity_complain)
public class ComplainActivity extends BaseToolBarActivity {
    @BindView(R.id.content)
    EditText content;
    @BindView(R.id.piclist)
    RecyclerView piclist;
    @BindView(R.id.submit)
    Button submit;
    private List<UpFile> mUpFiles;
    private PicAdapter mAdapter;
    private Register mUserInfo;
    private String mId;

    @Override
    protected void init() {
        setTitle("意见反馈");
        mUserInfo = (Register) SpUtil.readObj("userInfo");
        mId = getIntent().getStringExtra("id");
        mUpFiles = new ArrayList<>();
        mUpFiles.add(new UpFile(null, null));
        GridLayoutManager manager = new GridLayoutManager(this, 4);
        piclist.setLayoutManager(manager);
        mAdapter = new PicAdapter(R.layout.item_pic, mUpFiles);
        piclist.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener((adapter1, view, position) -> {
            if (position == 0) {
                if (mAdapter.getData().size() >= 5) {
                    toast("最多只能上传4张图片");
                    return;
                }
                new PhotoWindow(this).setListener(new PhotoWindow.OnConfirmClickListener() {
                    @Override
                    public void onCam(PhotoWindow confirmWindow) {
                        requestPermissionAndOpenCam();
                        confirmWindow.dismiss();
                    }

                    @Override
                    public void onGallery(PhotoWindow confirmWindow) {
                        requestPermissionAndOpenGallery();
                        confirmWindow.dismiss();
                    }
                }).setPopupGravity(Gravity.BOTTOM).showPopupWindow();
            }
        });
        mAdapter.setOnItemLongClickListener((adapter, view, position) -> {
            if (position != 0) {
                new ConfirmWindow(mContext)
                        .setContent("确认删除该图片吗?", "确认")
                        .setListener((ConfirmWindow window) -> {
                            mAdapter.removeAt(position);
                            window.dismiss();
                        })
                        .setPopupGravity(Gravity.CENTER)
                        .setBackPressEnable(true)
                        .setOutSideDismiss(true)
                        .showPopupWindow();
            }
            return true;
        });
        submit.setOnClickListener(view -> {
            feedback();
        });
    }

    private void feedback() {
        String content = this.content.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            toast("请输入投诉内容");
            return;
        }
        String id = "";
        for (UpFile upFile : mUpFiles
        ) {
            if (!TextUtils.isEmpty(upFile.id)) {
                id += upFile.id + ",";
            }
        }
        if (id.endsWith(",")) {
            id = id.substring(0, id.length() - 1);
        }
        showDialog("正在提交", true);
        OkGo.<String>post(Api.userApi).params("api_name", "feedback")
                .params("token", mUserInfo.token)
                .params("order_id", mId)
                .params("content", content)
                .params("img", id).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                dismissDialog();
                Resp resp = new Gson().fromJson(response.body(), Resp.class);
                toast(resp.msg);
                if (resp.code == 1) {
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


    private void requestPermissionAndOpenCam() {
        new RxPermissions(this)
                .requestEach(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe((p) -> {
                    if (p.granted) {
                        openCam();
                    } else if (p.shouldShowRequestPermissionRationale) {
                        requestPermissionAndOpenCam();
                    } else {
                        getAppDetailSettingIntent();
                    }
                });
    }

    private void requestPermissionAndOpenGallery() {
        new RxPermissions(this)
                .requestEach(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe((p) -> {
                    if (p.granted) {
                        openGallery();
                    } else if (p.shouldShowRequestPermissionRationale) {
                        requestPermissionAndOpenGallery();
                    } else {
                        getAppDetailSettingIntent();
                    }
                });
    }

    private void openGallery() {
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())
                .loadImageEngine(GlideEngine.createGlideEngine())
                .maxSelectNum(5 - mAdapter.getData().size())
                .forResult(new OnResultCallbackListener<LocalMedia>() {
                    @Override
                    public void onResult(List<LocalMedia> result) {
                        for (LocalMedia localMedia : result
                        ) {
                            if (Build.VERSION.SDK_INT == 29) {
                                upLoadFiles(localMedia.getAndroidQToPath());
                            } else {
                                upLoadFiles(localMedia.getPath());
                            }
                        }
                    }

                    @Override
                    public void onCancel() {
                        // onCancel Callback
                    }
                });
    }

    private void openCam() {
        PictureSelector.create(this)
                .openCamera(PictureMimeType.ofImage())
                .loadImageEngine(GlideEngine.createGlideEngine())
                .forResult(new OnResultCallbackListener<LocalMedia>() {
                    @Override
                    public void onResult(List<LocalMedia> result) {
                        LocalMedia localMedia = result.get(0);
                        if (Build.VERSION.SDK_INT == 29) {
                            upLoadFiles(localMedia.getAndroidQToPath());
                        } else {
                            upLoadFiles(localMedia.getPath());
                        }
                    }

                    @Override
                    public void onCancel() {
                        // onCancel Callback
                    }
                });
    }

    private void upLoadFiles(String path) {
        showDialog("正在上传", true);
        OkGo.<String>post(Api.baseApi).params("api_name", "uploadPic").params("img", new File(path)).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                dismissDialog();
                Resp<UpFile> resp = new Gson().fromJson(response.body(), new TypeToken<Resp<UpFile>>() {
                }.getType());
                toast(resp.msg);
                if (resp.code == 1) {
                    mAdapter.addData(new UpFile(resp.data.id, path));
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


    class PicAdapter extends BaseQuickAdapter<UpFile, BaseViewHolder> {

        public PicAdapter(int layoutResId, List<UpFile> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder baseViewHolder, UpFile upfile) {
            ImageView pic = baseViewHolder.getView(R.id.pic);
            int position = baseViewHolder.getLayoutPosition();
            if (position == 0) {
                pic.setImageResource(R.mipmap.ic_upfile);
            } else {
                try {
                    Glide.with(ComplainActivity.this).load(upfile.path).centerCrop().into(pic);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}