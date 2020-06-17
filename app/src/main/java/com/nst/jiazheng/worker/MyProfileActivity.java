package com.nst.jiazheng.worker;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

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
import com.nst.jiazheng.NickNameActivity;
import com.nst.jiazheng.R;
import com.nst.jiazheng.SexWindow;
import com.nst.jiazheng.api.Api;
import com.nst.jiazheng.api.resp.Addr;
import com.nst.jiazheng.api.resp.Order;
import com.nst.jiazheng.api.resp.Register;
import com.nst.jiazheng.api.resp.Resp;
import com.nst.jiazheng.api.resp.UpFile;
import com.nst.jiazheng.api.resp.UserCenter;
import com.nst.jiazheng.base.BaseToolBarActivity;
import com.nst.jiazheng.base.GlideEngine;
import com.nst.jiazheng.base.Layout;
import com.nst.jiazheng.base.LogUtil;
import com.nst.jiazheng.base.SpUtil;
import com.nst.jiazheng.base.ToastHelper;
import com.nst.jiazheng.im.ImManager;
import com.nst.jiazheng.login.LoginActivity;
import com.nst.jiazheng.user.PhotoWindow;
import com.nst.jiazheng.worker.utils.GetJsonDataUtil;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.UserInfo;

/**
 * 创建者     彭龙
 * 创建时间   2020/4/2 11:48 AM
 * 描述
 * <p>
 * 更新者     $
 * 更新时间   $
 * 更新描述
 */
@Layout(layoutId = R.layout.activity_my_profile)
public class MyProfileActivity extends BaseToolBarActivity {
    @BindView(R.id.ll_age)
    LinearLayout mSelectAge;
    @BindView(R.id.ll_sex)
    LinearLayout mSelectSex;
    @BindView(R.id.ll_city)
    LinearLayout mSelectCity;
    @BindView(R.id.ll_long)
    LinearLayout mSelectLong;

    @BindView(R.id.tv_age)
    TextView mTvAge;
    @BindView(R.id.tv_sex)
    TextView mTvSex;
    @BindView(R.id.tv_city)
    TextView mTvCity;
    @BindView(R.id.tv_long)
    TextView mTvLong;
    @BindView(R.id.tv_mobile)
    TextView mTvMobile;
    @BindView(R.id.tv_name)
    TextView mTvName;
    @BindView(R.id.tv_is_certification)
    TextView mTvIsCertification;
    @BindView(R.id.iv_avatar)
    CircleImageView mIvAvatar;


    private int REQUEST_AGE = 888;
    private int REQUEST_NICKNAME = 1;
    private List<Addr> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<Addr>> options2Items = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<Addr>>> options3Items = new ArrayList<>();
    private Register mUserInfo;
    private Addr mTotal;

    @Override
    protected void init() {
        setTitle("个人资料");
        mUserInfo = (Register) SpUtil.readObj("userInfo");
//        initJsonData();
        initJsonData1();
        getUserInfo();
    }

