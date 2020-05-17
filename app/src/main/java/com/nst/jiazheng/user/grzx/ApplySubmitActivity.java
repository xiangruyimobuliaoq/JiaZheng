package com.nst.jiazheng.user.grzx;

import android.Manifest;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
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
import com.nst.jiazheng.api.resp.Resp;
import com.nst.jiazheng.api.resp.UpFile;
import com.nst.jiazheng.base.BaseToolBarActivity;
import com.nst.jiazheng.base.GlideEngine;
import com.nst.jiazheng.base.Layout;
import com.nst.jiazheng.base.SpUtil;
import com.nst.jiazheng.login.LoginActivity;
import com.nst.jiazheng.user.PhotoWindow;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.util.List;

import butterknife.BindView;

/**
 * 创建者     彭龙
 * 创建时间   2020/5/6 4:14 PM
 * 描述
 * <p>
 * 更新者     $
 * 更新时间   $
 * 更新描述
 */
@Layout(layoutId = R.layout.activity_applysubmit)
public class ApplySubmitActivity extends BaseToolBarActivity {
    @BindView(R.id.takepicture)
    ImageView takepicture;
    private String currentPicId;

    @Override
    protected void init() {
        setTitle("私人管家认证");
        takepicture.setOnClickListener(view -> {
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
                .maxSelectNum(1)
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
                    currentPicId = resp.data.id;
                    try {
                        Glide.with(ApplySubmitActivity.this).load(path).centerCrop().into(takepicture);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else if (resp.code == 101) {
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
