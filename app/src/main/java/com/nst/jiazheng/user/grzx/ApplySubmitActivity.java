package com.nst.jiazheng.user.grzx;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.view.OptionsPickerView;
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
import com.nst.jiazheng.worker.InputAgeActivity;
import com.nst.jiazheng.worker.widget.ConfirmWindow;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
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
    @BindView(R.id.mobile)
    TextView mobile;
    @BindView(R.id.age)
    TextView age;
    @BindView(R.id.address)
    EditText address;
    @BindView(R.id.sex)
    TextView sex;
    @BindView(R.id.job_age)
    TextView job_age;
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.submit)
    Button submit;
    private String currentPicId;
    private Register mUserInfo;
    private int REQUEST_AGE = 888;
    private int currentSex;

    @Override
    protected void init() {
        setTitle("私人管家认证");
        mUserInfo = (Register) SpUtil.readObj("userInfo");
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
        age.setOnClickListener(view -> overlayForResult(InputAgeActivity.class, REQUEST_AGE));
        job_age.setOnClickListener(view -> showLongTimePicker());
        sex.setOnClickListener(view -> new SexWindow(this).setListener((confirmWindow, sex, sexText) -> {
            currentSex = sex;
            this.sex.setText(sexText);
        }).setPopupGravity(Gravity.BOTTOM).showPopupWindow());
        getPrivateInfo();
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                applySubmit();
            }
        });
    }

    private void applySubmit() {
        if (currentSex == 0) {
            toast("请选择性别");
            return;
        }
        if (null == currentPicId) {
            toast("请上传手持身份证照");
            return;
        }
        String trim1 = address.getText().toString().trim();
        if (TextUtils.isEmpty(trim1)) {
            toast("请填写地址");
            return;
        }
        showDialog("正在提交认证", true);
        String trim = job_age.getText().toString().trim();
        if (trim.equals("半年")) {
            trim = "0.5年";
        }

        OkGo.<String>post(Api.userApi).params("api_name", "private_sublimit")
                .params("token", mUserInfo.token)
                .params("mobile", mobile.getText().toString().trim())
                .params("name", name.getText().toString().trim())
                .params("address", trim1)
                .params("sex", currentSex)
                .params("id_card_img", currentPicId)
                .params("age", age.getText().toString().trim().replaceAll("岁", ""))
                .params("job_age", trim.replaceAll("年", ""))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        dismissDialog();
                        Resp<UserCenter> resp = new Gson().fromJson(response.body(), new TypeToken<Resp<UserCenter>>() {
                        }.getType());
                        toast(resp.msg);
                        if (resp.code == 1) {
                            setResult(RESULT_OK);
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

    private void showLongTimePicker() {
        List<String> options1Items = new ArrayList<>();
        options1Items.add("半年");
        for (int i = 0; i < 30; i++) {
            options1Items.add((i + 1) + "年");
        }
        //条件选择器
        OptionsPickerView pvOptions = new OptionsPickerBuilder(mContext, (options1, option2, options3, v) -> {
            String tx = options1Items.get(options1);
            job_age.setText(tx);
        })
                .setCancelColor(Color.GRAY)
                .setContentTextSize(20)
                .build();
        pvOptions.setPicker(options1Items);
        pvOptions.show();
    }

    private void getPrivateInfo() {
        OkGo.<String>post(Api.userApi).params("api_name", "private_info").params("token", mUserInfo.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Resp<UserCenter> resp = new Gson().fromJson(response.body(), new TypeToken<Resp<UserCenter>>() {
                        }.getType());
                        toast(resp.msg);
                        if (resp.code == 1) {
                            setData(resp.data);
                        } else if (resp.code == 101) {
                            SpUtil.putBoolean("isLogin", false);
                            startAndClearAll(LoginActivity.class);
                        }
                    }
                });
    }

    private void setData(UserCenter data) {
        name.setText(data.name);
        mobile.setText(data.mobile);
        if (data.sex == 0) {
            sex.setText("未知");
        } else if (data.sex == 1) {
            sex.setText("男");
        } else if (data.sex == 2) {
            sex.setText("女");
        }
        currentSex = data.sex;
        age.setText(data.age);
        job_age.setText(data.job_age + "年");
        address.setText(data.address);
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
        if (requestCode == REQUEST_AGE) {
            if (resultCode == InputAgeActivity.RESULT_CODE) {
                int age = data.getIntExtra(InputAgeActivity.INPUT_AGE, 0);
                this.age.setText(age + "");
            }
        }
    }
}
