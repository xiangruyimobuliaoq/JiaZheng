package com.nst.jiazheng.user.grzx;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import com.baidu.idl.face.platform.FaceSDKManager;
import com.baidu.idl.face.platform.utils.Base64Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.nst.jiazheng.R;
import com.nst.jiazheng.api.Api;
import com.nst.jiazheng.api.resp.Register;
import com.nst.jiazheng.api.resp.Resp;
import com.nst.jiazheng.api.resp.UpFile;
import com.nst.jiazheng.base.BaseToolBarActivity;
import com.nst.jiazheng.base.Layout;
import com.nst.jiazheng.base.SpUtil;
import com.nst.jiazheng.face.Config;
import com.nst.jiazheng.face.FaceDetectExpActivity;
import com.nst.jiazheng.face.FaceLivenessExpActivity;
import com.nst.jiazheng.login.LoginActivity;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import androidx.annotation.Nullable;
import butterknife.BindView;

/**
 * 创建者     彭龙
 * 创建时间   2020/5/6 3:51 PM
 * 描述
 * <p>
 * 更新者     $
 * 更新时间   $
 * 更新描述
 */
@Layout(layoutId = R.layout.activity_certification)
public class CertificationActivity extends BaseToolBarActivity {
    @BindView(R.id.name)
    EditText name;
    @BindView(R.id.cerNum)
    EditText cerNum;
    @BindView(R.id.submit)
    Button submit;
    private Register mUserInfo;

    @Override
    protected void init() {
        mUserInfo = (Register) SpUtil.readObj("userInfo");
        setTitle("实名认证");
        submit.setOnClickListener(view -> {
            checkIdAndName();
        });
    }

    private void checkIdAndName() {
        String name = this.name.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            toast("请输入姓名");
            return;
        }
        String cerNum = this.cerNum.getText().toString().trim();
        if (TextUtils.isEmpty(cerNum)) {
            toast("请输入身份证号码");
            return;
        }
        showDialog("认证中", true);
        OkGo.<String>post(Api.certificationApi).params("api_name", "idmatch")
                .params("token", mUserInfo.token)
                .params("id_card", cerNum)
                .params("name", name)
                .params("flag", "app").execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                dismissDialog();
                Resp resp = new Gson().fromJson(response.body(), Resp.class);
                toast(resp.msg);
                if (resp.code == 1) {
                    requestAndFaceDetect();
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

    private void PoliceCheck(String id) {
        String name = this.name.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            toast("请输入姓名");
            return;
        }
        String cerNum = this.cerNum.getText().toString().trim();
        if (TextUtils.isEmpty(cerNum)) {
            toast("请输入身份证号码");
            return;
        }
        showDialog("认证中", true);
        OkGo.<String>post(Api.certificationApi).params("api_name", "personVerify")
                .params("token", mUserInfo.token)
                .params("id_card", cerNum)
                .params("name", name)
                .params("img", id).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                dismissDialog();
                Resp resp = new Gson().fromJson(response.body(), Resp.class);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                upLoadFiles(saveBitmapFile(base64ToBitmap(data.getStringExtra("img"))));
            }
        }
    }

    private void upLoadFiles(File file) {
        showDialog("正在上传", true);
        OkGo.<String>post(Api.baseApi).params("api_name", "uploadPic").params("img", file).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                dismissDialog();
                Resp<UpFile> resp = new Gson().fromJson(response.body(), new TypeToken<Resp<UpFile>>() {
                }.getType());
                toast(resp.msg);
                if (resp.code == 1) {
                    PoliceCheck(resp.data.id);
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

    private static Bitmap base64ToBitmap(String base64Data) {
        byte[] bytes = Base64Utils.decode(base64Data, Base64Utils.NO_WRAP);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public File saveBitmapFile(Bitmap bitmap) {
        File file = new File(getCacheDir(), "faceImg.jpg");//将要保存图片的路径
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
            return file;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    void requestAndFaceLive() {
        new RxPermissions(this)
                .requestEach(Manifest.permission.CAMERA)
                .subscribe((p) -> {
                    if (p.granted) {
                        FaceSDKManager.getInstance().initialize(this, Config.licenseID, Config.licenseFileName);
                        startActivityForResult(new Intent(this, FaceLivenessExpActivity.class), 1);
                    } else if (p.shouldShowRequestPermissionRationale) {
                        requestAndFaceLive();
                    } else {
                        getAppDetailSettingIntent();
                    }
                });
    }

    void requestAndFaceDetect() {
        new RxPermissions(this)
                .requestEach(Manifest.permission.CAMERA)
                .subscribe((p) -> {
                    if (p.granted) {
                        FaceSDKManager.getInstance().initialize(this, Config.licenseID, Config.licenseFileName);
                        startActivityForResult(new Intent(this, FaceDetectExpActivity.class), 1);
                    } else if (p.shouldShowRequestPermissionRationale) {
                        requestAndFaceDetect();
                    } else {
                        getAppDetailSettingIntent();
                    }
                });
    }
}