    private void getUserInfo() {
        OkGo.<String>post(Api.userApi)
                .params("api_name", "user_info")
                .params("token", mUserInfo.token)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LogUtil.getInstance().d("user_info : " + response.body());
                        Resp<UserCenter> resp = new Gson().fromJson(response.body(),
                                new TypeToken<Resp<UserCenter>>() {
                                }.getType());
                        if (resp.code == 1) {
                            if (RongIMClient.getInstance().getCurrentConnectionStatus() != RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTED) {
                                ImManager.connect(mUserInfo.ry_token, resp.data);
                            } else {
                                UserInfo userInfo = new UserInfo(resp.data.id, resp.data.nickname, Uri.parse(resp.data.headimgurl));
                                RongIM.getInstance().refreshUserInfoCache(userInfo);
                            }
                            setData(resp.data);
                        } else if (resp.code == 101) {
                            SpUtil.putBoolean("isLogin", false);
                            startAndClearAll(LoginActivity.class);
                        }
                        toast(resp.msg);
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                    }
                });
    }

    private void setData(UserCenter data) {
        mTvAge.setText(data.age + "岁");
        mTvMobile.setText(data.mobile);
        mTvName.setText(data.nickname);
        mTvIsCertification.setText(data.is_certification == 1 ? "已认证" : "未认证");
        mTvSex.setText(data.sex == 0 ? "未知" : data.sex == 1 ? "男" : "女");
        mTvCity.setText(data.province + data.city + data.area + data.address);
        mTvLong.setText(data.job_age == 0.5 ? "半年" : data.job_age + "年");
        mTvName.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putString("nickname", data.nickname);
            overlayForResult(NickNameActivity.class, REQUEST_NICKNAME, bundle);

        });
        mSelectAge.setOnClickListener(view -> overlayForResult(InputAgeActivity.class, REQUEST_AGE));
        mSelectSex.setOnClickListener(view -> new SexWindow(this).setListener((confirmWindow, sex, sexText) -> {
            HashMap<String, String> map = new HashMap<>();
            map.put("sex", String.valueOf(sex));
            updateUserInfo(map);

        }).setPopupGravity(Gravity.BOTTOM).showPopupWindow());
        mSelectCity.setOnClickListener(view -> showCityPickerView());
        mSelectLong.setOnClickListener(view -> showLongTimePicker());
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
                    HashMap<String, String> map = new HashMap<>();
                    map.put("headimgurl", resp.data.id);
                    updateUserInfo(map);
                    try {
                        Glide.with(MyProfileActivity.this).load(path).centerCrop().into(mIvAvatar);
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
        OkGo.<String>post(Api.userApi)
                .params(map)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LogUtil.getInstance().d("edit_user : " + response.body());
                        Resp<Order> resp = new Gson().fromJson(response.body(),
                                new TypeToken<Resp<Order>>() {
                                }.getType());
                        if (resp.code == 1) {
                            getUserInfo();
                        } else if (resp.code == 101) {
                            SpUtil.putBoolean("isLogin", false);
                            startAndClearAll(LoginActivity.class);
                        }
                        ToastHelper.showToast(resp.msg, mContext);
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_AGE) {
            if (resultCode == InputAgeActivity.RESULT_CODE) {
                int age = data.getIntExtra(InputAgeActivity.INPUT_AGE, 18);
                mTvAge.setText(age + "");
                HashMap<String, String> map = new HashMap<>();
                map.put("age", String.valueOf(age));
                updateUserInfo(map);
            }
        } else if (requestCode == REQUEST_NICKNAME) {
            if (resultCode == RESULT_OK) {
                String nickname = data.getStringExtra("nickname");
                HashMap<String, String> map = new HashMap<>();
                map.put("nickname", nickname);
                updateUserInfo(map);
            }
        }
    }

    private void initJsonData1() {//解析数据
        String JsonData = new GetJsonDataUtil().getJson(this, "location.json");//获取assets目录下的json文件数据
        ArrayList<Addr> addrs = new Gson().fromJson(JsonData, new TypeToken<ArrayList<Addr>>() {
        }.getType());
        mTotal = new Addr();
        for (Addr addr : addrs
        ) {
            if (addr.pid == 0) {
                mTotal.mAddrs.add(addr);
                continue;
            }
            for (Addr addr1 : addrs
            ) {
                if (addr1.id == addr.pid) {
                    addr1.mAddrs.add(addr);
                    break;
                }
            }
        }
        options1Items = mTotal.mAddrs;
        for (int i = 0; i < mTotal.mAddrs.size(); i++) {//遍历省份
            ArrayList<Addr> cityList = new ArrayList<>();//该省的城市列表（第二级）
            ArrayList<ArrayList<Addr>> province_AreaList = new ArrayList<>();//该省的所有地区列表（第三极）
            for (int c = 0; c < mTotal.mAddrs.get(i).mAddrs.size(); c++) {//遍历该省份的所有城市
                Addr cityName = mTotal.mAddrs.get(i).mAddrs.get(c);
                cityList.add(cityName);//添加城市
                ArrayList<Addr> city_AreaList = new ArrayList<>();//该城市的所有地区列表
                //如果无地区数据，建议添加空字符串，防止数据为null 导致三个选项长度不匹配造成崩溃
                /*if (jsonBean.get(i).getCityList().get(c).getArea() == null
                        || jsonBean.get(i).getCityList().get(c).getArea().size() == 0) {
                    city_AreaList.add("");
                } else {
                    city_AreaList.addAll(jsonBean.get(i).getCityList().get(c).getArea());
                }*/
                city_AreaList.addAll(mTotal.mAddrs.get(i).mAddrs.get(c).mAddrs);
                province_AreaList.add(city_AreaList);//添加该省所有地区数据
            }
            /**
             * 添加城市数据
             */
            options2Items.add(cityList);
            /**
             * 添加地区数据
             */
            options3Items.add(province_AreaList);
        }
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
            mTvLong.setText(tx);
            HashMap<String, String> map = new HashMap<>();
            if (tx.equals("半年")) {
                tx = "0.5年";
            }
            map.put("job_age", tx.replaceAll("年", ""));
            updateUserInfo(map);
        })
                .setCancelColor(Color.GRAY)
                .setContentTextSize(20)
                .build();
        pvOptions.setPicker(options1Items);
        pvOptions.show();
    }


    private void showCityPickerView() {
        OptionsPickerView pvOptions = new OptionsPickerBuilder(this, (options1, options2, options3, v) -> {
            //返回的分别是三个级别的选中位置
            String opt1tx = options1Items.size() > 0 ?
                    options1Items.get(options1).getPickerViewText() : "";

            String opt2tx = options2Items.size() > 0
                    && options2Items.get(options1).size() > 0 ?
                    options2Items.get(options1).get(options2).name : "";

            String opt3tx = options2Items.size() > 0
                    && options3Items.get(options1).size() > 0
                    && options3Items.get(options1).get(options2).size() > 0 ?
                    options3Items.get(options1).get(options2).get(options3).name : "";

            String tx = opt1tx + opt2tx + opt3tx;
            mTvCity.setText(tx);
            HashMap<String, String> map = new HashMap<>();
            map.put("province", opt1tx);
            map.put("city", opt2tx);
            map.put("area", opt3tx);
            updateUserInfo(map);
        }).setTitleText("城市选择")
                .setCancelColor(Color.GRAY)
                .setDividerColor(Color.BLACK)
                .setTextColorCenter(Color.BLACK) //设置选中项文字颜色
                .setContentTextSize(20)
                .build();
        pvOptions.setPicker(options1Items, options2Items, options3Items);//三级选择器
        pvOptions.show();
    }

}
