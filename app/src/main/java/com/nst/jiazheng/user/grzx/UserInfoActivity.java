package com.nst.jiazheng.user.grzx;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.nst.jiazheng.NickNameActivity;
import com.nst.jiazheng.R;
import com.nst.jiazheng.SexWindow;
import com.nst.jiazheng.api.Api;
import com.nst.jiazheng.api.resp.Register;
import com.nst.jiazheng.api.resp.Resp;
import com.nst.jiazheng.api.resp.UpFile;
import com.nst.jiazheng.api.resp.UserCenter;
import com.nst.jiazheng.base.BaseToolBarActivity;
import com.nst.jiazheng.base.GlideEngine;
import com.nst.jiazheng.base.Layout;
import com.nst.jiazheng.base.SpUtil;
import com.nst.jiazheng.login.LoginActivity;
import com.nst.jiazheng.user.PhotoWindow;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.Nullable;
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
@Layout(layoutId = R.layout.activity_userinfo)
public class UserInfoActivity extends BaseToolBarActivity {
    @BindView(R.id.ll_sex)
    LinearLayout mSelectSex;
    @BindView(R.id.tv_sex)
    TextView mTvSex;
    @BindView(R.id.tv_mobile)
    TextView mTvMobile;
    @BindView(R.id.tv_name)
    TextView mTvName;
    @BindView(R.id.tv_is_certification)
    TextView mTvIsCertification;
    @BindView(R.id.iv_avatar)
    CircleImageView mIvAvatar;

    private Register mUserInfo;

    @Override
    protected void init() {
        setTitle("个人资料");
        mUserInfo = (Register) SpUtil.readObj("userInfo");
        getUserInfo();
    }

    private void getUserInfo() {
        OkGo.<String>post(Api.userApi)
                .params("api_name", "center")
                .params("token", mUserInfo.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Resp<UserCenter> resp = new Gson().fromJson(response.body(),
                                new TypeToken<Resp<UserCenter>>() {
                                }.getType());
                        if (resp.code == 1) {
                            setData(resp.data);
                        } else if (resp.code == 101) {
                            SpUtil.putBoolean("isLogin", false);
                            startAndClearAll(LoginActivity.class);
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                    }
                });
    }

    private void setData(UserCenter data) {
        mTvMobile.setText(data.mobile);
        mTvName.setText(data.nickname);
        mTvName.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putString("nickname", data.nickname);
            overlayForResult(NickNameActivity.class, 1, bundle);

        });
        mTvIsCertification.setText(data.is_certification == 1 ? "已认证" : "未认证");
        if (data.is_certification != 1) {
            mTvIsCertification.setOnClickListener(view -> {
                overlay(CertificationActivity.class);
            });
        }
        mTvSex.setText(data.sex == 0 ? "未知" : data.sex == 1 ? "男" : "女");
        mSelectSex.setOnClickListener(view -> new SexWindow(this).setListener((confirmWindow, sex, 男) -> {
            HashMap<String, String> map = new HashMap<>();
            map.put("sex", String.valueOf(sex));
            updateUserInfo(map);
        }).setPopupGravity(Gravity.BOTTOM).showPopupWindow());
        mIvAvatar.setOnClickListener(view -> new PhotoWindow(this).setListener(new PhotoWindow.OnConfirmClickListener() {
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
        }).setPopupGravity(Gravity.BOTTOM).showPopupWindow());
        try {
            Glide.with(this)
                    .load(data.headimgurl)
                    .error(R.mipmap.ic_tx)
                    .into(mIvAvatar);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                    HashMap<String, String> map = new HashMap<>();
                    map.put("headimgurl", resp.data.id);
                    updateUserInfo(map);
                    try {
                        Glide.with(UserInfoActivity.this).load(path).centerCrop().into(mIvAvatar);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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

    private void updateUserInfo(HashMap<String, String> info) {
        HashMap<String, String> map = new HashMap<>();
        map.put("api_name", "edit_user");
        map.put("token", mUserInfo.token);
        map.putAll(info);
        showDialog("正在提交", true);
        OkGo.<String>post(Api.userApi)
                .params(map)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        dismissDialog();
                        Resp resp = new Gson().fromJson(response.body(), new TypeToken<Resp>() {
                        }.getType());
                        toast(resp.msg);
                        if (resp.code == 1) {
                            getUserInfo();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            String nickname = data.getStringExtra("nickname");
            HashMap<String, String> map = new HashMap<>();
            map.put("nickname", nickname);
            updateUserInfo(map);
        }
    }
}
